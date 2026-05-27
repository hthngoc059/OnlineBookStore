package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Payment;
import com.student.onlinebookstore.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO { 
    // SQL Queries
    private static final String SQL_CREATE_PAYMENT = 
        "INSERT INTO payments (order_id, payment_method, payment_status, transaction_id) VALUES (?, ?, ?, ?)";
    
    private static final String SQL_GET_PAYMENT_BY_ID = 
        "SELECT * FROM payments WHERE payment_id = ?";
    
    private static final String SQL_GET_PAYMENT_BY_ORDER_ID = 
        "SELECT * FROM payments WHERE order_id = ?";
    
    private static final String SQL_GET_PAYMENTS_BY_STATUS = 
        "SELECT * FROM payments WHERE payment_status = ? ORDER BY payment_date DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_GET_PAYMENTS_BY_METHOD = 
        "SELECT * FROM payments WHERE payment_method = ? ORDER BY payment_date DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_GET_ALL_PAYMENTS = 
        "SELECT * FROM payments ORDER BY payment_date DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_UPDATE_PAYMENT_STATUS = 
        "UPDATE payments SET payment_status = ?, transaction_id = ? WHERE payment_id = ?";
    
    private static final String SQL_UPDATE_PAYMENT_BY_ORDER = 
        "UPDATE payments SET payment_status = ?, transaction_id = ? WHERE order_id = ?";
    
    private static final String SQL_DELETE_PAYMENT = 
        "DELETE FROM payments WHERE payment_id = ?";
    
    private static final String SQL_COUNT_PAYMENTS = 
        "SELECT COUNT(*) FROM payments";
    
    private static final String SQL_COUNT_BY_STATUS = 
        "SELECT COUNT(*) FROM payments WHERE payment_status = ?";
    
    private static final String SQL_GET_TOTAL_REVENUE = 
        "SELECT SUM(o.final_amount) FROM payments p JOIN orders o ON p.order_id = o.order_id " +
        "WHERE p.payment_status = 'completed'";
    
    private static final String SQL_GET_REVENUE_BY_DATE_RANGE = 
        "SELECT SUM(o.final_amount) FROM payments p JOIN orders o ON p.order_id = o.order_id " +
        "WHERE p.payment_status = 'completed' AND p.payment_date BETWEEN ? AND ?";
    
    private static final String SQL_GET_REVENUE_BY_METHOD = 
        "SELECT payment_method, SUM(o.final_amount) as total FROM payments p " +
        "JOIN orders o ON p.order_id = o.order_id " +
        "WHERE p.payment_status = 'completed' GROUP BY payment_method";
    
    // Create payment record
    public boolean createPayment(Payment payment) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE_PAYMENT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, payment.getOrder().getOrderId());
            pstmt.setString(2, payment.getPaymentMethod().name());
            pstmt.setString(3, payment.getPaymentStatus().name());
            pstmt.setString(4, payment.getTransactionId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    payment.setPaymentId(rs.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get payment by ID
    public Payment getPaymentById(int paymentId) {
        Payment payment = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_PAYMENT_BY_ID)) {
            
            pstmt.setInt(1, paymentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    payment = mapResultSetToPayment(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payment;
    }
    
    // Get payment by order ID
    public Payment getPaymentByOrderId(int orderId) {
        Payment payment = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_PAYMENT_BY_ORDER_ID)) {
            
            pstmt.setInt(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    payment = mapResultSetToPayment(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payment;
    }
    
    // Get payments by status
    public List<Payment> getPaymentsByStatus(String status, int page, int size) {
        List<Payment> payments = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_PAYMENTS_BY_STATUS)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    // Get payments by payment method
    public List<Payment> getPaymentsByMethod(String method, int page, int size) {
        List<Payment> payments = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_PAYMENTS_BY_METHOD)) {
            
            pstmt.setString(1, method);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    // Get all payments
    public List<Payment> getAllPayments(int page, int size) {
        List<Payment> payments = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ALL_PAYMENTS)) {
            
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    // Update payment status
    public boolean updatePaymentStatus(int paymentId, String status, String transactionId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PAYMENT_STATUS)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, transactionId);
            pstmt.setInt(3, paymentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Also update orders table payment_status
                updateOrderPaymentStatus(paymentId, status);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update payment status by order ID
    public boolean updatePaymentByOrderId(int orderId, String status, String transactionId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PAYMENT_BY_ORDER)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, transactionId);
            pstmt.setInt(3, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update order payment status
    private void updateOrderPaymentStatus(int paymentId, String paymentStatus) {
        String sql = "UPDATE orders SET payment_status = ? WHERE order_id = " +
                     "(SELECT order_id FROM payments WHERE payment_id = ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String orderStatus = "unpaid";
            if ("completed".equals(paymentStatus)) {
                orderStatus = "paid";
            } else if ("refunded".equals(paymentStatus)) {
                orderStatus = "refunded";
            }
            
            pstmt.setString(1, orderStatus);
            pstmt.setInt(2, paymentId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Delete payment
    public boolean deletePayment(int paymentId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_PAYMENT)) {
            
            pstmt.setInt(1, paymentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Count total payments
    public int countPayments() {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_PAYMENTS);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Count payments by status
    public int countPaymentsByStatus(String status) {
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
    
    // Get total revenue
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = BigDecimal.ZERO;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_TOTAL_REVENUE);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                revenue = rs.getBigDecimal(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return revenue;
    }
    
    // Get revenue by date range
    public BigDecimal getRevenueByDateRange(Timestamp startDate, Timestamp endDate) {
        BigDecimal revenue = BigDecimal.ZERO;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_REVENUE_BY_DATE_RANGE)) {
            
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    revenue = rs.getBigDecimal(1);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return revenue;
    }
    
    // Get revenue by payment method
    public List<Object[]> getRevenueByPaymentMethod() {
        List<Object[]> revenues = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_REVENUE_BY_METHOD);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getString("payment_method");
                row[1] = rs.getDouble("total");
                revenues.add(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return revenues;
    }
    
    // Map ResultSet to Payment object
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(rs.getString("payment_method")));
        payment.setPaymentStatus(Payment.PaymentStatus.valueOf(rs.getString("payment_status")));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
        
        // Create order object with just ID
        com.student.onlinebookstore.model.Order order = new com.student.onlinebookstore.model.Order();
        order.setOrderId(rs.getInt("order_id"));
        payment.setOrder(order);
        
        return payment;
    }
}