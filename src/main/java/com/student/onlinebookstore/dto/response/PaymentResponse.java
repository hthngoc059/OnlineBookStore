package com.student.onlinebookstore.dto.response;
import java.time.LocalDateTime;

public class PaymentResponse {
    private Integer paymentId;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String paymentMethodVietnamese;
    private String paymentStatusVietnamese;
    
    // Constructors
    public PaymentResponse() {}
    
    public PaymentResponse(Integer paymentId, String paymentMethod, String paymentStatus, 
                          String transactionId, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
        this.paymentMethodVietnamese = getPaymentMethodVietnamese(paymentMethod);
        this.paymentStatusVietnamese = getPaymentStatusVietnamese(paymentStatus);
    }
    
    // Helper methods
    private String getPaymentMethodVietnamese(String method) {
        switch (method) {
            case "cod": return "Cash On Delivery (COD)";
            case "banking": return "Bank Transfer";
            case "paypal": return "PayPal";
            case "momo": return "Momo E-Wallet";
            case "vnpay": return "VNPay";
            case "zalopay": return "ZaloPay";
            default: return method;
        }
    }
    
    private String getPaymentStatusVietnamese(String status) {
        switch (status) {
            case "pending": return "Pending";
            case "completed": return "Completed";
            case "failed": return "Failed";
            case "refunded": return "Refunded";
            default: return status;
        }
    }
    
    // Getters and Setters
    public Integer getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.paymentMethodVietnamese = getPaymentMethodVietnamese(paymentMethod);
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        this.paymentStatusVietnamese = getPaymentStatusVietnamese(paymentStatus);
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getPaymentMethodVietnamese() {
        return paymentMethodVietnamese;
    }
    
    public void setPaymentMethodVietnamese(String paymentMethodVietnamese) {
        this.paymentMethodVietnamese = paymentMethodVietnamese;
    }
    
    public String getPaymentStatusVietnamese() {
        return paymentStatusVietnamese;
    }
    
    public void setPaymentStatusVietnamese(String paymentStatusVietnamese) {
        this.paymentStatusVietnamese = paymentStatusVietnamese;
    }
}