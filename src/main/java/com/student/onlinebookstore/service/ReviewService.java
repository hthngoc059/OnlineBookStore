package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.request.ReviewRequest;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.dto.response.ReviewResponse;

public interface ReviewService {
    
    // CRUD operations
    ReviewResponse addReview(Integer userId, ReviewRequest request);
    ReviewResponse updateReview(Integer reviewId, ReviewRequest request);
    ReviewResponse getReviewById(Integer reviewId);
    boolean deleteReview(Integer reviewId);
    
    // Get reviews
    PaginationResponse getReviewsByBook(Integer bookId, int page, int size);
    PaginationResponse getReviewsByUser(Integer userId, int page, int size);
    ReviewResponse getReviewByUserAndBook(Integer userId, Integer bookId);  
    
    // Statistics
    double getAverageRating(Integer bookId);
    int getRatingCount(Integer bookId);
    boolean hasUserReviewed(Integer userId, Integer bookId);
}