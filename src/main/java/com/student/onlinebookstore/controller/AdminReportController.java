package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.OrderDAO;
import com.student.onlinebookstore.dao.PaymentDAO;
import com.student.onlinebookstore.dao.ReviewDAO;
import com.student.onlinebookstore.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/reports")
public class AdminReportController extends HttpServlet {
    
    private OrderDAO orderDAO;
    private PaymentDAO paymentDAO;
    private ReviewDAO reviewDAO;
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        paymentDAO = new PaymentDAO();
        reviewDAO = new ReviewDAO();
        objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        // Lấy tham số ngày tháng
        String fromDateParam = req.getParameter("fromDate");
        String toDateParam = req.getParameter("toDate");
        
        LocalDate startDate;
        LocalDate endDate;
        
        if (fromDateParam != null && !fromDateParam.isEmpty() && 
            toDateParam != null && !toDateParam.isEmpty()) {
            startDate = LocalDate.parse(fromDateParam);
            endDate = LocalDate.parse(toDateParam);
        } else {
            // Mặc định: 30 ngày gần nhất
            endDate = LocalDate.now();
            startDate = endDate.minusDays(30);
        }
        
        req.setAttribute("fromDate", startDate.toString());
        req.setAttribute("toDate", endDate.toString());
        
        // Lấy tổng doanh thu
        BigDecimal totalRevenue = paymentDAO.getTotalRevenueByDateRange(startDate, endDate);
        int totalOrders = orderDAO.countOrdersByDateRange(startDate, endDate);
        double avgRating = reviewDAO.getAverageRating();
        
        req.setAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("avgRating", String.format("%.1f", avgRating));
        
        // Lấy top sách bán chạy
        List<Object[]> topBooksData = orderDAO.getTopSellingBooks(startDate, endDate, 10);
        List<Map<String, Object>> topSellingBooks = new ArrayList<>();
        for (Object[] book : topBooksData) {
            Map<String, Object> bookMap = new HashMap<>();
            bookMap.put("title", book[1]);
            bookMap.put("author", book[2]);
            bookMap.put("totalSold", book[3]);
            bookMap.put("revenue", book[4]);
            topSellingBooks.add(bookMap);
        }
        req.setAttribute("topSellingBooks", topSellingBooks);
        
        // ========== 1. XỬ LÝ DOANH THU THEO NGÀY ==========
        List<Object[]> dailyRevenue = orderDAO.getDailyRevenue(startDate, endDate);
        
        // Tạo map để dễ tra cứu
        Map<LocalDate, BigDecimal> revenueMap = new HashMap<>();
        for (Object[] dr : dailyRevenue) {
            if (dr[0] != null && dr[1] != null) {
                revenueMap.put((LocalDate) dr[0], (BigDecimal) dr[1]);
            }
        }
        
        // Tạo labels và data cho tất cả các ngày trong khoảng
        List<String> dateLabels = new ArrayList<>();
        List<Double> revenueData = new ArrayList<>();
        
        LocalDate current = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        while (!current.isAfter(endDate)) {
            dateLabels.add(current.format(formatter));
            BigDecimal revenue = revenueMap.getOrDefault(current, BigDecimal.ZERO);
            revenueData.add(revenue.doubleValue());
            current = current.plusDays(1);
        }
        
        // Chuyển đổi sang JSON
        String dateLabelsJson = objectMapper.writeValueAsString(dateLabels);
        String revenueDataJson = objectMapper.writeValueAsString(revenueData);
        
        System.out.println("dateLabelsJson: " + dateLabelsJson);
        System.out.println("revenueDataJson: " + revenueDataJson);
        
        req.setAttribute("dateLabelsJson", dateLabelsJson);
        req.setAttribute("revenueDataJson", revenueDataJson);
        
        // ========== 2. XỬ LÝ DOANH THU THEO PHƯƠNG THỨC THANH TOÁN ==========
        List<Object[]> paymentStats = orderDAO.getRevenueByPaymentMethod(startDate, endDate);
        
        List<String> paymentLabels = new ArrayList<>();
        List<Double> paymentData = new ArrayList<>();
        
        // Map để nhóm các phương thức thanh toán
        Map<String, Double> paymentMap = new HashMap<>();
        
        for (Object[] ps : paymentStats) {
            if (ps[0] != null && ps[1] != null) {
                String method = (String) ps[0];
                String displayName = "";
                switch (method) {
                    case "cod": displayName = "COD (Tiền mặt)"; break;
                    case "banking": displayName = "Chuyển khoản"; break;
                    case "momo": displayName = "Momo"; break;
                    case "vnpay": displayName = "VNPay"; break;
                    default: displayName = method;
                }
                double amount = ((BigDecimal) ps[1]).doubleValue();
                paymentMap.put(displayName, paymentMap.getOrDefault(displayName, 0.0) + amount);
            }
        }
        
        // Chuyển map thành lists
        for (Map.Entry<String, Double> entry : paymentMap.entrySet()) {
            paymentLabels.add(entry.getKey());
            paymentData.add(entry.getValue());
        }
        
        // Nếu không có dữ liệu, hiển thị thông báo
        if (paymentLabels.isEmpty()) {
            paymentLabels.add("Chưa có dữ liệu");
            paymentData.add(0.0);
        }
        
        // Chuyển đổi sang JSON
        String paymentLabelsJson = objectMapper.writeValueAsString(paymentLabels);
        String paymentDataJson = objectMapper.writeValueAsString(paymentData);
        
        System.out.println("paymentLabelsJson: " + paymentLabelsJson);
        System.out.println("paymentDataJson: " + paymentDataJson);
        
        req.setAttribute("paymentLabelsJson", paymentLabelsJson);
        req.setAttribute("paymentDataJson", paymentDataJson);
        
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