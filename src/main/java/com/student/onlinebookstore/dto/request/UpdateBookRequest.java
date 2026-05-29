package com.student.onlinebookstore.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class UpdateBookRequest {
    
    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách không được vượt quá 255 ký tự")
    private String title;
    
    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 255, message = "Tên tác giả không được vượt quá 255 ký tự")
    private String author;
    
    @NotBlank(message = "Mô tả không được để trống")
    private String description;
    
    @Size(max = 255, message = "Nhà xuất bản không được vượt quá 255 ký tự")
    private String publisher;
    
    @Size(max = 50, message = "Ngôn ngữ không được vượt quá 50 ký tự")
    private String language;
    
    @NotNull(message = "Giá sách không được để trống")
    @DecimalMin(value = "0.00", inclusive = false, message = "Giá sách phải lớn hơn 0")
    @DecimalMax(value = "99999999.99", message = "Giá sách không được vượt quá 99,999,999.99")
    private BigDecimal price;
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity;
    
    // Optional fields - only update if provided
    private String coverImageUrl;
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
