package com.student.onlinebookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class PaymentCallbackRequest {
    
    // Basic payment info
    @NotBlank(message = "Order ID không được để trống")
    private String orderId;
    
    @NotBlank(message = "Transaction ID không được để trống")
    private String transactionId;
    
    @NotBlank(message = "Trạng thái thanh toán không được để trống")
    @Pattern(regexp = "pending|completed|failed|refunded", 
             message = "Trạng thái phải là: pending, completed, failed, refunded")
    private String paymentStatus;
    
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Pattern(regexp = "cod|banking|paypal|momo|vnpay|zalopay", 
             message = "Phương thức thanh toán không hợp lệ")
    private String paymentMethod;
    
    private BigDecimal amount;
    
    // VNPay specific fields
    private String vnp_ResponseCode;
    private String vnp_TransactionNo;
    private String vnp_BankCode;
    private String vnp_CardType;
    
    // Momo specific fields
    private String momo_resultCode;
    private String momo_message;
    private String momo_orderId;
    private String momo_requestId;
    
    // PayPal specific fields
    private String paypal_payerId;
    private String paypal_paymentId;
    private String paypal_token;
    private String paypal_PayerID;
    
    // Banking (VNPay QR, Bank transfer)
    private String bankCode;
    private String bankTranNo;
    private String cardType;
    
    // Common fields
    private String responseCode;
    private String message;
    private String signature;
    private Long timestamp;
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getVnp_ResponseCode() {
        return vnp_ResponseCode;
    }
    
    public void setVnp_ResponseCode(String vnp_ResponseCode) {
        this.vnp_ResponseCode = vnp_ResponseCode;
    }
    
    public String getVnp_TransactionNo() {
        return vnp_TransactionNo;
    }
    
    public void setVnp_TransactionNo(String vnp_TransactionNo) {
        this.vnp_TransactionNo = vnp_TransactionNo;
    }
    
    public String getVnp_BankCode() {
        return vnp_BankCode;
    }
    
    public void setVnp_BankCode(String vnp_BankCode) {
        this.vnp_BankCode = vnp_BankCode;
    }
    
    public String getVnp_CardType() {
        return vnp_CardType;
    }
    
    public void setVnp_CardType(String vnp_CardType) {
        this.vnp_CardType = vnp_CardType;
    }
    
    public String getMomo_resultCode() {
        return momo_resultCode;
    }
    
    public void setMomo_resultCode(String momo_resultCode) {
        this.momo_resultCode = momo_resultCode;
    }
    
    public String getMomo_message() {
        return momo_message;
    }
    
    public void setMomo_message(String momo_message) {
        this.momo_message = momo_message;
    }
    
    public String getMomo_orderId() {
        return momo_orderId;
    }
    
    public void setMomo_orderId(String momo_orderId) {
        this.momo_orderId = momo_orderId;
    }
    
    public String getMomo_requestId() {
        return momo_requestId;
    }
    
    public void setMomo_requestId(String momo_requestId) {
        this.momo_requestId = momo_requestId;
    }
    
    public String getPaypal_payerId() {
        return paypal_payerId;
    }
    
    public void setPaypal_payerId(String paypal_payerId) {
        this.paypal_payerId = paypal_payerId;
    }
    
    public String getPaypal_paymentId() {
        return paypal_paymentId;
    }
    
    public void setPaypal_paymentId(String paypal_paymentId) {
        this.paypal_paymentId = paypal_paymentId;
    }
    
    public String getPaypal_token() {
        return paypal_token;
    }
    
    public void setPaypal_token(String paypal_token) {
        this.paypal_token = paypal_token;
    }
    
    public String getPaypal_PayerID() {
        return paypal_PayerID;
    }
    
    public void setPaypal_PayerID(String paypal_PayerID) {
        this.paypal_PayerID = paypal_PayerID;
    }
    
    public String getBankCode() {
        return bankCode;
    }
    
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    
    public String getBankTranNo() {
        return bankTranNo;
    }
    
    public void setBankTranNo(String bankTranNo) {
        this.bankTranNo = bankTranNo;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    
    public String getResponseCode() {
        return responseCode;
    }
    
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    // Helper methods
    public boolean isPaymentSuccessful() {
        if (paymentMethod == null) {
            return false;
        }
        
        switch (paymentMethod.toLowerCase()) {
            case "vnpay":
                return "00".equals(vnp_ResponseCode);
            case "momo":
                return "0".equals(momo_resultCode);
            case "paypal":
                return "completed".equalsIgnoreCase(paymentStatus);
            case "cod":
                return "completed".equalsIgnoreCase(paymentStatus);
            default:
                return "completed".equalsIgnoreCase(paymentStatus);
        }
    }
    
    public boolean isPaymentFailed() {
        if (isPaymentSuccessful()) {
            return false;
        }
        return "failed".equalsIgnoreCase(paymentStatus) || 
               "pending".equalsIgnoreCase(paymentStatus);
    }
    
    public String getTransactionStatusMessage() {
        if (isPaymentSuccessful()) {
            return "Thanh toán thành công";
        }
        
        if (paymentMethod == null) {
            return "Không xác định";
        }
        
        switch (paymentMethod.toLowerCase()) {
            case "vnpay":
                return getVNPayMessage();
            case "momo":
                return getMomoMessage();
            default:
                return message != null ? message : "Thanh toán thất bại";
        }
    }
    
    private String getVNPayMessage() {
        if (vnp_ResponseCode == null) return "Không xác định";
        
        switch (vnp_ResponseCode) {
            case "00": return "Giao dịch thành công";
            case "01": return "Giao dịch đã tồn tại";
            case "02": return "Merchant không hợp lệ";
            case "03": return "Dữ liệu gửi sang không đúng định dạng";
            case "04": return "Không tìm thấy giao dịch";
            case "05": return "Số tiền không hợp lệ";
            case "06": return "Mã đơn hàng đã tồn tại";
            case "07": return "Chữ ký không hợp lệ";
            case "08": return "Kiểm tra dữ liệu TMN không hợp lệ";
            case "09": return "Giao dịch bị nghi ngờ là gian lận";
            default: return "Giao dịch thất bại với mã lỗi: " + vnp_ResponseCode;
        }
    }
    
    private String getMomoMessage() {
        if (momo_resultCode == null) return "Không xác định";
        
        switch (momo_resultCode) {
            case "0": return "Giao dịch thành công";
            case "1": return "Giao dịch đã tồn tại";
            case "2": return "Giao dịch thất bại";
            case "3": return "Giao dịch bị hủy";
            case "4": return "Số tiền không hợp lệ";
            case "5": return "Thông tin đơn hàng không hợp lệ";
            case "6": return "Chữ ký không hợp lệ";
            default: return momo_message != null ? momo_message : "Giao dịch thất bại";
        }
    }
    
    // Extract numeric order ID from string
    public Integer getOrderIdAsInteger() {
        if (orderId == null) return null;
        
        try {
            // Handle case where orderId might have prefix like "ORDER_123"
            String numericPart = orderId.replaceAll("[^0-9]", "");
            return numericPart.isEmpty() ? null : Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    // Create response summary for logging
    public String getSummary() {
        return String.format("Payment Callback - Order: %s, Transaction: %s, Status: %s, Method: %s, Success: %s",
            orderId, transactionId, paymentStatus, paymentMethod, isPaymentSuccessful());
    }
}