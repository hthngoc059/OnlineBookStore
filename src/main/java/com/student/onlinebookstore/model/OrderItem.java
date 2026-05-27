package com.student.onlinebookstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    private Integer quantity;
    
    @Column(name = "price_at_time", precision = 10, scale = 2)
    private BigDecimal priceAtTime;
    
    public OrderItem() {}
    
    public OrderItem(Order order, Book book, Integer quantity, BigDecimal priceAtTime) {
        this.order = order;
        this.book = book;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }
    
    // Getters and Setters
    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPriceAtTime() { return priceAtTime; }
    public void setPriceAtTime(BigDecimal priceAtTime) { this.priceAtTime = priceAtTime; }
}