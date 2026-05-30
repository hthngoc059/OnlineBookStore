package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Review;
import com.student.onlinebookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    // SQL Queries
    private static final String SQL_CREATE_REVIEW = 
        "INSERT INTO reviews (user_id, book_id, rating, comment) VALUES (?, ?, ?, ?)";
    
    private static final String SQL_GET_REVIEWS_BY_BOOK = 
        "SELECT r.*, u.username FROM reviews r JOIN users u ON r.user_id = u.user_id " +
        "WHERE r.book_id = ? ORDER BY r.created_at DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_GET_REVIEW_BY_USER_AND_BOOK = 
        "SELECT * FROM reviews WHERE user_id = ? AND book_id = ?";
    
    private static final String SQL_GET_AVERAGE_RATING = 
        "SELECT AVG(rating) FROM reviews WHERE book_id = ?";
    
    private static final String SQL_GET_RATING_COUNT = 
        "SELECT COUNT(*) FROM reviews WHERE book_id = ?";
    
    private static final String SQL_UPDATE_REVIEW = 
        "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";
    
    private static final String SQL_DELETE_REVIEW = 
        "DELETE FROM reviews WHERE review_id = ?";
    
    private static final String SQL_GET_REVIEW_BY_ID =
        "SELECT * FROM reviews WHERE review_id = ?";

    private static final String SQL_GET_REVIEWS_BY_USER = 
        "SELECT r.*, b.title FROM reviews r JOIN books b ON r.book_id = b.book_id " +
        "WHERE r.user_id = ? ORDER BY r.created_at DESC LIMIT ? OFFSET ?";

    // Create review
    public boolean createReview(Review review) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE_REVIEW)) {
            
            pstmt.setInt(1, review.getUser().getUserId());
            pstmt.setInt(2, review.getBook().getBookId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get reviews by book ID
    public List<Review> getReviewsByBookId(int bookId, int page, int size) {
        List<Review> reviews = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_REVIEWS_BY_BOOK)) {
            
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = mapResultSetToReview(rs);
                    reviews.add(review);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reviews;
    }

    public List<Review> getReviewsByUserId(int userId, int page, int size) {
        List<Review> reviews = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_REVIEWS_BY_USER)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = mapResultSetToReview(rs);
                    reviews.add(review);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reviews;
    }

    // Get review by user and book
    public Review getReviewByUserAndBook(int userId, int bookId) {
        Review review = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_REVIEW_BY_USER_AND_BOOK)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    review = mapResultSetToReview(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return review;
    }
    
    // Get average rating for book
    public double getAverageRating(int bookId) {
        double avg = 0.0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_AVERAGE_RATING)) {
            
            pstmt.setInt(1, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    avg = rs.getDouble(1);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return avg;
    }
    
    // Get rating count for book
    public int getRatingCount(int bookId) {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_RATING_COUNT)) {
            
            pstmt.setInt(1, bookId);
            
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
    
    // Update review
    public boolean updateReview(int reviewId, int rating, String comment) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_REVIEW)) {
            
            pstmt.setInt(1, rating);
            pstmt.setString(2, comment);
            pstmt.setInt(3, reviewId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete review
    public boolean deleteReview(int reviewId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_REVIEW)) {
            
            pstmt.setInt(1, reviewId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Map ResultSet to Review object
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // Create user object (basic info)
        com.student.onlinebookstore.model.User user = new com.student.onlinebookstore.model.User();
        user.setUserId(rs.getInt("user_id"));
        try {
            user.setUsername(rs.getString("username"));
        } catch (SQLException e) {
            // Username not in this result set
        }
        review.setUser(user);
        
        return review;
    }

    // Get review by ID
    public Review getReviewById(Integer reviewId) {
        try ( Connection conn = DBConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(SQL_GET_REVIEW_BY_ID)) {

            ps.setInt(1, reviewId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));

                return review;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    public int getRatingCountByStar(int bookId, int star) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE book_id = ? AND rating = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, star);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getAverageRating() {
        String sql = "SELECT AVG(rating) FROM reviews";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}