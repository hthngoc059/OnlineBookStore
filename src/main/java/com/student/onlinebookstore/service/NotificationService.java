package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.response.NotificationResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;

public interface NotificationService {
    
    // Send notifications
    boolean sendNotification(Integer userId, String title, String message);
    boolean sendNotificationToAll(String title, String message);
    boolean sendNotificationToRole(String role, String title, String message);
    
    // Order notifications
    boolean sendOrderStatusNotification(Integer userId, Integer orderId, String status);
    boolean sendPaymentStatusNotification(Integer userId, Integer orderId, String paymentStatus);
    
    // Book notifications
    boolean sendLowStockNotification(Integer bookId, String bookTitle, int currentStock);
    boolean sendBookAvailableNotification(Integer userId, Integer bookId, String bookTitle);
    boolean sendBookRestockNotification(Integer userId, Integer bookId);
    
    // Remind functionality (important for your requirement)
    boolean remindWhenAvailable(Integer userId, Integer bookId);
    void checkAndNotifyAvailableBooks();
    
    // Get notifications
    PaginationResponse getUserNotifications(Integer userId, int page, int size);
    PaginationResponse getUnreadNotifications(Integer userId, int page, int size);
    NotificationResponse getNotificationById(Integer notificationId);
    
    // Manage notifications
    boolean markAsRead(Integer notificationId);
    boolean markAllAsRead(Integer userId);
    boolean deleteNotification(Integer notificationId);
    boolean deleteAllNotifications(Integer userId);
    
    // Count
    int getUnreadCount(Integer userId);
}
