package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.OrderDAO;
import com.student.onlinebookstore.dao.PaymentDAO;
import com.student.onlinebookstore.dto.request.PaymentCallbackRequest;
import com.student.onlinebookstore.dto.response.DashboardResponse;
import com.student.onlinebookstore.dto.response.PaymentResponse;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.Order;
import com.student.onlinebookstore.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentServiceImpl implements PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    
    private PaymentDAO paymentDAO;
    private OrderDAO orderDAO;
    
    public PaymentServiceImpl(PaymentDAO paymentDAO, OrderDAO orderDAO) {
        this.paymentDAO = paymentDAO;
        this.orderDAO = orderDAO;
    }
    
    @Override
    public PaymentResponse processPayment(Integer orderId, String paymentMethod) {
        logger.info("Processing payment for orderId: {}, method: {}", orderId, paymentMethod);
        
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }
        
        Payment payment = paymentDAO.getPaymentByOrderId(orderId);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment information not found");
        }
        
        // Generate transaction ID
        String transactionId = generateTransactionId();
        
        // For COD, auto-complete payment
        if ("cod".equalsIgnoreCase(paymentMethod)) {
            payment.setPaymentStatus(Payment.PaymentStatus.completed);
            payment.setTransactionId(transactionId);
            paymentDAO.updatePaymentStatus(payment.getPaymentId(), "completed", transactionId);
            orderDAO.updatePaymentStatus(orderId, "paid");
            
            logger.info("COD payment completed for orderId: {}", orderId);
        } else {
            // For online payment methods, set as pending and wait for callback
            payment.setPaymentStatus(Payment.PaymentStatus.pending);
            payment.setTransactionId(transactionId);
            paymentDAO.updatePaymentStatus(payment.getPaymentId(), "pending", transactionId);
            
            logger.info("Payment initiated for orderId: {}, transactionId: {}", orderId, transactionId);
        }
        
        return getPaymentByOrderId(orderId);
    }
    
    @Override
    public PaymentResponse handlePaymentCallback(PaymentCallbackRequest request) {
        logger.info("Handling payment callback - transactionId: {}, status: {}", 
                   request.getTransactionId(), request.getPaymentStatus());
        
        Payment payment = null;
        
        return payment != null ? convertToResponse(payment) : null;
    }
    
    @Override
    public PaymentResponse getPaymentByOrderId(Integer orderId) {
        logger.info("Getting payment by orderId: {}", orderId);
        
        Payment payment = paymentDAO.getPaymentByOrderId(orderId);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment information not found");
        }
        
        return convertToResponse(payment);
    }
    
    @Override
    public PaymentResponse getPaymentById(Integer paymentId) {
        logger.info("Getting payment by id: {}", paymentId);
        
        Payment payment = paymentDAO.getPaymentById(paymentId);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment information not found");
        }
        
        return convertToResponse(payment);
    }
    
    @Override
    public boolean updatePaymentStatus(Integer paymentId, String status, String transactionId) {
        logger.info("Updating payment status - paymentId: {}, status: {}", paymentId, status);
        
        return paymentDAO.updatePaymentStatus(paymentId, status, transactionId);
    }
    
    @Override
    public boolean verifyPayment(String transactionId) {
        logger.info("Verifying payment - transactionId: {}", transactionId);
        
        // This would integrate with actual payment gateway API
        // For now, return true for demo
        return true;
    }
    
    @Override
    public boolean refundPayment(Integer orderId) {
        logger.info("Processing refund for orderId: {}", orderId);
        
        Payment payment = paymentDAO.getPaymentByOrderId(orderId);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment information not found");
        }
        
        if (payment.getPaymentStatus() != Payment.PaymentStatus.completed) {
            logger.warn("Cannot refund payment that is not completed - orderId: {}", orderId);
            return false;
        }
        
        boolean updated = paymentDAO.updatePaymentStatus(payment.getPaymentId(), "refunded", payment.getTransactionId());
        
        if (updated) {
            orderDAO.updatePaymentStatus(orderId, "refunded");
            logger.info("Refund completed for orderId: {}", orderId);
        }
        
        return updated;
    }
    
    @Override
    public BigDecimal getTotalRevenue() {
        return paymentDAO.getTotalRevenue();
    }
    
    @Override
    public BigDecimal getRevenueByDateRange(Timestamp startDate, Timestamp endDate) {
        return paymentDAO.getRevenueByDateRange(startDate, endDate);
    }
    
    @Override
    public DashboardResponse getRevenueStatistics() {
        logger.info("Getting revenue statistics");
        
        DashboardResponse dashboard = new DashboardResponse();
        
        // Get total revenue
        dashboard.setTotalRevenue(paymentDAO.getTotalRevenue());
        
        // Get today's revenue
        Timestamp todayStart = Timestamp.valueOf(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
        Timestamp todayEnd = Timestamp.valueOf(LocalDateTime.now());
        dashboard.setTodayRevenue(paymentDAO.getRevenueByDateRange(todayStart, todayEnd));
        
        // Get this week's revenue
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1);
        Timestamp weekStart = Timestamp.valueOf(startOfWeek.withHour(0).withMinute(0).withSecond(0));
        dashboard.setThisWeekRevenue(paymentDAO.getRevenueByDateRange(weekStart, todayEnd));
        
        // Get this month's revenue
        Timestamp monthStart = Timestamp.valueOf(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0));
        dashboard.setThisMonthRevenue(paymentDAO.getRevenueByDateRange(monthStart, todayEnd));
        
        return dashboard;
    }
    
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setPaymentStatus(payment.getPaymentStatus().name());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentDate(payment.getPaymentDate());
        
        return response;
    }
}