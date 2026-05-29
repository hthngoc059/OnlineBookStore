package com.student.onlinebookstore.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardResponse {
    // Order statistics
    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long shippingOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    
    // Revenue statistics
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal thisWeekRevenue;
    private BigDecimal thisMonthRevenue;
    
    // Book statistics
    private long totalBooks;
    private long outOfStockBooks;
    private long lowStockBooks;
    
    // User statistics
    private long totalUsers;
    private long newUsersThisMonth;
    
    // Top selling books
    private List<BookResponse> topSellingBooks;
    
    // Revenue by payment method
    private Map<String, BigDecimal> revenueByPaymentMethod;
    
    // Recent orders
    private List<OrderResponse> recentOrders;
    
    // Getters and Setters
    public long getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public long getPendingOrders() {
        return pendingOrders;
    }
    
    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }
    
    public long getConfirmedOrders() {
        return confirmedOrders;
    }
    
    public void setConfirmedOrders(long confirmedOrders) {
        this.confirmedOrders = confirmedOrders;
    }
    
    public long getShippingOrders() {
        return shippingOrders;
    }
    
    public void setShippingOrders(long shippingOrders) {
        this.shippingOrders = shippingOrders;
    }
    
    public long getDeliveredOrders() {
        return deliveredOrders;
    }
    
    public void setDeliveredOrders(long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }
    
    public long getCancelledOrders() {
        return cancelledOrders;
    }
    
    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }
    
    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }
    
    public BigDecimal getThisWeekRevenue() {
        return thisWeekRevenue;
    }
    
    public void setThisWeekRevenue(BigDecimal thisWeekRevenue) {
        this.thisWeekRevenue = thisWeekRevenue;
    }
    
    public BigDecimal getThisMonthRevenue() {
        return thisMonthRevenue;
    }
    
    public void setThisMonthRevenue(BigDecimal thisMonthRevenue) {
        this.thisMonthRevenue = thisMonthRevenue;
    }
    
    public long getTotalBooks() {
        return totalBooks;
    }
    
    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }
    
    public long getOutOfStockBooks() {
        return outOfStockBooks;
    }
    
    public void setOutOfStockBooks(long outOfStockBooks) {
        this.outOfStockBooks = outOfStockBooks;
    }
    
    public long getLowStockBooks() {
        return lowStockBooks;
    }
    
    public void setLowStockBooks(long lowStockBooks) {
        this.lowStockBooks = lowStockBooks;
    }
    
    public long getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public long getNewUsersThisMonth() {
        return newUsersThisMonth;
    }
    
    public void setNewUsersThisMonth(long newUsersThisMonth) {
        this.newUsersThisMonth = newUsersThisMonth;
    }
    
    public List<BookResponse> getTopSellingBooks() {
        return topSellingBooks;
    }
    
    public void setTopSellingBooks(List<BookResponse> topSellingBooks) {
        this.topSellingBooks = topSellingBooks;
    }
    
    public Map<String, BigDecimal> getRevenueByPaymentMethod() {
        return revenueByPaymentMethod;
    }
    
    public void setRevenueByPaymentMethod(Map<String, BigDecimal> revenueByPaymentMethod) {
        this.revenueByPaymentMethod = revenueByPaymentMethod;
    }
    
    public List<OrderResponse> getRecentOrders() {
        return recentOrders;
    }
    
    public void setRecentOrders(List<OrderResponse> recentOrders) {
        this.recentOrders = recentOrders;
    }
}