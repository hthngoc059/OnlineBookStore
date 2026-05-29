package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Notification;
import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDAO {
    // SQL Queries
    private static final String SQL_CREATE_NOTIFICATION = 
        "INSERT INTO notifications (user_id, title, message, is_read) VALUES (?, ?, ?, ?)";
    
    private static final String SQL_GET_NOTIFICATION_BY_ID = 
        "SELECT * FROM notifications WHERE notification_id = ?";
    
    private static final String SQL_GET_NOTIFICATIONS_BY_USER = 
        "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_GET_UNREAD_NOTIFICATIONS_BY_USER = 
        "SELECT * FROM notifications WHERE user_id = ? AND is_read = false ORDER BY created_at DESC";
    
    private static final String SQL_GET_ALL_NOTIFICATIONS = 
        "SELECT n.*, u.username FROM notifications n JOIN users u ON n.user_id = u.user_id " +
        "ORDER BY n.created_at DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_MARK_AS_READ = 
        "UPDATE notifications SET is_read = true WHERE notification_id = ?";
    
    private static final String SQL_MARK_ALL_AS_READ_BY_USER = 
        "UPDATE notifications SET is_read = true WHERE user_id = ? AND is_read = false";
    
    private static final String SQL_DELETE_NOTIFICATION = 
        "DELETE FROM notifications WHERE notification_id = ?";
    
    private static final String SQL_DELETE_ALL_BY_USER = 
        "DELETE FROM notifications WHERE user_id = ?";
    
    private static final String SQL_COUNT_UNREAD_BY_USER = 
        "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";
    
    private static final String SQL_COUNT_BY_USER = 
        "SELECT COUNT(*) FROM notifications WHERE user_id = ?";
    
    // Create notification
    public boolean createNotification(Notification notification) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE_NOTIFICATION, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, notification.getUser().getUserId());
            pstmt.setString(2, notification.getTitle());
            pstmt.setString(3, notification.getMessage());
            pstmt.setBoolean(4, notification.getIsRead());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    notification.setNotificationId(rs.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Create notification for user (convenience method)    
    public boolean createNotification(int userId, String title, String message) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE_NOTIFICATION)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, message);
            pstmt.setBoolean(4, false);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Create notification for all users
    public boolean createNotificationForAllUsers(String title, String message) {
        String sql = "INSERT INTO notifications (user_id, title, message) SELECT user_id, ?, ? FROM users";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, message);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Create notification for users with specific role
    public boolean createNotificationForRole(String role, String title, String message) {
        String sql = "INSERT INTO notifications (user_id, title, message) " +
                     "SELECT user_id, ?, ? FROM users WHERE role = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, message);
            pstmt.setString(3, role);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get notification by ID
    public Notification getNotificationById(int notificationId) {
        Notification notification = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_NOTIFICATION_BY_ID)) {
            
            pstmt.setInt(1, notificationId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    notification = mapResultSetToNotification(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notification;
    }
    
    // Get notifications by user ID
    public List<Notification> getNotificationsByUserId(int userId, int page, int size) {
        List<Notification> notifications = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_NOTIFICATIONS_BY_USER)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    // Get unread notifications by user ID
    public List<Notification> getUnreadNotificationsByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_UNREAD_NOTIFICATIONS_BY_USER)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    // Get all notifications (admin)
    public List<Notification> getAllNotifications(int page, int size) {
        List<Notification> notifications = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ALL_NOTIFICATIONS)) {
            
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = mapResultSetToNotification(rs);
                    // Add username to notification (would need to extend Notification model)
                    notifications.add(notification);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    // Mark notification as read
    public boolean markAsRead(int notificationId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_MARK_AS_READ)) {
            
            pstmt.setInt(1, notificationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Mark all notifications as read for user
    public boolean markAllAsReadByUserId(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_MARK_ALL_AS_READ_BY_USER)) {
            
            pstmt.setInt(1, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete notification
    public boolean deleteNotification(int notificationId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_NOTIFICATION)) {
            
            pstmt.setInt(1, notificationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete all notifications for user
    public boolean deleteAllByUserId(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_ALL_BY_USER)) {
            
            pstmt.setInt(1, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Count unread notifications by user
    public int countUnreadByUserId(int userId) {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_UNREAD_BY_USER)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Count notifications by user
    public int countByUserId(int userId) {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_BY_USER)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    public List<Notification> getAllNotificationsWithUsers(int page, int size) {
        List<Notification> notifications = new ArrayList<>();
        int offset = page * size;
        
        String sql = "SELECT n.*, u.username, u.email FROM notifications n " +
                    "JOIN users u ON n.user_id = u.user_id " +
                    "ORDER BY n.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setNotificationId(rs.getInt("notification_id"));
                    notification.setTitle(rs.getString("title"));
                    notification.setMessage(rs.getString("message"));
                    notification.setIsRead(rs.getBoolean("is_read"));
                    notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    notification.setUser(user);
                    
                    notifications.add(notification);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }

    /**
     * Count total notifications (for pagination)
     */
    public int countAllNotifications() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM notifications";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Send low stock notification
    public boolean sendLowStockNotification(int bookId, String bookTitle, int currentStock) {
        String message = "The \"" + bookTitle + "\" only has " + currentStock + " copies left in stock.";
        return createNotificationForRole("admin", "Low Stock Alert", message);
    }
    
    // Send order status notification
    public boolean sendOrderStatusNotification(int userId, int orderId, String status) {
        String title = "Order Update #" + orderId;
        String message = "Order #" + orderId + " has been updated to: " + getStatusVietnamese(status);
        return createNotification(userId, title, message);
    }
    
    // Send book available notification (for remind functionality)
    public boolean sendBookAvailableNotification(int userId, int bookId, String bookTitle) {
        String title = "Book Available";
        String message = "The book \"" + bookTitle + "\" you were interested in is now available.";
        return createNotification(userId, title, message);
    }
    
    // Get status in Vietnamese
    private String getStatusVietnamese(String status) {
        switch (status) {
            case "pending": return "Waiting for confirmation";
            case "confirmed": return "Confirmed";
            case "shipping": return "Shipping";
            case "delivered": return "Delivered";
            case "cancelled": return "Cancelled";
            default: return status;
        }
    }
    
    // Map ResultSet to Notification object
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setIsRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // Create user object with just ID
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        notification.setUser(user);
        
        return notification;
    }
}