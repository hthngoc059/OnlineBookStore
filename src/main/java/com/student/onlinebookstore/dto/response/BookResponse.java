package com.student.onlinebookstore.dto.response;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Set;

public class BookResponse {
    private Integer bookId;
    private String coverImageUrl;
    private String title;
    private String author;
    private String description;
    private String isbn;
    private String publisher;
    private Year publishedYear;
    private String language;
    private BigDecimal price;
    private Integer stockQuantity;
    private String slug;
    private String format;
    private Boolean isAvailable;
    private Double averageRating;
    private Set<String> genres;
    private Integer ratingCount;
    
    public BookResponse() {}
    
    // Getters and Setters
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public Year getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Year publishedYear) { this.publishedYear = publishedYear; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Set<String> getGenres() { return genres; }
    public void setGenres(Set<String> genres) { this.genres = genres; }

    public int getRatingCount() { return ratingCount;}

    public void setRatingCount( int ratingCount) { this.ratingCount = ratingCount;}
}
