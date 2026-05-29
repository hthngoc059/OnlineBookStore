package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.config.ApplicationContextProvider;
import com.student.onlinebookstore.dao.AddressDAO;
import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.CartDAO;
import com.student.onlinebookstore.dao.DiscountDAO;
import com.student.onlinebookstore.dao.NotificationDAO;
import com.student.onlinebookstore.dao.OrderDAO;
import com.student.onlinebookstore.dao.PaymentDAO;
import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.dto.request.CreateOrderRequest;
import com.student.onlinebookstore.dto.response.OrderResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.exception.InvalidCredentialsException;
import com.student.onlinebookstore.exception.OutOfStockException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.Address;
import com.student.onlinebookstore.model.Cart;
import com.student.onlinebookstore.model.CartItem;
import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(urlPatterns = {"/checkout", "/orders", "/orders/*"})
public class OrderController extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private OrderService  orderService;
    private CartDAO       cartDAO;
    private AddressDAO    addressDAO;

    @Override
    public void init() {
        this.orderService = ApplicationContextProvider.getBean(OrderService.class);
        this.cartDAO      = ApplicationContextProvider.getBean(CartDAO.class);
        this.addressDAO   = ApplicationContextProvider.getBean(AddressDAO.class);
    }

    // ------------------------------------------------------------------ //
    //  GET
    // ------------------------------------------------------------------ //
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect="
                    + URLEncoder.encode(req.getRequestURI(), StandardCharsets.UTF_8));
            return;
        }

        String path = req.getServletPath();   // "/checkout" | "/orders"
        String info = req.getPathInfo();       // null | "/123"

        switch (path) {
            case "/checkout":
                showCheckout(req, resp, user);
                break;
            case "/orders":
                if (info != null && info.length() > 1) {
                    showOrderDetail(req, resp, user, info.substring(1));
                } else {
                    showOrderHistory(req, resp, user);
                }
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/orders");
        }
    }

    // ------------------------------------------------------------------ //
    //  POST
    // ------------------------------------------------------------------ //
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        String info = req.getPathInfo(); // e.g. "/123/cancel"

        if ("/checkout".equals(path)) {
            handlePlaceOrder(req, resp, user);

        } else if ("/orders".equals(path) || info != null) {
            if (info != null && info.contains("/cancel")) {
                handleCancelOrder(req, resp, user, info);
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Trang Checkout
    // ------------------------------------------------------------------ //
    private void showCheckout(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        Cart cart = cartDAO.getCartByUserId(user.getUserId());
        if (cart == null) {
            req.setAttribute("errorMessage", "Giỏ hàng trống. Hãy thêm sách trước khi đặt hàng.");
            req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
            return;
        }

        List<CartItem> cartItems = cartDAO.getCartItems(cart.getCartId());
        if (cartItems == null || cartItems.isEmpty()) {
            req.setAttribute("errorMessage", "Giỏ hàng trống. Hãy thêm sách trước khi đặt hàng.");
            req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
            return;
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(ci -> ci.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Address> addresses = addressDAO.getAddressesByUserId(user.getUserId());

        req.setAttribute("cartItems",   cartItems);
        req.setAttribute("totalAmount", totalAmount);
        req.setAttribute("addresses",   addresses);
        req.setAttribute("currentUser", user);

        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------ //
    //  Đặt hàng
    // ------------------------------------------------------------------ //
    private void handlePlaceOrder(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        String addressIdStr  = req.getParameter("addressId");
        String paymentMethod = req.getParameter("paymentMethod");
        String discountCode  = req.getParameter("discountCode");

        if (addressIdStr == null || addressIdStr.isBlank()) {
            req.setAttribute("errorMessage", "Vui lòng chọn địa chỉ giao hàng");
            showCheckout(req, resp, user);
            return;
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            req.setAttribute("errorMessage", "Vui lòng chọn phương thức thanh toán");
            showCheckout(req, resp, user);
            return;
        }

        try {
            CreateOrderRequest request = new CreateOrderRequest();
            request.setAddressId(Integer.parseInt(addressIdStr));
            request.setPaymentMethod(paymentMethod);
            request.setDiscountCode(discountCode);

            OrderResponse order = orderService.createOrder(user.getUserId(), request);

            // Xóa giỏ hàng
            Cart cart = cartDAO.getCartByUserId(user.getUserId());
            if (cart != null) {
                cartDAO.clearCart(cart.getCartId());
            }
            req.getSession().setAttribute("cartCount", 0);

            // Redirect sang order detail, truyền success qua URL param
            resp.sendRedirect(req.getContextPath() 
                + "/orders/" + order.getOrderId() + "?success=true");
            return;

        } catch (Exception e) {
            logger.error("Lỗi đặt hàng", e);
            req.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            showCheckout(req, resp, user);
        }
    }

    // ------------------------------------------------------------------ //
    //  Lịch sử đơn hàng
    // ------------------------------------------------------------------ //
    private void showOrderHistory(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        boolean isSuccess = "true".equals(req.getParameter("success"));

        // Reset cartCount
        if (isSuccess) {
            req.getSession().setAttribute("cartCount", 0);
        } else {
            Cart cart = cartDAO.getCartByUserId(user.getUserId());
            int realCount = 0;
            if (cart != null) {
                List<CartItem> items = cartDAO.getCartItems(cart.getCartId());
                realCount = (items != null) ? items.size() : 0;
            }
            req.getSession().setAttribute("cartCount", realCount);
        }

        int page = parseIntParam(req.getParameter("page"), 0);
        int size = 10;
        PaginationResponse pagination = orderService.getUserOrders(user.getUserId(), page, size);

        req.setAttribute("orderSuccess",  isSuccess);  // <-- dùng attribute thay vì param
        req.setAttribute("orders",        pagination.getContent());
        req.setAttribute("currentPage",   pagination.getPage());
        req.setAttribute("totalPages",    pagination.getTotalPages());
        req.setAttribute("currentUser",   user);

        req.getRequestDispatcher("/WEB-INF/views/order-history.jsp").forward(req, resp);
    }
    // ------------------------------------------------------------------ //
    //  Chi tiết đơn hàng
    // ------------------------------------------------------------------ //
    private void showOrderDetail(HttpServletRequest req, HttpServletResponse resp,
                                 User user, String idStr) throws ServletException, IOException {
        try {
            // idStr có thể là "123" hoặc "123/cancel"
            int orderId = Integer.parseInt(idStr.split("/")[0]);
            OrderResponse order = orderService.getOrderById(orderId);
                
            if (order.getUserId() != user.getUserId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "Bạn không có quyền xem đơn hàng này");
                return;
            }
            req.setAttribute("order",      order);
            req.setAttribute("justPlaced", "true".equals(req.getParameter("success")));
            req.setAttribute("currentUser", user);

            req.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/orders");
        } catch (ResourceNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    // ------------------------------------------------------------------ //
    //  Hủy đơn hàng
    // ------------------------------------------------------------------ //
    private void handleCancelOrder(HttpServletRequest req, HttpServletResponse resp,
                                   User user, String info) throws IOException {
        try {
            // info = "/123/cancel"
            String[] parts = info.split("/");
            int orderId = Integer.parseInt(parts.length >= 2 ? parts[1] : "0");

            orderService.cancelOrder(orderId);
            resp.sendRedirect(req.getContextPath() + "/orders/" + orderId + "?cancelled=true");

        } catch (Exception e) {
            logger.warn("Lỗi hủy đơn: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/orders?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute("currentUser"); // 1 key duy nhất
    }

    private int parseIntParam(String value, int defaultVal) {
        try { return value != null ? Integer.parseInt(value) : defaultVal; }
        catch (NumberFormatException e) { return defaultVal; }
    }
}