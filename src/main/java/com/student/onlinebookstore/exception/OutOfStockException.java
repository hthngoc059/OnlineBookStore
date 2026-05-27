package com.student.onlinebookstore.exception;

public class OutOfStockException extends RuntimeException {
    
    private Integer bookId;
    private String bookTitle;
    private Integer requestedQuantity;
    private Integer availableQuantity;
    
    public OutOfStockException(String message) {
        super(message);
    }
    
    public OutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OutOfStockException(Integer bookId, String bookTitle) {
        super("Sách \"" + bookTitle + "\" hiện đã hết hàng");
        this.bookId = bookId;
        this.bookTitle = bookTitle;
    }
    
    public OutOfStockException(Integer bookId, String bookTitle, int requestedQuantity, int availableQuantity) {
        super("Sách \"" + bookTitle + "\" chỉ còn " + availableQuantity + " bản trong kho, " +
              "bạn yêu cầu " + requestedQuantity + " bản");
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    // Getters
    public Integer getBookId() {
        return bookId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
}
