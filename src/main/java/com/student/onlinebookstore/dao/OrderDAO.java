package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Address;
import com.student.onlinebookstore.model.Order;
import com.student.onlinebookstore.model.OrderItem;
import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDAO {
    private static final Logger logger = LoggerFactory.getLogger(OrderDAO.class);
    // SQL Queries
    private static final String SQL_CREATE_ORDER = 
        "INSERT INTO orders (user_id, address_id, total_amount, discount_amount, final_amount, status, payment_status) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_ADD_ORDER_ITEM = 
        "INSERT INTO order_items (order_id, book_id, quantity, price_at_time) VALUES (?, ?, ?, ?)";
    
    private static final String SQL_GET_ORDER_BY_ID = 
        "SELECT * FROM orders WHERE order_id = ?";
    
    private static final String SQL_GET_ORDER_ITEMS = 
        "SELECT oi.*, b.title, b.author, b.cover_image_url " +
        "FROM order_items oi JOIN books b ON oi.book_id = b.book_id " +
        "WHERE oi.order_id = ?";
    
    private static final String SQL_GET_ORDERS_BY_USER = 
        "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_GET_ALL_ORDERS = 
        "SELECT * FROM orders ORDER BY order_date DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_GET_ORDERS_BY_STATUS = 
        "SELECT * FROM orders WHERE status = ? ORDER BY order_date DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_UPDATE_ORDER_STATUS = 
        "UPDATE orders SET status = ?, updated_at = NOW() WHERE order_id = ?";
    
    private static final String SQL_UPDATE_PAYMENT_STATUS = 
        "UPDATE orders SET payment_status = ?, updated_at = NOW() WHERE order_id = ?";
    
    private static final String SQL_CANCEL_ORDER = 
        "UPDATE orders SET status = 'cancelled', updated_at = NOW() WHERE order_id = ? AND status = 'pending'";
    
    private static final String SQL_COUNT_BY_USER = 
        "SELECT COUNT(*) FROM orders WHERE user_id = ?";
    
    private static final String SQL_COUNT_ALL = 
        "SELECT COUNT(*) FROM orders";
    
    private static final String SQL_COUNT_BY_STATUS = 
        "SELECT COUNT(*) FROM orders WHERE status = ?";
    
    // Create order
    public int createOrder(Order order) {
        int orderId = -1;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE_ORDER, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, order.getUser().getUserId());
            pstmt.setInt(2, order.getAddress().getAddressId());
            pstmt.setBigDecimal(3, order.getTotalAmount());
            pstmt.setBigDecimal(4, order.getDiscountAmount());
            pstmt.setBigDecimal(5, order.getFinalAmount());
            pstmt.setString(6, order.getStatus().name());
            pstmt.setString(7, order.getPaymentStatus().name());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                    order.setOrderId(orderId);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orderId;
    }
    
    // Add order item
    public boolean addOrderItem(int orderId, OrderItem item) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ADD_ORDER_ITEM)) {
            
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, item.getBook().getBookId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setBigDecimal(4, item.getPriceAtTime());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get order by ID
    public Order getOrderById(int orderId) {
        Order order = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ORDER_BY_ID)) {
            
            pstmt.setInt(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order = mapResultSetToOrder(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return order;
    }
    
    // Get order items
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ORDER_ITEMS)) {
            
            pstmt.setInt(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPriceAtTime(rs.getBigDecimal("price_at_time"));
                    
                    // Create book object
                    com.student.onlinebookstore.model.Book book = new com.student.onlinebookstore.model.Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setCoverImageUrl(rs.getString("cover_image_url"));
                    
                    item.setBook(book);
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Get orders by user ID
    public List<Order> getOrdersByUserId(int userId, int page, int size) {
        List<Order> orders = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ORDERS_BY_USER)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    // Get all orders
    public List<Order> getAllOrders(int page, int size) {
        List<Order> orders = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ALL_ORDERS)) {
            
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    // Get orders by status
    public List<Order> getOrdersByStatus(String status, int page, int size) {
        List<Order> orders = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ORDERS_BY_STATUS)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    // Update order status
    public boolean updateOrderStatus(int orderId, String status) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_ORDER_STATUS)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update payment status
    public boolean updatePaymentStatus(int orderId, String paymentStatus) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PAYMENT_STATUS)) {
            
            pstmt.setString(1, paymentStatus);
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cancel order
    public boolean cancelOrder(int orderId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CANCEL_ORDER)) {
            
            pstmt.setInt(1, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Count orders by user
    public int countOrdersByUserId(int userId) {
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
    
    // Count all orders
    public int countAllOrders() {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_ALL);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Count orders by status
    public int countOrdersByStatus(String status) {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_BY_STATUS)) {
            
            pstmt.setString(1, status);
            
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
    
    // Map ResultSet to Order object
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        order.setFinalAmount(rs.getBigDecimal("final_amount"));
        order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
        order.setPaymentStatus(Order.PaymentStatus.valueOf(rs.getString("payment_status")));

        // ✅ THÊM 2 ĐOẠN NÀY
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        order.setUser(user);

        Address address = new Address();
        address.setAddressId(rs.getInt("address_id"));
        order.setAddress(address);
        // ✅ KẾT THÚC THÊM

        if (rs.getTimestamp("updated_at") != null) {
            order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return order;
    }

    public boolean updateOrderDiscount(int orderId, BigDecimal discountAmount, BigDecimal finalAmount) {
        String sql = "UPDATE orders SET discount_amount = ?, final_amount = ?, updated_at = NOW() WHERE order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, discountAmount);
            pstmt.setBigDecimal(2, finalAmount);
            pstmt.setInt(3, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật giảm giá cho đơn hàng: {}", e.getMessage());
            return false;
        }
    }
}