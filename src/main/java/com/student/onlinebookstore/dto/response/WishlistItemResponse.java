package com.student.onlinebookstore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WishlistItemResponse {
    private Integer wishlistItemId;
    private Integer bookId;
    private String bookTitle;
    private String bookAuthor;
    private String coverImageUrl;
    private BigDecimal price;
    private Boolean isAvailable;
    private Integer stockQuantity;
    private LocalDateTime addedAt;
    
    public WishlistItemResponse() {}
    
    // Getters and Setters
    public Integer getWishlistItemId() { return wishlistItemId; }
    public void setWishlistItemId(Integer wishlistItemId) { this.wishlistItemId = wishlistItemId; }
    
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}