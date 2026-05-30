package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.ReviewDAO;
import com.student.onlinebookstore.dto.request.CreateBookRequest;
import com.student.onlinebookstore.dto.request.UpdateBookRequest;
import com.student.onlinebookstore.dto.response.BookResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.exception.DuplicateResourceException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.util.FileUploadUtil;
import com.student.onlinebookstore.util.SlugGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    
    private BookDAO bookDAO;
    private ReviewDAO reviewDAO;
    private NotificationService notificationService;
    private FileUploadUtil fileUploadUtil;
    
    @Autowired 
    public BookServiceImpl(BookDAO bookDAO, ReviewDAO reviewDAO, 
                          NotificationService notificationService, FileUploadUtil fileUploadUtil) {
        this.bookDAO = bookDAO;
        this.reviewDAO = reviewDAO;
        this.notificationService = notificationService;
        this.fileUploadUtil = fileUploadUtil;
    }
    
    @Override
    public BookResponse createBook(CreateBookRequest request, MultipartFile image) {
        logger.info("Creating new book: {}", request.getTitle());
        
        // Check if ISBN already exists
        if (request.getIsbn() != null && !request.getIsbn().isEmpty()) {
            Book existingBook = bookDAO.getBookByIsbn(request.getIsbn());
            if (existingBook != null) {
                throw new DuplicateResourceException("ISBN is already in use");
            }
        }
        
        // Upload image
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = fileUploadUtil.uploadFile(image);
        }
        
        // Create book
        Book book = new Book();
        book.setCoverImageUrl(imageUrl);
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setIsbn(request.getIsbn());
        book.setPublisher(request.getPublisher());
        if (request.getPublishedYear() != null) {
            book.setPublishedYear(Year.of(request.getPublishedYear()));
        }
        book.setLanguage(request.getLanguage());
        book.setPrice(request.getPrice());
        book.setStockQuantity(request.getStockQuantity());
        book.setSlug(SlugGenerator.generateSlug(request.getTitle()));
        book.setFormat(Book.Format.valueOf(request.getFormat()));
        book.setIsAvailable(request.getStockQuantity() > 0);
        
        boolean created = bookDAO.createBook(book);
        if (!created) {
            throw new RuntimeException("Failed to create book");
        }
        
        logger.info("Book created successfully with id: {}", book.getBookId());
        return convertToResponse(book);
    }
    
    @Override
    public BookResponse updateBook(Integer bookId, UpdateBookRequest request, MultipartFile image) {
        logger.info("Updating book with id: {}", bookId);
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        
        // Upload new image if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileUploadUtil.uploadFile(image);
            book.setCoverImageUrl(imageUrl);
        }
        
        // Update fields
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setPublisher(request.getPublisher());
        book.setLanguage(request.getLanguage());
        book.setPrice(request.getPrice());
        book.setStockQuantity(request.getStockQuantity());
        book.setIsAvailable(request.getStockQuantity() > 0);
        
        boolean updated = bookDAO.updateBook(book);
        if (!updated) {
            throw new RuntimeException("Failed to update book");
        }
        
        // Check for low stock notification
        if (book.getStockQuantity() <= 5 && book.getStockQuantity() > 0) {
            notificationService.sendLowStockNotification(bookId, book.getTitle(), book.getStockQuantity());
        }
        
        logger.info("Book updated successfully with id: {}", bookId);
        return convertToResponse(book);
    }
    
    @Override
    public BookResponse getBookById(Integer bookId) {
        logger.info("Getting book by id: {}", bookId);
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        
        return convertToResponse(book);
    }
    
    @Override
    public BookResponse getBookBySlug(String slug) {
        logger.info("Getting book by slug: {}", slug);
        
        Book book = bookDAO.getBookBySlug(slug);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        
        return convertToResponse(book);
    }
    
    @Override
    public BookResponse getBookByIsbn(String isbn) {
        logger.info("Getting book by ISBN: {}", isbn);
        
        Book book = bookDAO.getBookByIsbn(isbn);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        
        return convertToResponse(book);
    }
    
    @Override
    public boolean deleteBook(Integer bookId) {
        logger.info("Deleting book with id: {}", bookId);
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        
        return bookDAO.deleteBook(bookId);
    }
    
    @Override
    public PaginationResponse getAllBooks(int page, int size, String sortBy) {
        logger.info("Getting all books - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        
        List<Book> books = bookDAO.getAllBooks(page, size);
        int total = bookDAO.getTotalBooks();
        
        List<BookResponse> responses = books.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public PaginationResponse searchBooks(String keyword, String genre, String author, int page, int size) {
        logger.info("Searching books - keyword: {}, genre: {}, author: {}", keyword, genre, author);
        
        List<Book> books;
        int total;
        
        if (genre != null && !genre.isEmpty()) {
            books = bookDAO.filterByGenre(genre, page, size);
            total = bookDAO.getTotalBooks(); // You may need a separate count method for genre
        } else if (keyword != null && !keyword.isEmpty()) {
            books = bookDAO.searchBooks(keyword, page, size);
            total = bookDAO.getSearchCount(keyword);
        } else {
            books = bookDAO.getAllBooks(page, size);
            total = bookDAO.getTotalBooks();
        }
        
        List<BookResponse> responses = books.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public PaginationResponse getBooksByGenre(String genreName, int page, int size) {
        logger.info("Getting books by genre: {}, page: {}, size: {}", genreName, page, size);
        
        List<Book> books = bookDAO.filterByGenre(genreName, page, size);
        int total = books.size(); // You may need a proper count method
        
        List<BookResponse> responses = books.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public PaginationResponse getNewArrivals(int page, int size) {
        logger.info("Getting new arrivals - page: {}, size: {}", page, size);
        
        List<Book> books = bookDAO.getNewArrivals(size);
        List<BookResponse> responses = books.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, responses.size());
    }
    
    @Override
    public PaginationResponse getBestSellers(int page, int size) {
        logger.info("Getting best sellers - page: {}, size: {}", page, size);
        
        List<Book> books = bookDAO.getBestSellers(size);
        List<BookResponse> responses = books.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, responses.size());
    }
    
    @Override
    public PaginationResponse getRecommendedBooks(Integer userId, int page, int size) {
        logger.info("Getting recommended books for user: {}", userId);
        
        // Simple recommendation based on user's purchased genres
        // This can be enhanced with more sophisticated algorithms
        List<Book> books = bookDAO.getNewArrivals(size);
        List<BookResponse> responses = books.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, responses.size());
    }
    
    @Override
    public boolean updateStock(Integer bookId, int quantity) {
        logger.info("Updating stock for book {} to {}", bookId, quantity);
        
        boolean updated = bookDAO.updateStock(bookId, quantity);
        if (updated) {
            Book book = bookDAO.getBookById(bookId);
            if (book.getStockQuantity() <= 5 && book.getStockQuantity() > 0) {
                notificationService.sendLowStockNotification(bookId, book.getTitle(), book.getStockQuantity());
            }
        }
        return updated;
    }
    
    @Override
    public boolean decreaseStock(Integer bookId, int quantity) {
        logger.info("Decreasing stock for book {} by {}", bookId, quantity);
        return bookDAO.decreaseStock(bookId, quantity);
    }
    
    @Override
    public void checkLowStock() {
        logger.info("Checking low stock books");
        
        List<Book> books = bookDAO.getAllBooks(0, 1000);
        for (Book book : books) {
            if (book.getStockQuantity() <= 5 && book.getStockQuantity() > 0) {
                notificationService.sendLowStockNotification(
                    book.getBookId(), book.getTitle(), book.getStockQuantity()
                );
            }
        }
    }
    
    @Override
    public double getAverageRating(Integer bookId) {
        return reviewDAO.getAverageRating(bookId);
    }
    
    @Override
    public int getRatingCount(Integer bookId) {
        return reviewDAO.getRatingCount(bookId);
    }
    
    private BookResponse convertToResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setBookId(book.getBookId());
        response.setCoverImageUrl(book.getCoverImageUrl());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setDescription(book.getDescription());
        response.setIsbn(book.getIsbn());
        response.setPublisher(book.getPublisher());
        if (book.getPublishedYear() != null) {
            response.setPublishedYear(Year.of(book.getPublishedYear().getValue()));
        }
        response.setLanguage(book.getLanguage());
        response.setPrice(book.getPrice());
        response.setStockQuantity(book.getStockQuantity());
        response.setSlug(book.getSlug());
        response.setFormat(book.getFormat() != null ? book.getFormat().name() : null);
        response.setIsAvailable(book.getIsAvailable());
        response.setAverageRating(getAverageRating(book.getBookId()));
        response.setRatingCount(getRatingCount(book.getBookId()));
        
        return response;
    }
}