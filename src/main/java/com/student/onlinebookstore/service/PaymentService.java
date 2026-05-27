package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.request.PaymentCallbackRequest;
import com.student.onlinebookstore.dto.response.PaymentResponse;
import com.student.onlinebookstore.dto.response.DashboardResponse;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface PaymentService {
    
    // Payment processing
    PaymentResponse processPayment(Integer orderId, String paymentMethod);
    PaymentResponse handlePaymentCallback(PaymentCallbackRequest request);
    PaymentResponse getPaymentByOrderId(Integer orderId);
    PaymentResponse getPaymentById(Integer paymentId);
    
    // Payment status
    boolean updatePaymentStatus(Integer paymentId, String status, String transactionId);
    boolean verifyPayment(String transactionId);
    
    // Refund
    boolean refundPayment(Integer orderId);
    
    // Statistics (for admin)
    BigDecimal getTotalRevenue();
    BigDecimal getRevenueByDateRange(Timestamp startDate, Timestamp endDate);
    DashboardResponse getRevenueStatistics();
}