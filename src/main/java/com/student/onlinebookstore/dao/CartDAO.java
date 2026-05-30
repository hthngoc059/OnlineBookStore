package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Cart;
import com.student.onlinebookstore.model.CartItem;
import com.student.onlinebookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CartDAO {
    // SQL Queries
    private static final String SQL_GET_CART_BY_USER = 
        "SELECT * FROM carts WHERE user_id = ?";
    
    private static final String SQL_ADD_ITEM = 
        "INSERT INTO cart_items (cart_id, book_id, quantity) VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
    
    private static final String SQL_GET_CART_ITEMS = 
        "SELECT ci.*, b.title, b.author, b.price, b.cover_image_url " +
        "FROM cart_items ci JOIN books b ON ci.book_id = b.book_id " +
        "WHERE ci.cart_id = ?";
    
    private static final String SQL_UPDATE_ITEM_QUANTITY = 
        "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
    
    private static final String SQL_REMOVE_ITEM = 
        "DELETE FROM cart_items WHERE cart_item_id = ?";
    
    private static final String SQL_CLEAR_CART = 
        "DELETE FROM cart_items WHERE cart_id = ?";
    
    private static final String SQL_GET_CART_ITEM = 
        "SELECT * FROM cart_items WHERE cart_item_id = ?";
    
    //Get or create cart for user
    public int getOrCreateCart(int userId) {
        // Bước 1: thử lấy cart đã có
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_CART_BY_USER)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cart_id");  // đã có → trả về luôn
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bước 2: chưa có → tạo mới
        String sqlInsert = "INSERT INTO carts (user_id) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
    
    //Get cart by user ID
    public Cart getCartByUserId(int userId) {
        Cart cart = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_CART_BY_USER)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cart = new Cart();
                    cart.setCartId(rs.getInt("cart_id"));
                    cart.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cart;
    }
    
    //Add item to cart
    public boolean addItem(int cartId, int bookId, int quantity) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ADD_ITEM)) {
            
            pstmt.setInt(1, cartId);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, quantity);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Get cart items
    public List<CartItem> getCartItems(int cartId) {
        List<CartItem> items = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_CART_ITEMS)) {
            
            pstmt.setInt(1, cartId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setCartItemId(rs.getInt("cart_item_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    
                    // Create book object
                    com.student.onlinebookstore.model.Book book = new com.student.onlinebookstore.model.Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setPrice(rs.getBigDecimal("price"));
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
    
    //Update cart item quantity
    public boolean updateItemQuantity(int cartItemId, int quantity) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_ITEM_QUANTITY)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, cartItemId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Remove item from cart
    public boolean removeItem(int cartItemId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_REMOVE_ITEM)) {
            
            pstmt.setInt(1, cartItemId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Clear cart
    public boolean clearCart(int cartId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CLEAR_CART)) {
            
            pstmt.setInt(1, cartId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Get cart item by ID
    public CartItem getCartItemById(int cartItemId) {
        CartItem item = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_CART_ITEM)) {
            
            pstmt.setInt(1, cartItemId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    item = new CartItem();
                    item.setCartItemId(rs.getInt("cart_item_id"));
                    item.setQuantity(rs.getInt("quantity"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return item;
    }
}