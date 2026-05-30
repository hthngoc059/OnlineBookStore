package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.*;
import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.model.Order;
import com.student.onlinebookstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardController extends HttpServlet {
    
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private BookDAO bookDAO;
    private PaymentDAO paymentDAO;
    
    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        userDAO = new UserDAO();
        bookDAO = new BookDAO();
        paymentDAO = new PaymentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Check if user is logged in and is admin
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (!"admin".equals(currentUser.getRole().name())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        
        // Get dashboard data
        BigDecimal totalRevenue = paymentDAO.getTotalRevenue();
        req.setAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        int totalOrders = orderDAO.countAllOrders();
        req.setAttribute("totalOrders", totalOrders);
        
        int pendingOrders = orderDAO.countOrdersByStatus("pending");
        req.setAttribute("pendingOrders", pendingOrders);
        
        int totalUsers = userDAO.getTotalUsers();
        req.setAttribute("totalUsers", totalUsers);
        
        int totalBooks = bookDAO.getTotalBooks();
        req.setAttribute("totalBooks", totalBooks);
        
        // Get list of books that are running low on stock (stock <= 5)
        List<Book> allBooks = bookDAO.getAllBooks(0, 1000);
        List<Book> lowStockBooks = allBooks.stream()
                .filter(b -> b.getStockQuantity() > 0 && b.getStockQuantity() <= 5)
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
        req.setAttribute("lowStockBooksList", lowStockBooks);
        req.setAttribute("lowStockBooks", lowStockBooks.size());
        
        // Get recent orders (limit 10)
        List<Order> recentOrders = orderDAO.getAllOrders(0, 10);
        req.setAttribute("recentOrders", recentOrders);
        
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}