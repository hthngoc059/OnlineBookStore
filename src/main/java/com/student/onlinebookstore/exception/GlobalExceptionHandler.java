package com.student.onlinebookstore.exception;

import com.student.onlinebookstore.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;

public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // For use with Spring Boot, add @ControllerAdvice and appropriate annotations
    // For now, this is a template class
    
    public ApiResponse handleOutOfStockException(OutOfStockException ex) {
        logger.error("Out of stock: {}", ex.getMessage());
        return new ApiResponse(false, ex.getMessage());
    }
    
    public ApiResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        return new ApiResponse(false, ex.getMessage());
    }
    
    public ApiResponse handleDuplicateResourceException(DuplicateResourceException ex) {
        logger.error("Duplicate resource: {}", ex.getMessage());
        return new ApiResponse(false, ex.getMessage());
    }
    
    public ApiResponse handleInvalidCredentialsException(InvalidCredentialsException ex) {
        logger.error("Invalid credentials: {}", ex.getMessage());
        return new ApiResponse(false, ex.getMessage());
    }
    
    public ApiResponse handleDatabaseException(DatabaseException ex) {
        logger.error("Database error: {}", ex.getMessage());
        return new ApiResponse(false, "Lỗi hệ thống, vui lòng thử lại sau");
    }
    
    public ApiResponse handleGenericException(Exception ex) {
        logger.error("Unexpected error: ", ex);
        return new ApiResponse(false, "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau");
    }
    
    // Validation error response
    public ApiResponse handleValidationErrors(Map<String, String> errors) {
        StringBuilder message = new StringBuilder("Dữ liệu không hợp lệ: ");
        for (Map.Entry<String, String> error : errors.entrySet()) {
            message.append(error.getValue()).append("; ");
        }
        return new ApiResponse(false, message.toString(), errors);
    }
}