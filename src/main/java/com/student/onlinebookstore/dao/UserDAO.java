package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {
    // SQL Queries
    private static final String SQL_AUTHENTICATE = 
        "SELECT * FROM users WHERE email = ? AND role = ?";
    
    private static final String SQL_GET_BY_ID = 
        "SELECT * FROM users WHERE user_id = ?";
    
    private static final String SQL_GET_BY_EMAIL = 
        "SELECT * FROM users WHERE email = ?";
    
    private static final String SQL_GET_BY_USERNAME = 
        "SELECT * FROM users WHERE username = ?";
    
    private static final String SQL_GET_ALL = 
        "SELECT * FROM users ORDER BY user_id DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_COUNT_USERS = 
        "SELECT COUNT(*) FROM users";
    
    private static final String SQL_INSERT = 
        "INSERT INTO users (username, email, password, phone_number, role) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SQL_UPDATE = 
        "UPDATE users SET username = ?, phone_number = ?, updated_at = NOW() WHERE user_id = ?";
    
    private static final String SQL_UPDATE_PASSWORD = 
        "UPDATE users SET password = ?, updated_at = NOW() WHERE user_id = ?";
    
    private static final String SQL_EXISTS_BY_EMAIL = 
        "SELECT COUNT(*) FROM users WHERE email = ?";
    
    private static final String SQL_EXISTS_BY_USERNAME = 
        "SELECT COUNT(*) FROM users WHERE username = ?";
    
    // Authenticate user with email and password
    public User authenticate(String email, String password) {
        User user = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_AUTHENTICATE)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, "user");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        user = mapResultSetToUser(rs);
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    // Authenticate admin with email and password
    public User authenticateAdmin(String email, String password) {
        User user = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_AUTHENTICATE)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, "admin");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        user = mapResultSetToUser(rs);
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    // Get user by ID
    public User getUserById(int userId) {
        User user = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_ID)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    // Get user by email
    public User getUserByEmail(String email) {
        User user = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_EMAIL)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    // Get user by username
    public User getUserByUsername(String username) {
        User user = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_USERNAME)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    // Get all users with pagination
    public List<User> getAllUsers(int page, int size) {
        List<User> users = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ALL)) {
            
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    // Get total users count
    public int getTotalUsers() {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_USERS);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Create new user with hashed password
    public boolean createUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT)) {
            
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getRole().name().toLowerCase());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update user profile
    public boolean updateUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPhoneNumber());
            pstmt.setInt(3, user.getUserId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update user password
    public boolean updatePassword(int userId, String newPassword) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {
            
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update phone number
    public boolean updatePhoneNumber(int userId, String phoneNumber) {
        String sql = "UPDATE users SET phone_number = ?, updated_at = NOW() WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phoneNumber);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete user by ID
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            
            System.out.println("Deleted user with ID: " + userId + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if email exists
    public boolean emailExists(String email) {
        boolean exists = false;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_EXISTS_BY_EMAIL)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return exists;
    }
    
    // Check if username exists
    public boolean usernameExists(String username) {
        boolean exists = false;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_EXISTS_BY_USERNAME)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return exists;
    }
    
    // Search users by keyword
    public List<User> searchUsers(String keyword, int page, int size) {
        List<User> users = new ArrayList<>();
        int offset = page * size;
        String sql = "SELECT * FROM users WHERE username LIKE ? OR email LIKE ? ORDER BY user_id DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setInt(3, size);
            pstmt.setInt(4, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }

    // Get search count
    public int getSearchCount(String keyword) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM users WHERE username LIKE ? OR email LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
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

    // Get users by role
    public List<User> getUsersByRole(String role, int page, int size) {
        List<User> users = new ArrayList<>();
        int offset = page * size;
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY user_id DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }

    // Get count by role
    public int getCountByRole(String role) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            
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
    
    // ResultSet to User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("updated_at") != null) {
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return user;
    }
}