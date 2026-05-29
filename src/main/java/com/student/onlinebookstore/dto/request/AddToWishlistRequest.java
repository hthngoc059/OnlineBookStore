package com.student.onlinebookstore.dto.request;

import jakarta.validation.constraints.NotNull;

public class AddToWishlistRequest {
    @NotNull(message = "Book ID cannot be null")
    private Integer bookId;
    
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
}