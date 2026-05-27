package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.util.DBConnection;

import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    // SQL Queries
    private static final String SQL_INSERT = 
        "INSERT INTO books (cover_image_url, title, author, description, ISBN, publisher, " +
        "published_year, language, price, stock_quantity, slug, format) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_GET_BY_ID = 
        "SELECT * FROM books WHERE book_id = ?";
    
    private static final String SQL_GET_BY_SLUG = 
        "SELECT * FROM books WHERE slug = ?";
    
    private static final String SQL_GET_BY_ISBN = 
        "SELECT * FROM books WHERE ISBN = ?";
    
    private static final String SQL_GET_ALL = 
        "SELECT * FROM books ORDER BY book_id DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_SEARCH = 
        "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY book_id DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_FILTER_BY_GENRE = 
        "SELECT b.* FROM books b JOIN book_genre bg ON b.book_id = bg.book_id " +
        "JOIN genres g ON bg.genre_id = g.genre_id WHERE g.genre_name = ? ORDER BY b.book_id DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_NEW_ARRIVALS = 
        "SELECT * FROM books ORDER BY created_at DESC LIMIT ?";
    
    private static final String SQL_BEST_SELLERS = 
        "SELECT b.*, SUM(oi.quantity) as total_sold FROM books b " +
        "JOIN order_items oi ON b.book_id = oi.book_id " +
        "GROUP BY b.book_id ORDER BY total_sold DESC LIMIT ?";
    
    private static final String SQL_UPDATE = 
        "UPDATE books SET title = ?, author = ?, description = ?, price = ?, " +
        "stock_quantity = ?, is_available = ?, updated_at = NOW() WHERE book_id = ?";
    
    private static final String SQL_UPDATE_STOCK = 
        "UPDATE books SET stock_quantity = ?, is_available = ?, updated_at = NOW() WHERE book_id = ?";
    
    private static final String SQL_UPDATE_IMAGE = 
        "UPDATE books SET cover_image_url = ?, updated_at = NOW() WHERE book_id = ?";
    
    private static final String SQL_DELETE = 
        "DELETE FROM books WHERE book_id = ?";
    
    private static final String SQL_COUNT = 
        "SELECT COUNT(*) FROM books";
    
    private static final String SQL_COUNT_SEARCH = 
        "SELECT COUNT(*) FROM books WHERE title LIKE ? OR author LIKE ?";
    
    private static final String SQL_DECREASE_STOCK = 
        "UPDATE books SET stock_quantity = stock_quantity - ?, updated_at = NOW() WHERE book_id = ? AND stock_quantity >= ?";
    
    // Create new book
    public boolean createBook(Book book) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, book.getCoverImageUrl());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getDescription());
            pstmt.setString(5, book.getIsbn());
            pstmt.setString(6, book.getPublisher());
            pstmt.setInt(7, book.getPublishedYear() != null ? book.getPublishedYear().getValue() : null);
            pstmt.setString(8, book.getLanguage());
            pstmt.setBigDecimal(9, book.getPrice());
            pstmt.setInt(10, book.getStockQuantity());
            pstmt.setString(11, book.getSlug());
            pstmt.setString(12, book.getFormat().name());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    book.setBookId(rs.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get book by ID
    public Book getBookById(int bookId) {
        Book book = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_ID)) {
            
            pstmt.setInt(1, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    book = mapResultSetToBook(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return book;
    }
    
    // Get book by slug
    public Book getBookBySlug(String slug) {
        Book book = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_SLUG)) {
            
            pstmt.setString(1, slug);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    book = mapResultSetToBook(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return book;
    }
    
    // Get book by ISBN
    public Book getBookByIsbn(String isbn) {
        Book book = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_ISBN)) {
            
            pstmt.setString(1, isbn);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    book = mapResultSetToBook(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return book;
    }
    
    // Get all books with pagination
    public List<Book> getAllBooks(int page, int size) {
        List<Book> books = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ALL)) {
            
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            
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
    
    // Search books by keyword
    public List<Book> searchBooks(String keyword, int page, int size) {
        List<Book> books = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SEARCH)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setInt(3, size);
            pstmt.setInt(4, offset);
            
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
    
    // Filter books by genre
    public List<Book> filterByGenre(String genreName, int page, int size) {
        List<Book> books = new ArrayList<>();
        int offset = page * size;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FILTER_BY_GENRE)) {
            
            pstmt.setString(1, genreName);
            pstmt.setInt(2, size);
            pstmt.setInt(3, offset);
            
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
    
    // Get new arrivals
    public List<Book> getNewArrivals(int limit) {
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_NEW_ARRIVALS)) {
            
            pstmt.setInt(1, limit);
            
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
    
    // Get best sellers
    public List<Book> getBestSellers(int limit) {
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_BEST_SELLERS)) {
            
            pstmt.setInt(1, limit);
            
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
    
    // Update book
    public boolean updateBook(Book book) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getDescription());
            pstmt.setBigDecimal(4, book.getPrice());
            pstmt.setInt(5, book.getStockQuantity());
            pstmt.setBoolean(6, book.getStockQuantity() > 0);
            pstmt.setInt(7, book.getBookId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update stock quantity
    public boolean updateStock(int bookId, int quantity) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_STOCK)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setBoolean(2, quantity > 0);
            pstmt.setInt(3, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update cover image
    public boolean updateCoverImage(int bookId, String imageUrl) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_IMAGE)) {
            
            pstmt.setString(1, imageUrl);
            pstmt.setInt(2, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Decrease stock when ordering
    public boolean decreaseStock(int bookId, int quantity) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DECREASE_STOCK)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, quantity);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete book
    public boolean deleteBook(int bookId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {
            
            pstmt.setInt(1, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get total books count
    public int getTotalBooks() {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // Get search result count
    public int getSearchCount(String keyword) {
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT_SEARCH)) {
            
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
    
    // Map ResultSet to Book object
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setCoverImageUrl(rs.getString("cover_image_url"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setDescription(rs.getString("description"));
        book.setIsbn(rs.getString("ISBN"));
        book.setPublisher(rs.getString("publisher"));
        
        int year = rs.getInt("published_year");
        if (year > 0) {
            book.setPublishedYear(Year.of(year));
        }
        
        book.setLanguage(rs.getString("language"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setStockQuantity(rs.getInt("stock_quantity"));
        book.setSlug(rs.getString("slug"));
        
        String formatStr = rs.getString("format");
        if (formatStr != null) {
            book.setFormat(Book.Format.valueOf(formatStr));
        }
        
        book.setIsAvailable(rs.getBoolean("is_available"));
        book.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        if (rs.getTimestamp("updated_at") != null) {
            book.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        return book;
    }
}