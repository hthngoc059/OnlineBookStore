package com.student.onlinebookstore.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    private String resourceType;
    private String fieldName;
    private Object fieldValue;
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceType, Object id) {
        super(resourceType + " với ID '" + id + "' không tồn tại");
        this.resourceType = resourceType;
        this.fieldName = "id";
        this.fieldValue = id;
    }
    
    public ResourceNotFoundException(String resourceType, String fieldName, Object fieldValue) {
        super(resourceType + " với " + fieldName + " '" + fieldValue + "' không tồn tại");
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
    
    public Object getFieldValue() {
        return fieldValue;
    }
}