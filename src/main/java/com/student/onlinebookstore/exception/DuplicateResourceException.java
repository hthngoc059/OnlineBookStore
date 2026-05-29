package com.student.onlinebookstore.exception;

public class DuplicateResourceException extends RuntimeException {
    
    private String resourceType;
    private String fieldName;
    private String fieldValue;
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String resourceType, String fieldName, String fieldValue) {
        super(resourceType + " với " + fieldName + " '" + fieldValue + "' đã tồn tại");
        this.resourceType = resourceType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    // Getters
    public String getResourceType() {
        return resourceType;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }
}