package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dto.request.LoginRequest;
import com.student.onlinebookstore.dto.request.RegisterRequest;
import com.student.onlinebookstore.dto.request.UpdateProfileRequest;
import com.student.onlinebookstore.dto.response.AuthResponse;
import com.student.onlinebookstore.dto.response.UserResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;

public interface UserService {
    
    // Authentication
    AuthResponse login(LoginRequest request);
    AuthResponse adminLogin(LoginRequest request);
    UserResponse register(RegisterRequest request);
    
    // User management
    UserResponse getProfile(Integer userId);
    UserResponse updateProfile(Integer userId, UpdateProfileRequest request);
    boolean changePassword(Integer userId, String oldPassword, String newPassword);
    
    // Admin functions
    PaginationResponse getAllUsers(int page, int size);
    UserResponse getUserById(Integer userId);
    boolean deleteUser(Integer userId);
    boolean toggleUserStatus(Integer userId, boolean isActive);
    
    // Validation
    boolean isEmailExists(String email);
    boolean isUsernameExists(String username);
}