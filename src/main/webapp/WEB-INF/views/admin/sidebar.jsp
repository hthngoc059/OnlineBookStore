<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<div class="sidebar">
    <div class="sidebar-header">
        <h2>Admin Panel</h2>
        <p>storyshop</p>
    </div>
    
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item ${param.page == 'dashboard' ? 'active' : ''}">
            <span class="nav-icon"></span>
            <span>Tổng quan</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/books" class="nav-item ${param.page == 'books' ? 'active' : ''}">
            <span class="nav-icon"></span>
            <span>Quản lý sách</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/orders" class="nav-item ${param.page == 'orders' ? 'active' : ''}">
            <span class="nav-icon"></span>
            <span>Quản lý đơn hàng</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/users" class="nav-item ${param.page == 'users' ? 'active' : ''}">
            <span class="nav-icon"></span>
            <span>Quản lý người dùng</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/discounts" class="nav-item ${param.page == 'discounts' ? 'active' : ''}">
            <span class="nav-icon">️</span>
            <span>Mã giảm giá</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/notifications" class="nav-item ${param.page == 'notifications' ? 'active' : ''}">
            <span class="nav-icon"></span>
            <span>Thông báo</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item ${param.page == 'reports' ? 'active' : ''}">
            <span class="nav-icon"></span>
            <span>Báo cáo</span>
        </a>
    </nav>
    
    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/" class="nav-item">
            <span class="nav-icon"></span>
            <span>Trở về</span>
        </a>
        <a href="${pageContext.request.contextPath}/user?action=logout" class="nav-item">
            <span class="nav-icon"></span>
            <span>Đăng xuất</span>
        </a>
    </div>
</div>