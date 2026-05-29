package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.*;
import com.student.onlinebookstore.dto.request.CreateOrderRequest;
import com.student.onlinebookstore.dto.response.*;
import com.student.onlinebookstore.exception.InvalidCredentialsException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.exception.OutOfStockException;
import com.student.onlinebookstore.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    private OrderDAO orderDAO;
    private CartDAO cartDAO;
    private BookDAO bookDAO;
    private AddressDAO addressDAO;
    private PaymentDAO paymentDAO;
    private DiscountService discountService;
    private NotificationService notificationService;
    @Autowired 
    public OrderServiceImpl(OrderDAO orderDAO, CartDAO cartDAO, BookDAO bookDAO,
                           AddressDAO addressDAO, PaymentDAO paymentDAO,
                           DiscountService discountService, NotificationService notificationService) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.bookDAO = bookDAO;
        this.addressDAO = addressDAO;
        this.paymentDAO = paymentDAO;
        this.discountService = discountService;
        this.notificationService = notificationService;
    }
    
    @Override
    public OrderResponse createOrder(Integer userId, CreateOrderRequest request) {
        logger.info("Creating order for user: {}", userId);
        
        // Get cart
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) {
            throw new InvalidCredentialsException("Empty cart for user: " + userId);
        }
        
        List<CartItem> cartItems = cartDAO.getCartItems(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new InvalidCredentialsException("Empty cart for user: " + userId);
        }
        
        // Get address
        Address address = addressDAO.getAddressById(request.getAddressId());
        if (address == null) {
            throw new ResourceNotFoundException("Address not found for user: " + userId);
        }
        
        // Calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            Book book = bookDAO.getBookById(cartItem.getBook().getBookId());
            
            // Check stock
            if (book.getStockQuantity() < cartItem.getQuantity()) {
                throw new OutOfStockException("Book \"" + book.getTitle() + "\" is out of stock");
            }
            
            BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            
            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTime(book.getPrice());
            orderItems.add(orderItem);
        }
        
        // Apply discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        Discount discount = null;
        if (request.getDiscountCode() != null && !request.getDiscountCode().isEmpty()) {
            discount = discountService.validateDiscount(request.getDiscountCode());
            if (discount != null) {
                discountAmount = discountService.calculateDiscount(totalAmount, discount);
                discountService.applyDiscount(discount.getDiscountId());
            }
        }
        
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        
        // Create order
        Order order = new Order();
        User user = new User();
        user.setUserId(userId);
        order.setUser(user);
        order.setAddress(address);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        order.setStatus(Order.OrderStatus.pending);
        order.setPaymentStatus(Order.PaymentStatus.unpaid);
        
        int orderId = orderDAO.createOrder(order);
        if (orderId == -1) {
            throw new RuntimeException("Failed to create order");
        }
        
        // Add order items and decrease stock
        for (OrderItem item : orderItems) {
            orderDAO.addOrderItem(orderId, item);
            bookDAO.decreaseStock(item.getBook().getBookId(), item.getQuantity());
        }
        
        // Create payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setPaymentStatus(Payment.PaymentStatus.pending);
        paymentDAO.createPayment(payment);
        
        // Clear cart
        cartDAO.clearCart(cart.getCartId());
        
        // Send notification
        notificationService.sendOrderStatusNotification(userId, orderId, "pending");
        
        logger.info("Order created successfully with id: {}", orderId);
        
        // Build response
        return buildOrderResponse(orderId);
    }
    
    @Override
    public OrderResponse getOrderById(Integer orderId) {
        logger.info("Getting order by id: {}", orderId);
        
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Could not find order with id: " + orderId);
        }
        
        return buildOrderResponse(orderId);
    }
    
    @Override
    public PaginationResponse getUserOrders(Integer userId, int page, int size) {
        logger.info("Getting orders for user: {}, page: {}, size: {}", userId, page, size);
        
        List<Order> orders = orderDAO.getOrdersByUserId(userId, page, size);
        int total = orderDAO.countOrdersByUserId(userId);
        
        List<OrderResponse> responses = orders.stream()
            .map(order -> buildOrderResponse(order.getOrderId()))
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public boolean cancelOrder(Integer orderId) {
        logger.info("Cancelling order: {}", orderId);
        
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Could not find order with id: " + orderId);
        }
        
        if (!"pending".equals(order.getStatus().name())) {
            throw new InvalidCredentialsException("Only pending orders can be cancelled");
        }
        
        // Restore stock
        List<OrderItem> items = orderDAO.getOrderItems(orderId);
        for (OrderItem item : items) {
            Book book = item.getBook();
            bookDAO.updateStock(book.getBookId(), book.getStockQuantity() + item.getQuantity());
        }
        
        boolean cancelled = orderDAO.cancelOrder(orderId);
        
        if (cancelled) {
            notificationService.sendOrderStatusNotification(
                order.getUser().getUserId(), orderId, "cancelled"
            );
        }
        
        return cancelled;
    }
    
    @Override
    public PaginationResponse getAllOrders(int page, int size) {
        logger.info("Getting all orders - page: {}, size: {}", page, size);
        
        List<Order> orders = orderDAO.getAllOrders(page, size);
        int total = orderDAO.countAllOrders();
        
        List<OrderResponse> responses = orders.stream()
            .map(order -> buildOrderResponse(order.getOrderId()))
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public PaginationResponse getOrdersByStatus(String status, int page, int size) {
        logger.info("Getting orders by status: {}, page: {}, size: {}", status, page, size);
        
        List<Order> orders = orderDAO.getOrdersByStatus(status, page, size);
        int total = orderDAO.countOrdersByStatus(status);
        
        List<OrderResponse> responses = orders.stream()
            .map(order -> buildOrderResponse(order.getOrderId()))
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public OrderResponse updateOrderStatus(Integer orderId, String status) {
        logger.info("Updating order status - orderId: {}, status: {}", orderId, status);
        
        boolean updated = orderDAO.updateOrderStatus(orderId, status);
        if (!updated) {
            throw new RuntimeException("Failed to update order status");
        }
        
        // Send notification
        Order order = orderDAO.getOrderById(orderId);
        notificationService.sendOrderStatusNotification(
            order.getUser().getUserId(), orderId, status
        );
        
        return buildOrderResponse(orderId);
    }
    
    @Override
    public OrderResponse updatePaymentStatus(Integer orderId, String paymentStatus) {
        logger.info("Updating payment status - orderId: {}, paymentStatus: {}", orderId, paymentStatus);
        
        boolean updated = orderDAO.updatePaymentStatus(orderId, paymentStatus);
        if (!updated) {
            throw new RuntimeException("Failed to update payment status");
        }
        
        return buildOrderResponse(orderId);
    }
    
    @Override
    public long countTotalOrders() {
        return orderDAO.countAllOrders();
    }
    
    @Override
    public long countOrdersByStatus(String status) {
        return orderDAO.countOrdersByStatus(status);
    }
    
    private OrderResponse buildOrderResponse(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        Address address = order.getAddress();
        List<OrderItem> items = orderDAO.getOrderItems(orderId);
        Payment payment = paymentDAO.getPaymentByOrderId(orderId);
        
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setOrderDate(order.getOrderDate());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setFinalAmount(order.getFinalAmount());
        response.setStatus(order.getStatus().name());
        response.setPaymentStatus(order.getPaymentStatus().name());
        response.setUserId(order.getUser().getUserId());
        // Set address
        AddressResponse addressResponse = new AddressResponse(
            address.getAddressId(), address.getFullName(), address.getPhone(),
            address.getAddressLine(), address.getWard(), address.getDistrict(),
            address.getCity(), address.getIsDefault()
        );
        response.setAddress(addressResponse);
        
        // Set order items
        List<OrderItemResponse> itemResponses = items.stream()
            .map(item -> {
                OrderItemResponse itemResp = new OrderItemResponse(
                    item.getOrderItemId(), item.getBook().getBookId(),
                    item.getBook().getTitle(), item.getBook().getAuthor(),
                    item.getQuantity(), item.getPriceAtTime()
                );
                itemResp.setCoverImageUrl(item.getBook().getCoverImageUrl());
                return itemResp;
            })
            .collect(Collectors.toList());
        response.setItems(itemResponses);
        
        // Set payment
        if (payment != null) {
            PaymentResponse paymentResponse = new PaymentResponse(
                payment.getPaymentId(), payment.getPaymentMethod().name(),
                payment.getPaymentStatus().name(), payment.getTransactionId(),
                payment.getPaymentDate()
            );
            response.setPayment(paymentResponse);
        }
        
        return response;
    }
}