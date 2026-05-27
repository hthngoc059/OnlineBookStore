package com.student.onlinebookstore.exception;

public class InvalidCredentialsException extends RuntimeException {
    
    private String email;
    private String username;
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String email, String message) {
        super(message);
        this.email = email;
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // Getters
    public String getEmail() {
        return email;
    }
    
    public String getUsername() {
        return username;
    }
}