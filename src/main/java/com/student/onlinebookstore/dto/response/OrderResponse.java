package com.student.onlinebookstore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Integer orderId;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String status;
    private String paymentStatus;
    private AddressResponse address;
    private List<OrderItemResponse> items;
    private PaymentResponse payment;
    private int totalItems;
    private Integer userId;
    
    // Constructors
    public OrderResponse() {}
    
    public OrderResponse(Integer orderId, LocalDateTime orderDate, BigDecimal totalAmount, 
                        BigDecimal discountAmount, BigDecimal finalAmount, String status, 
                        String paymentStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }
    
    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public AddressResponse getAddress() {
        return address;
    }
    
    public void setAddress(AddressResponse address) {
        this.address = address;
    }
    
    public List<OrderItemResponse> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
        if (items != null) {
            this.totalItems = items.size();
        }
    }
    
    public PaymentResponse getPayment() {
        return payment;
    }
    
    public void setPayment(PaymentResponse payment) {
        this.payment = payment;
    }
    
    public int getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    // Helper method to get status in Vietnamese
    public String getStatusVietnamese() {
        switch (status) {
            case "pending": return "Waiting for confirmation";
            case "confirmed": return "Confirmed";
            case "shipping": return "Shipping";
            case "delivered": return "Delivered";
            case "cancelled": return "Cancelled";
            case "returned": return "Returned";
            default: return status;
        }
    }
    
    // Helper method to get payment status in Vietnamese
    public String getPaymentStatusVietnamese() {
        switch (paymentStatus) {
            case "unpaid": return "Unpaid";
            case "paid": return "Paid";
            case "refunded": return "Refunded";
            default: return paymentStatus;
        }
    }
    public String getOrderDateFormatted() {
        if (orderDate == null) return "";
        return orderDate.format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }
}