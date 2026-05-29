package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.WishlistItem;
import com.student.onlinebookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {
    // SQL Queries
    private static final String SQL_GET_OR_CREATE_WISHLIST = 
        "INSERT INTO wishlists (user_id) VALUES (?) ON DUPLICATE KEY UPDATE wishlist_id = wishlist_id";
    
    private static final String SQL_ADD_ITEM = 
        "INSERT INTO wishlist_items (wishlist_id, book_id) VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE wishlist_item_id = wishlist_item_id";
    
    private static final String SQL_GET_WISHLIST_ITEMS = 
        "SELECT wi.*, b.title, b.author, b.price, b.cover_image_url, b.is_available, b.stock_quantity " +
        "FROM wishlist_items wi JOIN books b ON wi.book_id = b.book_id " +
        "WHERE wi.wishlist_id = ? ORDER BY wi.added_at DESC";
    
    private static final String SQL_REMOVE_ITEM = 
        "DELETE FROM wishlist_items WHERE wishlist_item_id = ?";
    
    private static final String SQL_EXISTS = 
        "SELECT COUNT(*) FROM wishlist_items wi JOIN wishlists w ON wi.wishlist_id = w.wishlist_id " +
        "WHERE w.user_id = ? AND wi.book_id = ?";
    
    // Get or create wishlist for user
    public int getOrCreateWishlist(int userId) {
        int wishlistId = -1;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_OR_CREATE_WISHLIST, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                wishlistId = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return wishlistId;
    }
    
    // Add item to wishlist
    public boolean addItem(int wishlistId, int bookId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ADD_ITEM)) {
            
            pstmt.setInt(1, wishlistId);
            pstmt.setInt(2, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get wishlist items
    public List<WishlistItem> getWishlistItems(int wishlistId) {
        List<WishlistItem> items = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_WISHLIST_ITEMS)) {
            
            pstmt.setInt(1, wishlistId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem();
                    item.setWishlistItemId(rs.getInt("wishlist_item_id"));
                    item.setAddedAt(rs.getTimestamp("added_at").toLocalDateTime());
                    
                    // Create book object
                    com.student.onlinebookstore.model.Book book = new com.student.onlinebookstore.model.Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setPrice(rs.getBigDecimal("price"));
                    book.setCoverImageUrl(rs.getString("cover_image_url"));
                    book.setIsAvailable(rs.getBoolean("is_available"));
                    book.setStockQuantity(rs.getInt("stock_quantity"));
                    
                    item.setBook(book);
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Remove item from wishlist
    public boolean removeItem(int wishlistItemId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_REMOVE_ITEM)) {
            
            pstmt.setInt(1, wishlistItemId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if book is in wishlist
    public boolean isInWishlist(int userId, int bookId) {
        boolean exists = false;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_EXISTS)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            
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
}