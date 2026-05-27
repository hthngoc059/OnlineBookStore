package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.dao.CartDAO;
import com.student.onlinebookstore.dao.WishlistDAO;
import com.student.onlinebookstore.dto.request.LoginRequest;
import com.student.onlinebookstore.dto.request.RegisterRequest;
import com.student.onlinebookstore.dto.request.UpdateProfileRequest;  
import com.student.onlinebookstore.dto.response.AuthResponse;
import com.student.onlinebookstore.dto.response.UserResponse;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.exception.DuplicateResourceException;
import com.student.onlinebookstore.exception.InvalidCredentialsException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.util.JwtUtil;
import com.student.onlinebookstore.util.PasswordHashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private UserDAO userDAO;
    private CartDAO cartDAO;
    private WishlistDAO wishlistDAO;
    private JwtUtil jwtUtil;
    private PasswordHashGenerator passwordHashGenerator;
    
    // Constructor injection
    public UserServiceImpl(UserDAO userDAO, CartDAO cartDAO, WishlistDAO wishlistDAO, 
                          JwtUtil jwtUtil, PasswordHashGenerator passwordHashGenerator) {
        this.userDAO = userDAO;
        this.cartDAO = cartDAO;
        this.wishlistDAO = wishlistDAO;
        this.jwtUtil = jwtUtil;
        this.passwordHashGenerator = passwordHashGenerator;
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        
        User user = userDAO.getUserByEmail(request.getEmail());
        if (user == null) {
            throw new InvalidCredentialsException("Email hoặc password không đúng");
        }
        
        if (!passwordHashGenerator.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email hoặc password không đúng");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        UserResponse userResponse = new UserResponse(
            user.getUserId(), user.getUsername(), 
            user.getEmail(), user.getPhoneNumber(), 
            user.getRole().toString()
        );
        
        logger.info("Login successful for user: {}", user.getUsername());
        return new AuthResponse(token, userResponse);
    }
    
    @Override
    public AuthResponse adminLogin(LoginRequest request) {
        logger.info("Admin login attempt for email: {}", request.getEmail());
        
        User user = userDAO.getUserByEmail(request.getEmail());
        if (user == null || !User.Role.admin.equals(user.getRole())) {
            throw new InvalidCredentialsException("Email hoặc password không đúng");
        }
        
        if (!passwordHashGenerator.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email hoặc password không đúng");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        UserResponse userResponse = new UserResponse(
            user.getUserId(), user.getUsername(), 
            user.getEmail(), user.getPhoneNumber(), 
            user.getRole().toString()
        );
        
        logger.info("Admin login successful for user: {}", user.getUsername());
        return new AuthResponse(token, userResponse);
    }
    
    @Override
    public UserResponse register(RegisterRequest request) {
        logger.info("Registration attempt for email: {}", request.getEmail());
        
        // Check existing email
        if (userDAO.emailExists(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại");
        }
        
        // Check existing username
        if (userDAO.usernameExists(request.getUsername())) {
            throw new DuplicateResourceException("Username đã tồn tại");
        }
        
        // Check password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("Xác nhận mật khẩu không khớp");
        }
        
        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordHashGenerator.hashPassword(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(User.Role.user); // Default role
        
        boolean created = userDAO.createUser(user);
        if (!created) {
            throw new RuntimeException("Không thể tạo tài khoản");
        }
        
        // Get the created user
        User savedUser = userDAO.getUserByEmail(request.getEmail());
        
        // Create cart for user
        int cartId = cartDAO.getOrCreateCart(savedUser.getUserId());
        logger.info("Cart created with id: {} for user: {}", cartId, savedUser.getUserId());
        
        // Create wishlist for user
        int wishlistId = wishlistDAO.getOrCreateWishlist(savedUser.getUserId());
        logger.info("Wishlist created with id: {} for user: {}", wishlistId, savedUser.getUserId());
        
        UserResponse response = new UserResponse(
            savedUser.getUserId(), savedUser.getUsername(),
            savedUser.getEmail(), savedUser.getPhoneNumber(),
            savedUser.getRole().toString()
        );
        
        logger.info("Registration successful for user: {}", savedUser.getUsername());
        return response;
    }
    
    @Override
    public UserResponse getProfile(Integer userId) {
        logger.info("Getting profile for user id: {}", userId);
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        
        return new UserResponse(
            user.getUserId(), user.getUsername(),
            user.getEmail(), user.getPhoneNumber(),
            user.getRole().toString()
        );
    }
    
    @Override
    public UserResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        logger.info("Updating profile for user id: {}", userId);
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        
        // Update fields
        user.setUsername(request.getUsername());
        user.setPhoneNumber(request.getPhoneNumber());
        
        boolean updated = userDAO.updateUser(user);
        if (!updated) {
            throw new RuntimeException("Không thể cập nhật thông tin người dùng");
        }
        
        User updatedUser = userDAO.getUserById(userId);
        
        return new UserResponse(
            updatedUser.getUserId(), updatedUser.getUsername(),
            updatedUser.getEmail(), updatedUser.getPhoneNumber(),
            updatedUser.getRole().toString()
        );
    }
    
    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        logger.info("Changing password for user id: {}", userId);
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        
        if (!passwordHashGenerator.verifyPassword(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Mật khẩu cũ không đúng");
        }
        
        String hashedPassword = passwordHashGenerator.hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashedPassword);
    }
    
    @Override
    public PaginationResponse getAllUsers(int page, int size) {
        logger.info("Getting all users - page: {}, size: {}", page, size);
        
        List<User> users = userDAO.getAllUsers(page, size);
        int total = userDAO.getTotalUsers();
        
        List<UserResponse> responses = users.stream()
            .map(user -> new UserResponse(
                user.getUserId(), user.getUsername(),
                user.getEmail(), user.getPhoneNumber(),
                user.getRole().toString()
            ))
            .collect(Collectors.toList());
        
        return new PaginationResponse(responses, page, size, total);
    }
    
    @Override
    public UserResponse getUserById(Integer userId) {
        logger.info("Getting user by id: {}", userId);
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        
        return new UserResponse(
            user.getUserId(), user.getUsername(),
            user.getEmail(), user.getPhoneNumber(),
            user.getRole().toString()
        );
    }
    
    @Override
    public boolean deleteUser(Integer userId) {
        logger.info("Deleting user id: {}", userId);
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        
        return userDAO.deleteUser(userId);
    }
    
    @Override
    public boolean toggleUserStatus(Integer userId, boolean isActive) {
        logger.info("Toggling user status for user id: {} to {}", userId, isActive);
        // Implementation depends on your database schema
        return true;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        return userDAO.emailExists(email);
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        return userDAO.usernameExists(username);
    }
}