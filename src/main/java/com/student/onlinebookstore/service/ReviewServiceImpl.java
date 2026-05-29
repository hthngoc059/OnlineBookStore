package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.ReviewDAO;
import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.dto.request.ReviewRequest;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.dto.response.ReviewResponse;
import com.student.onlinebookstore.exception.DuplicateResourceException;
import com.student.onlinebookstore.exception.InvalidInputException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.Review;
import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewServiceImpl implements ReviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);
    
    private ReviewDAO reviewDAO;
    private UserDAO userDAO;
    private BookDAO bookDAO;
    
    public ReviewServiceImpl(ReviewDAO reviewDAO, UserDAO userDAO, BookDAO bookDAO) {
        this.reviewDAO = reviewDAO;
        this.userDAO = userDAO;
        this.bookDAO = bookDAO;
    }
    
    @Override
    public ReviewResponse addReview(Integer userId, ReviewRequest request) {
        logger.info("Adding review - userId: {}, bookId: {}", userId, request.getBookId());
        
        // Validate input
        validateReviewRequest(request);
        
        // Check if book exists
        Book book = bookDAO.getBookById(request.getBookId());
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        
        // Check if user exists
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        
        // Check if user already reviewed this book (SỬA LỖI - thêm kiểm tra trùng lặp)
        Review existingReview = reviewDAO.getReviewByUserAndBook(userId, request.getBookId());
        if (existingReview != null) {
            throw new DuplicateResourceException("Bạn đã đánh giá sách này rồi");
        }
        
        // Create review
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        boolean created = reviewDAO.createReview(review);
        if (!created) {
            throw new RuntimeException("Không thể thêm đánh giá");
        }
        
        logger.info("Review added successfully for user {} on book {}", userId, request.getBookId());
        
        // Return the newly created review
        return getReviewByUserAndBook(userId, request.getBookId());
    }
    
    @Override
    public ReviewResponse updateReview(Integer reviewId, ReviewRequest request) {
        logger.info("Updating review - reviewId: {}", reviewId);
        
        // Validate input
        validateReviewRequest(request);
        
        // Check if review exists
        Review review = reviewDAO.getReviewById(reviewId);
        if (review == null) {
            throw new ResourceNotFoundException("Không tìm thấy đánh giá");
        }
        
        boolean updated = reviewDAO.updateReview(reviewId, request.getRating(), request.getComment());
        if (!updated) {
            throw new RuntimeException("Không thể cập nhật đánh giá");
        }
        
        logger.info("Review updated successfully - reviewId: {}", reviewId);
        
        Review updatedReview = reviewDAO.getReviewById(reviewId);
        return convertToResponse(updatedReview);
    }
    
    @Override
    public ReviewResponse getReviewById(Integer reviewId) {
        logger.info("Getting review by id: {}", reviewId);
        
        Review review = reviewDAO.getReviewById(reviewId);
        if (review == null) {
            throw new ResourceNotFoundException("Không tìm thấy đánh giá");
        }
        
        return convertToResponse(review);
    }
    
    @Override
    public boolean deleteReview(Integer reviewId) {
        logger.info("Deleting review - reviewId: {}", reviewId);
        
        Review review = reviewDAO.getReviewById(reviewId);
        if (review == null) {
            throw new ResourceNotFoundException("Không tìm thấy đánh giá");
        }
        
        return reviewDAO.deleteReview(reviewId);
    }
    
    @Override
    public PaginationResponse getReviewsByBook(Integer bookId, int page, int size) {
        logger.info("Getting reviews by book - bookId: {}, page: {}, size: {}", bookId, page, size);
        
        // Validate book exists
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        
        // Validate pagination parameters
        page = Math.max(0, page);
        size = Math.min(50, Math.max(1, size));
        
        List<Review> reviews = reviewDAO.getReviewsByBookId(bookId, page, size);
        int total = reviewDAO.getRatingCount(bookId);
        
        List<ReviewResponse> responses = reviews.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public PaginationResponse getReviewsByUser(Integer userId, int page, int size) {
        logger.info("Getting reviews by user - userId: {}, page: {}, size: {}", userId, page, size);
        
        // Validate user exists
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        
        // Validate pagination parameters
        page = Math.max(0, page);
        size = Math.min(50, Math.max(1, size));
        
        List<Review> reviews = reviewDAO.getReviewsByUserId(userId, page, size);
        int total = reviews.size(); // You may want to add a dedicated count method in ReviewDAO
        
        List<ReviewResponse> responses = reviews.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public ReviewResponse getReviewByUserAndBook(Integer userId, Integer bookId) {
        logger.info("Getting user review - userId: {}, bookId: {}", userId, bookId);
        
        // Validate user and book exist
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        
        Review review = reviewDAO.getReviewByUserAndBook(userId, bookId);
        if (review == null) {
            throw new ResourceNotFoundException("Bạn chưa đánh giá sách này");
        }
        
        return convertToResponse(review);
    }
    
    @Override
    public double getAverageRating(Integer bookId) {
        logger.info("Getting average rating for book: {}", bookId);
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        
        return reviewDAO.getAverageRating(bookId);
    }
    
    @Override
    public int getRatingCount(Integer bookId) {
        logger.info("Getting rating count for book: {}", bookId);
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        
        return reviewDAO.getRatingCount(bookId);
    }
    
    @Override
    public boolean hasUserReviewed(Integer userId, Integer bookId) {
        logger.info("Checking if user {} reviewed book {}", userId, bookId);
        
        Review review = reviewDAO.getReviewByUserAndBook(userId, bookId);
        return review != null;
    }
    
    /**
     * Validate review request
     */
    private void validateReviewRequest(ReviewRequest request) {
        if (request == null) {
            throw new InvalidInputException("Dữ liệu đánh giá không được để trống");
        }
        
        if (request.getBookId() == null || request.getBookId() <= 0) {
            throw new InvalidInputException("ID sách không hợp lệ");
        }
        
        if (request.getRating() == null) {
            throw new InvalidInputException("Vui lòng chọn số sao đánh giá");
        }
        
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new InvalidInputException("Số sao đánh giá phải từ 1 đến 5");
        }
        
        if (request.getComment() != null && request.getComment().length() > 1000) {
            throw new InvalidInputException("Nội dung đánh giá không được vượt quá 1000 ký tự");
        }
    }
    
    /**
     * Convert Review entity to ReviewResponse DTO
     */
    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setReviewId(review.getReviewId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        
        if (review.getUser() != null) {
            response.setUserId(review.getUser().getUserId());
            response.setUsername(review.getUser().getUsername());
        }
        
        if (review.getBook() != null) {
            response.setBookId(review.getBook().getBookId());
            response.setBookTitle(review.getBook().getTitle());
            response.setBookAuthor(review.getBook().getAuthor());
            response.setCoverImageUrl(review.getBook().getCoverImageUrl());
        }
        
        return response;
    }
}