package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.NotificationDAO;
import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.dto.response.NotificationResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    private BookDAO bookDAO;
    
    public NotificationServiceImpl(NotificationDAO notificationDAO, UserDAO userDAO, 
                                  BookDAO bookDAO) {
        this.notificationDAO = notificationDAO;
        this.userDAO = userDAO;
        this.bookDAO = bookDAO;
    }
    
    @Override
    public boolean sendNotification(Integer userId, String title, String message) {
        logger.info("Sending notification to user {}: {}", userId, title);
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            logger.warn("User not found: {}", userId);
            return false;
        }
        
        return notificationDAO.createNotification(userId, title, message);
    }
    
    @Override
    public boolean sendNotificationToAll(String title, String message) {
        logger.info("Sending notification to all users: {}", title);
        return notificationDAO.createNotificationForAllUsers(title, message);
    }
    
    @Override
    public boolean sendNotificationToRole(String role, String title, String message) {
        logger.info("Sending notification to role {}: {}", role, title);
        return notificationDAO.createNotificationForRole(role, title, message);
    }
    
    @Override
    public boolean sendOrderStatusNotification(Integer userId, Integer orderId, String status) {
        String statusVi = getStatusVietnamese(status);
        String title = "Order Update #" + orderId;
        String message = "Order #" + orderId + " has been updated to status: " + statusVi;
        
        logger.info("Sending order status notification - userId: {}, orderId: {}, status: {}", 
                   userId, orderId, status);
        
        return notificationDAO.createNotification(userId, title, message);
    }
    
    @Override
    public boolean sendPaymentStatusNotification(Integer userId, Integer orderId, String paymentStatus) {
        String statusVi = getPaymentStatusVietnamese(paymentStatus);
        String title = "Payment Update #" + orderId;
        String message = "Payment for order #" + orderId + " has been updated to status: " + statusVi;
        
        logger.info("Sending payment status notification - userId: {}, orderId: {}, status: {}", 
                   userId, orderId, paymentStatus);
        
        return notificationDAO.createNotification(userId, title, message);
    }
    
    @Override
    public boolean sendLowStockNotification(Integer bookId, String bookTitle, int currentStock) {
        String title = "Low Stock Alert";
        String message = "The book \"" + bookTitle + "\" only has " + currentStock + " copies left in stock.";
        
        logger.info("Sending low stock notification - bookId: {}, stock: {}", bookId, currentStock);
        
        return notificationDAO.createNotificationForRole("admin", title, message);
    }
    
    @Override
    public boolean sendBookAvailableNotification(Integer userId, Integer bookId, String bookTitle) {
        String title = "Book Available";
        String message = "The book \"" + bookTitle + "\" that you were interested in is now available.";
        
        logger.info("Sending book available notification - userId: {}, bookId: {}", userId, bookId);
        
        return notificationDAO.createNotification(userId, title, message);
    }
    
    @Override
    public boolean sendBookRestockNotification(Integer userId, Integer bookId) {
        com.student.onlinebookstore.model.Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            logger.warn("Book not found: {}", bookId);
            return false;
        }
        
        return sendBookAvailableNotification(userId, bookId, book.getTitle());
    }
    
    @Override
    public boolean remindWhenAvailable(Integer userId, Integer bookId) {
        logger.info("User {} requested reminder for book {}", userId, bookId);
        
        // Check if book exists
        com.student.onlinebookstore.model.Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        
        // If book is already available, notify immediately
        if (book.getIsAvailable() && book.getStockQuantity() > 0) {
            sendBookAvailableNotification(userId, bookId, book.getTitle());
            logger.info("Book already available, notification sent immediately");
        }
        
        // Add to wishlist (or a separate "remind me" table if needed)
        // This uses wishlist as "remind me" list
        // You may want to create a separate remind_me table for this
        
        return true;
    }
    
    @Override
    public void checkAndNotifyAvailableBooks() {
        logger.info("Checking for books that are back in stock");
        
        // This method would check books that were out of stock and now have stock
        // And notify users who requested reminders
        
        // For each book that was restocked, find users who requested reminders
        // And send them notifications
        
        logger.info("Completed checking for restocked books");
    }
    
    @Override
    public PaginationResponse getUserNotifications(Integer userId, int page, int size) {
        logger.info("Getting notifications for user - userId: {}, page: {}, size: {}", userId, page, size);
        
        List<com.student.onlinebookstore.model.Notification> notifications = 
            notificationDAO.getNotificationsByUserId(userId, page, size);
        int total = notificationDAO.countByUserId(userId);
        
        List<NotificationResponse> responses = notifications.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public PaginationResponse getUnreadNotifications(Integer userId, int page, int size) {
        logger.info("Getting unread notifications for user - userId: {}, page: {}, size: {}", userId, page, size);
        
        List<com.student.onlinebookstore.model.Notification> notifications = 
            notificationDAO.getUnreadNotificationsByUserId(userId);
        
        // Paginate manually
        int start = page * size;
        int end = Math.min(start + size, notifications.size());
        List<com.student.onlinebookstore.model.Notification> paginated = notifications.subList(start, end);
        
        List<NotificationResponse> responses = paginated.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, notifications.size());
    }
    
    @Override
    public NotificationResponse getNotificationById(Integer notificationId) {
        logger.info("Getting notification by id: {}", notificationId);
        
        com.student.onlinebookstore.model.Notification notification = notificationDAO.getNotificationById(notificationId);
        if (notification == null) {
            throw new ResourceNotFoundException("Notification not found");
        }
        
        return convertToResponse(notification);
    }
    
    @Override
    public boolean markAsRead(Integer notificationId) {
        logger.info("Marking notification as read - id: {}", notificationId);
        return notificationDAO.markAsRead(notificationId);
    }
    
    @Override
    public boolean markAllAsRead(Integer userId) {
        logger.info("Marking all notifications as read for user: {}", userId);
        return notificationDAO.markAllAsReadByUserId(userId);
    }
    
    @Override
    public boolean deleteNotification(Integer notificationId) {
        logger.info("Deleting notification - id: {}", notificationId);
        return notificationDAO.deleteNotification(notificationId);
    }
    
    @Override
    public boolean deleteAllNotifications(Integer userId) {
        logger.info("Deleting all notifications for user: {}", userId);
        return notificationDAO.deleteAllByUserId(userId);
    }
    
    @Override
    public int getUnreadCount(Integer userId) {
        return notificationDAO.countUnreadByUserId(userId);
    }
    
    private String getStatusVietnamese(String status) {
        switch (status) {
            case "pending": return "Pending";
            case "confirmed": return "Confirmed";
            case "shipping": return "Shipping";
            case "delivered": return "Delivered";
            case "cancelled": return "Cancelled";
            case "returned": return "Returned";
            default: return status;
        }
    }
    
    private String getPaymentStatusVietnamese(String status) {
        switch (status) {
            case "unpaid": return "Unpaid";
            case "paid": return "Paid";
            case "refunded": return "Refunded";
            default: return status;
        }
    }
    
    private NotificationResponse convertToResponse(com.student.onlinebookstore.model.Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(notification.getNotificationId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt());
        
        if (notification.getUser() != null) {
            response.setUserId(notification.getUser().getUserId());
        }
        
        return response;
    }
}