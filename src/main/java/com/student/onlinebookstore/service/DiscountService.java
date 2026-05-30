package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.request.CreateDiscountRequest;
import com.student.onlinebookstore.dto.response.DiscountResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.model.Discount;

import java.math.BigDecimal;

public interface DiscountService {
    
    // Discount management
    DiscountResponse createDiscount(CreateDiscountRequest request);
    DiscountResponse updateDiscount(Integer discountId, CreateDiscountRequest request);
    DiscountResponse getDiscountById(Integer discountId);
    DiscountResponse getDiscountByCode(String code);
    PaginationResponse getAllDiscounts(int page, int size);
    PaginationResponse getActiveDiscounts(int page, int size);
    boolean deleteDiscount(Integer discountId);
    boolean toggleDiscountStatus(Integer discountId, boolean isActive);
    
    // Discount application
    Discount validateDiscount(String code);
    Discount validateDiscountForPreview(String code);
    Discount validateDiscountForUser(String code, int userId);
    BigDecimal calculateDiscount(BigDecimal originalAmount, Discount discount);
    BigDecimal applyDiscountToOrder(Integer orderId, String discountCode);
    boolean applyDiscount(Integer discountId);
    
    // Book discount management
    boolean addBookToDiscount(Integer discountId, Integer bookId);
    boolean removeBookFromDiscount(Integer discountId, Integer bookId);
    PaginationResponse getBooksByDiscount(Integer discountId, int page, int size);
    
}