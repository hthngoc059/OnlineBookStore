package com.student.onlinebookstore.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Integer cartId;
    private List<CartItemResponse> items;
    private int totalItems;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal total;
    
    public CartResponse() {}
    
    public CartResponse(Integer cartId, List<CartItemResponse> items, BigDecimal subtotal) {
        this.cartId = cartId;
        this.items = items;
        this.subtotal = subtotal;
        this.totalItems = items != null ? items.size() : 0;
        this.total = subtotal;
        this.discountAmount = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public Integer getCartId() { return cartId; }
    public void setCartId(Integer cartId) { this.cartId = cartId; }
    
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { 
        this.items = items;
        this.totalItems = items != null ? items.size() : 0;
    }
    
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}