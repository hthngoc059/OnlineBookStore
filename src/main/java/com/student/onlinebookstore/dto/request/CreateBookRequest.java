package com.student.onlinebookstore.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateBookRequest {
    
    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách không được vượt quá 255 ký tự")
    private String title;
    
    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 255, message = "Tên tác giả không được vượt quá 255 ký tự")
    private String author;
    
    @NotBlank(message = "Mô tả không được để trống")
    private String description;
    
    @Pattern(regexp = "^(?:\\d{13}|\\d{10})$", message = "ISBN phải có 10 hoặc 13 chữ số")
    private String isbn;
    
    @Size(max = 255, message = "Nhà xuất bản không được vượt quá 255 ký tự")
    private String publisher;
    
    @Min(value = 1000, message = "Năm xuất bản phải từ 1000 đến năm hiện tại")
    @Max(value = 2026, message = "Năm xuất bản không được vượt quá năm hiện tại")
    private Integer publishedYear;
    
    @Size(max = 50, message = "Ngôn ngữ không được vượt quá 50 ký tự")
    private String language;
    
    @NotNull(message = "Giá sách không được để trống")
    @DecimalMin(value = "0.00", inclusive = false, message = "Giá sách phải lớn hơn 0")
    @DecimalMax(value = "99999999.99", message = "Giá sách không được vượt quá 99,999,999.99")
    private BigDecimal price;
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity;
    
    @NotNull(message = "Định dạng sách không được để trống")
    @Pattern(regexp = "paperback|hardcover|ebook|audiobook", 
             message = "Định dạng sách phải là: paperback, hardcover, ebook hoặc audiobook")
    private String format;
    
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
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public Integer getPublishedYear() {
        return publishedYear;
    }
    
    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
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
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
}