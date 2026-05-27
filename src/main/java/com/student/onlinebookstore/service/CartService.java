package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.request.AddToCartRequest;
import com.student.onlinebookstore.dto.request.UpdateCartItemRequest;
import com.student.onlinebookstore.dto.response.CartResponse;

public interface CartService {
    
    CartResponse addToCart(Integer userId, AddToCartRequest request);
    CartResponse getCart(Integer userId);
    CartResponse updateCartItem(Integer userId, UpdateCartItemRequest request);
    CartResponse removeCartItem(Integer userId, Integer cartItemId);
    void clearCart(Integer userId);
    int getCartItemCount(Integer userId);
}
