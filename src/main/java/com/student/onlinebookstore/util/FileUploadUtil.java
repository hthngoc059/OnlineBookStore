package com.student.onlinebookstore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);
    
    // Configuration - can be moved to properties file
    private static final String UPLOAD_DIR = "uploads/books/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    
    // Upload file to server
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("File is empty or null");
            return null;
        }
        
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            logger.error("File size exceeds limit: {} bytes", file.getSize());
            throw new RuntimeException("File size exceeds maximum allowed size of 5MB");
        }
        
        // Validate file type
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        if (!isAllowedExtension(extension)) {
            logger.error("File extension not allowed: {}", extension);
            throw new RuntimeException("File type not allowed. Allowed types: jpg, jpeg, png, gif, webp");
        }
        
        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", UPLOAD_DIR);
            }
            
            // Generate unique filename
            String fileName = generateFileName(originalFilename);
            Path filePath = uploadPath.resolve(fileName);
            
            // Save file
            Files.copy(file.getInputStream(), filePath);
            
            String fileUrl = "/" + UPLOAD_DIR + fileName;
            logger.info("File uploaded successfully: {}", fileUrl);
            
            return fileUrl;
            
        } catch (IOException e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
    
    // Delete file from server
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        try {
            // Remove leading slash if present
            String relativePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Path filePath = Paths.get(relativePath);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("File deleted successfully: {}", fileUrl);
                return true;
            } else {
                logger.warn("File not found: {}", fileUrl);
                return false;
            }
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", e.getMessage());
            return false;
        }
    }
    
    // Get file extension
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    // Check if file extension is allowed
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    // Generate unique filename
    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId + "." + extension;
    }
    
    //Get file size in human readable format
    public static String getFileSizeReadable(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
