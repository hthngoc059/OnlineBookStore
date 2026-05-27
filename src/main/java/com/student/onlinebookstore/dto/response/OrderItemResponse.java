package com.student.onlinebookstore.dto.response;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Integer orderItemId;
    private Integer bookId;
    private String bookTitle;
    private String bookAuthor;
    private String coverImageUrl;
    private Integer quantity;
    private BigDecimal priceAtTime;
    private BigDecimal subtotal;
    
    // Constructors
    public OrderItemResponse() {}
    
    public OrderItemResponse(Integer orderItemId, Integer bookId, String bookTitle, 
                            String bookAuthor, Integer quantity, BigDecimal priceAtTime) {
        this.orderItemId = orderItemId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.subtotal = priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    public Integer getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public Integer getBookId() {
        return bookId;
    }
    
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public String getBookAuthor() {
        return bookAuthor;
    }
    
    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (priceAtTime != null) {
            this.subtotal = priceAtTime.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }
    
    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
        if (quantity != null) {
            this.subtotal = priceAtTime.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}