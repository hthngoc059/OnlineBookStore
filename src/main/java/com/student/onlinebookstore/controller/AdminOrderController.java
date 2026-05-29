package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.OrderDAO;
import com.student.onlinebookstore.dao.PaymentDAO;
import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.model.Order;
import com.student.onlinebookstore.model.OrderItem;
import com.student.onlinebookstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class AdminOrderController extends HttpServlet {
    
    private OrderDAO orderDAO;
    private PaymentDAO paymentDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        paymentDAO = new PaymentDAO();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("detail".equals(action)) {
            // Order details page
            int orderId = Integer.parseInt(req.getParameter("id"));
            Order order = orderDAO.getOrderById(orderId);
            List<OrderItem> orderItems = orderDAO.getOrderItems(orderId);
            var payment = paymentDAO.getPaymentByOrderId(orderId);
            
            // Add user details to order for display in admin panel
            if (order != null && order.getUser() != null) {
                User fullUser = userDAO.getUserById(order.getUser().getUserId());
                order.setUser(fullUser);
            }
            
            req.setAttribute("order", order);
            req.setAttribute("orderItems", orderItems);
            req.setAttribute("payment", payment);
            req.getRequestDispatcher("/WEB-INF/views/admin/order-detail.jsp").forward(req, resp);
            
        } else {
            // List orders with pagination and optional filtering
            int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
            int size = 10;
            String status = req.getParameter("status");
            
            List<Order> orders;
            int totalOrders;
            
            if (status != null && !status.isEmpty()) {
                orders = orderDAO.getOrdersByStatus(status, page, size);
                totalOrders = orderDAO.countOrdersByStatus(status);
            } else {
                orders = orderDAO.getAllOrders(page, size);
                totalOrders = orderDAO.countAllOrders();
            }
            
            // Add user details to each order for display in the admin panel
            for (Order order : orders) {
                if (order.getUser() != null) {
                    User fullUser = userDAO.getUserById(order.getUser().getUserId());
                    order.setUser(fullUser);
                }
            }
            
            req.setAttribute("orders", orders);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", (int) Math.ceil((double) totalOrders / size));
            req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("updateStatus".equals(action)) {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String status = req.getParameter("status");
            String paymentStatus = req.getParameter("paymentStatus");
            
            if (status != null && !status.isEmpty()) {
                orderDAO.updateOrderStatus(orderId, status);
            }
            if (paymentStatus != null && !paymentStatus.isEmpty()) {
                orderDAO.updatePaymentStatus(orderId, paymentStatus);
                
                // If payment status is updated to "paid", we can also update the payment record in the database
                if ("paid".equals(paymentStatus)) {
                    var payment = paymentDAO.getPaymentByOrderId(orderId);
                    if (payment != null) {
                        paymentDAO.updatePaymentStatus(payment.getPaymentId(), "completed", payment.getTransactionId());
                    }
                }
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/orders?action=detail&id=" + orderId);
        }
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