package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.request.CreateOrderRequest;
import com.student.onlinebookstore.dto.response.OrderResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;

public interface OrderService {
    
    // User functions
    OrderResponse createOrder(Integer userId, CreateOrderRequest request);
    OrderResponse getOrderById(Integer orderId);
    PaginationResponse getUserOrders(Integer userId, int page, int size);
    boolean cancelOrder(Integer orderId);
    
    // Admin functions
    PaginationResponse getAllOrders(int page, int size);
    PaginationResponse getOrdersByStatus(String status, int page, int size);
    OrderResponse updateOrderStatus(Integer orderId, String status);
    OrderResponse updatePaymentStatus(Integer orderId, String paymentStatus);
    
    // Statistics
    long countTotalOrders();
    long countOrdersByStatus(String status);
}