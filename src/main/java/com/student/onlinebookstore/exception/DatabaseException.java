package com.student.onlinebookstore.exception;

public class DatabaseException extends RuntimeException {
    
    private String sqlState;
    private Integer errorCode;
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(String message, String sqlState, Integer errorCode) {
        super(message);
        this.sqlState = sqlState;
        this.errorCode = errorCode;
    }
    
    // Getters
    public String getSqlState() {
        return sqlState;
    }
    
    public Integer getErrorCode() {
        return errorCode;
    }
}
