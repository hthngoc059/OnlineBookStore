package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.request.CreateBookRequest;
import com.student.onlinebookstore.dto.request.UpdateBookRequest;
import com.student.onlinebookstore.dto.response.BookResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    
    // Book management
    BookResponse createBook(CreateBookRequest request, MultipartFile image);
    BookResponse updateBook(Integer bookId, UpdateBookRequest request, MultipartFile image);
    BookResponse getBookById(Integer bookId);
    BookResponse getBookBySlug(String slug);
    BookResponse getBookByIsbn(String isbn);
    boolean deleteBook(Integer bookId);
    
    // Listing and search
    PaginationResponse getAllBooks(int page, int size, String sortBy);
    PaginationResponse searchBooks(String keyword, String genre, String author, int page, int size);
    PaginationResponse getBooksByGenre(String genreName, int page, int size);
    PaginationResponse getNewArrivals(int page, int size);
    PaginationResponse getBestSellers(int page, int size);
    PaginationResponse getRecommendedBooks(Integer userId, int page, int size);
    
    // Stock management
    boolean updateStock(Integer bookId, int quantity);
    boolean decreaseStock(Integer bookId, int quantity);
    void checkLowStock();
    
    // Rating
    double getAverageRating(Integer bookId);
    int getRatingCount(Integer bookId);
}
