package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.DiscountDAO;
import com.student.onlinebookstore.dao.OrderDAO;
import com.student.onlinebookstore.dto.request.CreateDiscountRequest;
import com.student.onlinebookstore.dto.response.BookResponse;
import com.student.onlinebookstore.dto.response.DiscountResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.exception.DuplicateResourceException;
import com.student.onlinebookstore.exception.InvalidCredentialsException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.Discount;
import com.student.onlinebookstore.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DiscountServiceImpl implements DiscountService {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscountServiceImpl.class);
    
    private DiscountDAO discountDAO;
    private OrderDAO orderDAO;
    private BookDAO bookDAO;
    
    public DiscountServiceImpl(DiscountDAO discountDAO, OrderDAO orderDAO, BookDAO bookDAO) {
        this.discountDAO = discountDAO;
        this.orderDAO = orderDAO;
        this.bookDAO = bookDAO;
    }
    
    @Override
    public DiscountResponse createDiscount(CreateDiscountRequest request) {
        logger.info("Creating discount with code: {}", request.getCode());
        
        // Check if code already exists
        Discount existing = discountDAO.getDiscountByCode(request.getCode());
        if (existing != null) {
            throw new DuplicateResourceException("Mã giảm giá đã tồn tại");
        }
        
        // Create discount
        Discount discount = new Discount();
        discount.setCode(request.getCode().toUpperCase());
        discount.setDescription(request.getDescription());
        discount.setDiscountType(Discount.DiscountType.valueOf(request.getDiscountType()));
        discount.setDiscountValue(request.getDiscountValue());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setMaxUsage(request.getMaxUsage());
        discount.setIsActive(true);
        
        boolean created = discountDAO.createDiscount(discount);
        if (!created) {
            throw new RuntimeException("Lỗi khi tạo mã giảm giá");
        }
        
        logger.info("Mã giảm giá đã được tạo thành công với id: {}", discount.getDiscountId());
        return convertToResponse(discount);
    }
    
    @Override
    public DiscountResponse updateDiscount(Integer discountId, CreateDiscountRequest request) {
        logger.info("Updating discount with id: {}", discountId);
        
        Discount discount = discountDAO.getDiscountById(discountId);
        if (discount == null) {
            throw new ResourceNotFoundException("Không tìm thấy mã giảm giá");
        }
        
        // Update fields
        discount.setDescription(request.getDescription());
        discount.setDiscountType(Discount.DiscountType.valueOf(request.getDiscountType()));
        discount.setDiscountValue(request.getDiscountValue());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setMaxUsage(request.getMaxUsage());
        
        boolean updated = discountDAO.updateDiscount(discount);
        if (!updated) {
            throw new RuntimeException("Lỗi khi cập nhật mã giảm giá");
        }
        
        logger.info("Mã giảm giá đã được cập nhật thành công với id: {}", discountId);
        return convertToResponse(discount);
    }
    
    @Override
    public DiscountResponse getDiscountById(Integer discountId) {
        logger.info("Getting discount by id: {}", discountId);
        
        Discount discount = discountDAO.getDiscountById(discountId);
        if (discount == null) {
            throw new ResourceNotFoundException("Không tìm thấy mã giảm giá");
        }
        
        return convertToResponse(discount);
    }
    
    @Override
    public DiscountResponse getDiscountByCode(String code) {
        logger.info("Getting discount by code: {}", code);
        
        Discount discount = discountDAO.getDiscountByCode(code.toUpperCase());
        if (discount == null) {
            throw new ResourceNotFoundException("Không tìm thấy mã giảm giá");
        }
        
        return convertToResponse(discount);
    }
    
    @Override
    public PaginationResponse getAllDiscounts(int page, int size) {
        logger.info("Getting all discounts - page: {}, size: {}", page, size);
        
        List<Discount> discounts = discountDAO.getAllDiscounts(page, size);
        int total = discountDAO.countDiscounts();
        
        List<DiscountResponse> responses = discounts.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public PaginationResponse getActiveDiscounts(int page, int size) {
        logger.info("Getting active discounts - page: {}, size: {}", page, size);
        
        List<Discount> discounts = discountDAO.getActiveDiscounts(page, size);
        int total = discountDAO.countActiveDiscounts();
        
        List<DiscountResponse> responses = discounts.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public boolean deleteDiscount(Integer discountId) {
        logger.info("Deleting discount with id: {}", discountId);
        
        Discount discount = discountDAO.getDiscountById(discountId);
        if (discount == null) {
            throw new ResourceNotFoundException("Không tìm thấy mã giảm giá");
        }
        
        return discountDAO.deleteDiscount(discountId);
    }
    
    @Override
    public boolean toggleDiscountStatus(Integer discountId, boolean isActive) {
        logger.info("Toggling discount status - id: {}, isActive: {}", discountId, isActive);
        
        Discount discount = discountDAO.getDiscountById(discountId);
        if (discount == null) {
            throw new ResourceNotFoundException("Không tìm thấy mã giảm giá");
        }
        
        return discountDAO.updateDiscountStatus(discountId, isActive);
    }
    
    @Override
    public Discount validateDiscount(String code) {
        logger.info("Validating discount code: {}", code);
        
        Discount discount = discountDAO.validateDiscount(code.toUpperCase());
        if (discount == null) {
            throw new InvalidCredentialsException("Discount code is invalid or has expired");
        }
        
        return discount;
    }
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal originalAmount, Discount discount) {
        if (discount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        if (discount.getDiscountType() == Discount.DiscountType.percent) {
            discountAmount = originalAmount.multiply(discount.getDiscountValue())
                .divide(BigDecimal.valueOf(100));
        } else {
            discountAmount = discount.getDiscountValue();
            if (discountAmount.compareTo(originalAmount) > 0) {
                discountAmount = originalAmount;
            }
        }
        
        logger.info("Calculated discount amount: {} for original amount: {}", discountAmount, originalAmount);
        return discountAmount;
    }

    @Override
    public BigDecimal applyDiscountToOrder(Integer orderId, String discountCode) {
        logger.info("Applying discount to order - orderId: {}, code: {}", orderId, discountCode);
        
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }
        
        Discount discount = validateDiscount(discountCode);
        
        BigDecimal discountAmount = calculateDiscount(order.getTotalAmount(), discount);
        
        BigDecimal finalAmount = order.getTotalAmount().subtract(discountAmount);
        
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        
        boolean updated = orderDAO.updateOrderDiscount(orderId, discountAmount, finalAmount);
        if (!updated) {
            throw new RuntimeException("Không thể cập nhật giảm giá cho đơn hàng");
        }
        
        applyDiscount(discount.getDiscountId());
        
        logger.info("Đã áp dụng giảm giá cho đơn hàng {}: " +
                    "totalAmount={}, discountAmount={}, finalAmount={}", 
                    orderId, order.getTotalAmount(), discountAmount, finalAmount);
        
        return discountAmount;
    }

    @Override
    public boolean applyDiscount(Integer discountId) {
        logger.info("Applying discount - incrementing used count for id: {}", discountId);
        
        Discount discount = discountDAO.getDiscountById(discountId);
        if (discount == null) {
            throw new ResourceNotFoundException("Khong tìm thấy mã giảm giá");
        }
        
        // Kiểm tra xem còn lượt sử dụng không
        if (discount.getMaxUsage() != null && discount.getUsedCount() >= discount.getMaxUsage()) {
            throw new InvalidCredentialsException("Mã giảm giá đã hết lượt sử dụng");
        }
        
        return discountDAO.incrementUsedCount(discountId);
    }
    
    @Override
    public boolean addBookToDiscount(Integer discountId, Integer bookId) {
        logger.info("Adding book {} to discount {}", bookId, discountId);
        
        Discount discount = discountDAO.getDiscountById(discountId);
        if (discount == null) {
            throw new ResourceNotFoundException("Khong tìm thấy mã giảm giá");
        }
        
        if (bookDAO.getBookById(bookId) == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        
        return discountDAO.addBookToDiscount(bookId, discountId);
    }
    
    @Override
    public boolean removeBookFromDiscount(Integer discountId, Integer bookId) {
        logger.info("Removing book {} from discount {}", bookId, discountId);
        return discountDAO.removeBookFromDiscount(bookId, discountId);
    }
    
    @Override
    public PaginationResponse getBooksByDiscount(Integer discountId, int page, int size) {
        logger.info("Getting books for discount: {}", discountId);
        
        List<com.student.onlinebookstore.model.Book> books = discountDAO.getBooksByDiscount(discountId);
        
        List<BookResponse> responses = books.stream()
            .map(book -> {
                BookResponse response = new BookResponse();
                response.setBookId(book.getBookId());
                response.setTitle(book.getTitle());
                response.setAuthor(book.getAuthor());
                response.setPrice(book.getPrice());
                response.setCoverImageUrl(book.getCoverImageUrl());
                return response;
            })
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, responses.size());
    }
    
    private DiscountResponse convertToResponse(Discount discount) {
        DiscountResponse response = new DiscountResponse();
        response.setDiscountId(discount.getDiscountId());
        response.setCode(discount.getCode());
        response.setDescription(discount.getDescription());
        response.setDiscountType(discount.getDiscountType().name());
        response.setDiscountValue(discount.getDiscountValue());
        response.setStartDate(discount.getStartDate());
        response.setEndDate(discount.getEndDate());
        response.setMaxUsage(discount.getMaxUsage());
        response.setUsedCount(discount.getUsedCount());
        response.setIsActive(discount.getIsActive());
        
        // Calculate status
        LocalDateTime now = LocalDateTime.now();
        if (!discount.getIsActive()) {
            response.setStatus("inactive");
        } else if (now.isBefore(discount.getStartDate())) {
            response.setStatus("upcoming");
        } else if (now.isAfter(discount.getEndDate())) {
            response.setStatus("expired");
        } else {
            response.setStatus("active");
        }
        
        return response;
    }
}