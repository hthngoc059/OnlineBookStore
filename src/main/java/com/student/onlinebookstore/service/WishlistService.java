package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.dto.response.WishlistResponse;

public interface WishlistService {
    
    WishlistResponse addToWishlist(Integer userId, Integer bookId);
    WishlistResponse removeFromWishlist(Integer userId, Integer wishlistItemId);
    WishlistResponse getWishlist(Integer userId);
    PaginationResponse getWishlistItems(Integer userId, int page, int size);
    boolean isInWishlist(Integer userId, Integer bookId);
    int getWishlistCount(Integer userId);
    WishlistResponse moveToCart(Integer userId, Integer wishlistItemId);
    WishlistResponse addAllToCart(Integer userId);
    Integer getWishlistItemId(Integer userId, Integer bookId);
}
