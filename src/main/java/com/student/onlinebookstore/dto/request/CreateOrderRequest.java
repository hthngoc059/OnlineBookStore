package com.student.onlinebookstore.dto.request;

import jakarta.validation.constraints.NotNull;

public class CreateOrderRequest {
    @NotNull(message = "Address cannot be blank")
    private Integer addressId;
    
    private String discountCode;
    
    @NotNull(message = "Payment method cannot be blank")
    private String paymentMethod; // cod, banking, momo, vnpay...
    
    // Getters and Setters
    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }
    
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}