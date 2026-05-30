package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.OrderDAO;
import com.student.onlinebookstore.dao.PaymentDAO;
import com.student.onlinebookstore.dao.ReviewDAO;
import com.student.onlinebookstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/reports")
public class AdminReportController extends HttpServlet {
    
    private OrderDAO orderDAO;
    private PaymentDAO paymentDAO;
    private ReviewDAO reviewDAO;
    
    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        paymentDAO = new PaymentDAO();
        reviewDAO = new ReviewDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        // Lấy báo cáo doanh thu
        BigDecimal totalRevenue = paymentDAO.getTotalRevenue();
        int totalOrders = orderDAO.countAllOrders();
        double avgRating = reviewDAO.getAverageRating(0); // Cần điều chỉnh
        
        req.setAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("avgRating", String.format("%.1f", avgRating));
        
        // Top sách bán chạy (dữ liệu mẫu, cần implement trong DAO)
        List<Object[]> topBooks = new ArrayList<>();
        req.setAttribute("topSellingBooks", topBooks);
        
        // Dữ liệu cho biểu đồ
        List<String> dateLabels = new ArrayList<>();
        List<Double> revenueData = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            dateLabels.add(LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("dd/MM")));
            revenueData.add(0.0);
        }
        req.setAttribute("dateLabels", dateLabels);
        req.setAttribute("revenueData", revenueData);
        
        // Dữ liệu cho biểu đồ tròn
        List<String> paymentLabels = List.of("COD", "Chuyển khoản", "Momo", "VNPay");
        List<Double> paymentData = List.of(60.0, 20.0, 15.0, 5.0);
        req.setAttribute("paymentLabels", paymentLabels);
        req.setAttribute("paymentData", paymentData);
        
        req.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(req, resp);
    }
    
    private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return false;
        }
        User currentUser = (User) session.getAttribute("currentUser");
        if (!"admin".equals(currentUser.getRole().name())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return false;
        }
        return true;
    }
}