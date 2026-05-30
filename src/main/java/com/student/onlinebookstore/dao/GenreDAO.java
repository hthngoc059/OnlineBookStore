package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Genre;
import com.student.onlinebookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {
    // SQL Queries
    private static final String SQL_GET_ALL = 
        "SELECT * FROM genres ORDER BY genre_name";
    
    private static final String SQL_GET_BY_ID = 
        "SELECT * FROM genres WHERE genre_id = ?";
    
    private static final String SQL_GET_BY_NAME = 
        "SELECT * FROM genres WHERE genre_name = ?";
    
    private static final String SQL_CREATE = 
        "INSERT INTO genres (genre_name) VALUES (?)";
    
    private static final String SQL_UPDATE = 
        "UPDATE genres SET genre_name = ? WHERE genre_id = ?";
    
    private static final String SQL_DELETE = 
        "DELETE FROM genres WHERE genre_id = ?";
    
    private static final String SQL_COUNT = 
        "SELECT COUNT(*) FROM genres";

    private static final String SQL_ADD_BOOK_TO_GENRE = 
        "INSERT INTO book_genre (book_id, genre_id) VALUES (?, ?)";
    
    private static final String SQL_REMOVE_BOOK_FROM_GENRE = 
        "DELETE FROM book_genre WHERE book_id = ? AND genre_id = ?";
    
    private static final String SQL_REMOVE_ALL_GENRES_FROM_BOOK = 
        "DELETE FROM book_genre WHERE book_id = ?";
    
    private static final String SQL_GET_GENRES_BY_BOOK_ID = 
        "SELECT g.* FROM genres g " +
        "JOIN book_genre bg ON g.genre_id = bg.genre_id " +
        "WHERE bg.book_id = ? ORDER BY g.genre_name";
    
    private static final String SQL_GET_GENRE_IDS_BY_BOOK_ID = 
        "SELECT genre_id FROM book_genre WHERE book_id = ?";
    
    private static final String SQL_GET_BOOKS_BY_GENRE_ID = 
        "SELECT b.* FROM books b " +
        "JOIN book_genre bg ON b.book_id = bg.book_id " +
        "WHERE bg.genre_id = ? ORDER BY b.title";
    
    // Get all genres
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ALL);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                genres.add(mapResultSetToGenre(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return genres;
    }
    
    // Get genre by ID
    public Genre getGenreById(int genreId) {
        Genre genre = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_ID)) {
            
            pstmt.setInt(1, genreId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    genre = mapResultSetToGenre(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return genre;
    }
    
    // Get genre by name
    public Genre getGenreByName(String genreName) {
        Genre genre = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_NAME)) {
            
            pstmt.setString(1, genreName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    genre = mapResultSetToGenre(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return genre;
    }
    
    // Add new genre
    public boolean createGenre(Genre genre) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, genre.getGenreName());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    genre.setGenreId(rs.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update existing genre
    public boolean updateGenre(Genre genre) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {
            
            pstmt.setString(1, genre.getGenreName());
            pstmt.setInt(2, genre.getGenreId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete genre
    public boolean deleteGenre(int genreId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {
            
            pstmt.setInt(1, genreId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Count total genres
    public int countGenres() {
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

    
    // Add book to genre
    public boolean addBookToGenre(int bookId, int genreId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ADD_BOOK_TO_GENRE)) {
            
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, genreId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Remove book from genre
    public boolean removeBookFromGenre(int bookId, int genreId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_REMOVE_BOOK_FROM_GENRE)) {
            
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, genreId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Remove all genres from a book (used when updating book genres)
    public boolean removeAllGenresFromBook(int bookId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_REMOVE_ALL_GENRES_FROM_BOOK)) {
            
            pstmt.setInt(1, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected >= 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get genres by book ID
    public List<Genre> getGenresByBookId(int bookId) {
        List<Genre> genres = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_GENRES_BY_BOOK_ID)) {
            
            pstmt.setInt(1, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(mapResultSetToGenre(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return genres;
    }
    
    // Get genre IDs by book ID (used for pre-selecting genres in admin edit form)
    public List<Integer> getGenreIdsByBookId(int bookId) {
        List<Integer> genreIds = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_GENRE_IDS_BY_BOOK_ID)) {
            
            pstmt.setInt(1, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    genreIds.add(rs.getInt("genre_id"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return genreIds;
    }
    
    // Get book IDs by genre ID (used for filtering books by genre in catalog)
    public List<Integer> getBookIdsByGenreId(int genreId) {
        List<Integer> bookIds = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BOOKS_BY_GENRE_ID)) {
            
            pstmt.setInt(1, genreId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookIds.add(rs.getInt("book_id"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bookIds;
    }
    
    // Helper method to map ResultSet to Genre object
    private Genre mapResultSetToGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setGenreId(rs.getInt("genre_id"));
        genre.setGenreName(rs.getString("genre_name"));
        return genre;
    }
}