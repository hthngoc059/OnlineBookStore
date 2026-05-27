package com.student.onlinebookstore.dto.response;

import java.util.List;

public class WishlistResponse {
    private Integer wishlistId;
    private List<WishlistItemResponse> items;
    private int totalItems;
    
    public WishlistResponse() {}
    
    public WishlistResponse(Integer wishlistId, List<WishlistItemResponse> items) {
        this.wishlistId = wishlistId;
        this.items = items;
        this.totalItems = items != null ? items.size() : 0;
    }
    
    // Getters and Setters
    public Integer getWishlistId() { return wishlistId; }
    public void setWishlistId(Integer wishlistId) { this.wishlistId = wishlistId; }
    
    public List<WishlistItemResponse> getItems() { return items; }
    public void setItems(List<WishlistItemResponse> items) {
        this.items = items;
        this.totalItems = items != null ? items.size() : 0;
    }
    
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
}  