package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.model.Discount;
import com.student.onlinebookstore.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DiscountDAO {
    // SQL Queries
    private static final String SQL_CREATE_DISCOUNT = 
        "INSERT INTO discounts (code, description, discount_type, discount_value, start_date, end_date, max_usage, is_active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_GET_DISCOUNT_BY_ID = 
        "SELECT * FROM discounts WHERE discount_id = ?";
    
    private static final String SQL_GET_DISCOUNT_BY_CODE = 
        "SELECT * FROM discounts WHERE code = ?";
    
    private static final String SQL_GET_ALL_DISCOUNTS = 
        "SELECT * FROM discounts ORDER BY discount_id DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_GET_ACTIVE_DISCOUNTS = 
        "SELECT * FROM discounts WHERE is_active = true AND start_date <= NOW() AND end_date >= NOW() " +
        "ORDER BY discount_id DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_GET_EXPIRED_DISCOUNTS = 
        "SELECT * FROM discounts WHERE end_date < NOW() OR is_active = false";
    
    private static final String SQL_UPDATE_DISCOUNT = 
        "UPDATE discounts SET description = ?, discount_type = ?, discount_value = ?, " +
        "start_date = ?, end_date = ?, max_usage = ?, is_active = ? WHERE discount_id = ?";
    
    private static final String SQL_UPDATE_DISCOUNT_STATUS = 
        "UPDATE discounts SET is_active = ? WHERE discount_id = ?";
    
    private static final String SQL_DELETE_DISCOUNT = 
        "DELETE FROM discounts WHERE discount_id = ?";
    
    private static final String SQL_ADD_BOOK_TO_DISCOUNT = 
        "INSERT INTO book_discount (book_id, discount_id) VALUES (?, ?)";
    
    private static final String SQL_REMOVE_BOOK_FROM_DISCOUNT = 
        "DELETE FROM book_discount WHERE book_id = ? AND discount_id = ?";
    
    private static final String SQL_GET_BOOKS_BY_DISCOUNT = 
        "SELECT b.* FROM books b JOIN book_discount bd ON b.book_id = bd.book_id " +
        "WHERE bd.discount_id = ?";
    
    private static final String SQL_GET_DISCOUNTS_BY_BOOK = 
        "SELECT d.* FROM discounts d JOIN book_discount bd ON d.discount_id = bd.discount_id " +
        "WHERE bd.book_id = ? AND d.is_active = true AND d.start_date <= NOW() AND d.end_date >= NOW()";
    
    private static final String SQL_VALIDATE_DISCOUNT = 
        "SELECT * FROM discounts WHERE code = ? AND is_active = true AND start_date <= NOW() AND end_date >= NOW() " +
        "AND (max_usage IS NULL OR used_count < max_usage)";
    
    private static final String SQL_VALIDATE_DISCOUNT_PREVIEW = 
        "SELECT * FROM discounts WHERE code = ? AND is_active = true " +
        "AND start_date <= NOW() AND end_date >= NOW()";

    public Discount validateDiscountForPreview(String code) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_VALIDATE_DISCOUNT_PREVIEW)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToDiscount(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    private static final String SQL_INCREMENT_USED_COUNT = 
        "UPDATE discounts SET used_count = used_count + 1 WHERE discount_id = ?";
    
    private static final String SQL_COUNT_DISCOUNTS = 
        "SELECT COUNT(*) FROM discounts";
    
    private static final String SQL_COUNT_ACTIVE_DISCOUNTS = 
        "SELECT COUNT(*) FROM discounts WHERE is_active = true AND start_date <= NOW() AND end_date >= NOW()";
    private static final String SQL_CHECK_USER_USED_DISCOUNT =
        "SELECT COUNT(*) FROM user_discount_usage WHERE user_id = ? AND discount_id = ?";

    private static final String SQL_SAVE_USER_DISCOUNT_USAGE =
        "INSERT INTO user_discount_usage (user_id, discount_id) VALUES (?, ?)";

    public boolean hasUserUsedDiscount(int userId, int discountId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CHECK_USER_USED_DISCOUNT)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, discountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean saveUserDiscountUsage(int userId, int discountId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SAVE_USER_DISCOUNT_USAGE)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, discountId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    // Create discount
    public boolean createDiscount(Discount discount) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE_DISCOUNT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, discount.getCode());
            pstmt.setString(2, discount.getDescription());
            pstmt.setString(3, discount.getDiscountType().name());
            pstmt.setBigDecimal(4, discount.getDiscountValue());
            pstmt.setTimestamp(5, Timestamp.valueOf(discount.getStartDate()));
            pstmt.setTimestamp(6, Timestamp.valueOf(discount.getEndDate()));
            pstmt.setObject(7, discount.getMaxUsage());
            pstmt.setBoolean(8, discount.getIsActive());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    discount.setDiscountId(rs.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get discount by ID
    public Discount getDiscountById(int discountId) {
        Discount discount = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_DISCOUNT_BY_ID)) {
            
            pstmt.setInt(1, discountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    discount = mapResultSetToDiscount(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return discount;
    }
    
    // Get discount by code
    public Discount getDiscountByCode(String code) {
        Discount discount = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_DISCOUNT_BY_CODE)) {
            
            pstmt.setString(1, code);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    discount = mapResultSetToDiscount(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return discount;
    }
    
    // Get all discounts
    public List<Discount> getAllDiscounts(int page, int size) {
        List<Discount> discounts = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ALL_DISCOUNTS)) {
            
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDiscount(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return discounts;
    }
    
    // Get active discounts
    public List<Discount> getActiveDiscounts(int page, int size) {
        List<Discount> discounts = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ACTIVE_DISCOUNTS)) {
            
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDiscount(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return discounts;
    }
    
    // Get expired discounts
    public List<Discount> getExpiredDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_EXPIRED_DISCOUNTS);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                discounts.add(mapResultSetToDiscount(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return discounts;
    }
    
    // Update discount
    public boolean updateDiscount(Discount discount) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_DISCOUNT)) {
            
            pstmt.setString(1, discount.getDescription());
            pstmt.setString(2, discount.getDiscountType().name());
            pstmt.setBigDecimal(3, discount.getDiscountValue());
            pstmt.setTimestamp(4, Timestamp.valueOf(discount.getStartDate()));
            pstmt.setTimestamp(5, Timestamp.valueOf(discount.getEndDate()));
            pstmt.setObject(6, discount.getMaxUsage());
            pstmt.setBoolean(7, discount.getIsActive());
            pstmt.setInt(8, discount.getDiscountId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update discount status
    public boolean updateDiscountStatus(int discountId, boolean isActive) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_DISCOUNT_STATUS)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, discountId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete discount
    public boolean deleteDiscount(int discountId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_DISCOUNT)) {
            
            pstmt.setInt(1, discountId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Add book to discount
    public boolean addBookToDiscount(int bookId, int discountId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ADD_BOOK_TO_DISCOUNT)) {
            
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, discountId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Remove book from discount
    public boolean removeBookFromDiscount(int bookId, int discountId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_REMOVE_BOOK_FROM_DISCOUNT)) {
            
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, discountId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get books by discount
    public List<Book> getBooksByDiscount(int discountId) {
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BOOKS_BY_DISCOUNT)) {
            
            pstmt.setInt(1, discountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return books;
    }
    
    // Get discounts by book
    public List<Discount> getDiscountsByBook(int bookId) {
        List<Discount> discounts = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_DISCOUNTS_BY_BOOK)) {
            
            pstmt.setInt(1, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDiscount(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return discounts;
    }
    
    // Validate discount code
    public Discount validateDiscount(String code) {
        Discount discount = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_VALIDATE_DISCOUNT)) {
            
            pstmt.setString(1, code);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    discount = mapResultSetToDiscount(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return discount;
    }
    
    // Increment used count
    public boolean incrementUsedCount(int discountId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INCREMENT_USED_COUNT)) {
            
            pstmt.setInt(1, discountId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Calculate discount amount
    public BigDecimal calculateDiscount(BigDecimal originalAmount, Discount discount) {
        if (discount == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        if (discount.getDiscountType() == Discount.DiscountType.percent) {
            discountAmount = originalAmount.multiply(discount.getDiscountValue())
                .divide(BigDecimal.valueOf(100));
        } else {
            discountAmount = discount.getDiscountValue();
            if (discountAmount.compareTo(originalAmount) > 0) {
                discountAmount = originalAmount;
            }
        }
        
        return discountAmount;
    }
    
    // Count total discounts
    public int countDiscounts() {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_DISCOUNTS);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Count active discounts
    public int countActiveDiscounts() {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_ACTIVE_DISCOUNTS);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Map ResultSet to Discount object
    private Discount mapResultSetToDiscount(ResultSet rs) throws SQLException {
        Discount discount = new Discount();
        discount.setDiscountId(rs.getInt("discount_id"));
        discount.setCode(rs.getString("code"));
        discount.setDescription(rs.getString("description"));
        discount.setDiscountType(Discount.DiscountType.valueOf(rs.getString("discount_type")));
        discount.setDiscountValue(rs.getBigDecimal("discount_value"));
        discount.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
        discount.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
        
        int maxUsage = rs.getInt("max_usage");
        if (!rs.wasNull()) {
            discount.setMaxUsage(maxUsage);
        }
        
        discount.setUsedCount(rs.getInt("used_count"));
        discount.setIsActive(rs.getBoolean("is_active"));
        
        return discount;
    }
    
    // Map ResultSet to Book object
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setCoverImageUrl(rs.getString("cover_image_url"));
        book.setIsAvailable(rs.getBoolean("is_available"));
        return book;
    }
}
