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
            
            // Add user details to order
            if (order != null && order.getUser() != null) {
                User fullUser = userDAO.getUserById(order.getUser().getUserId());
                order.setUser(fullUser);
            }
            
            req.setAttribute("order", order);
            req.setAttribute("orderItems", orderItems);
            req.setAttribute("payment", payment);
            req.getRequestDispatcher("/WEB-INF/views/admin/order-detail.jsp").forward(req, resp);
            
        } else {
            // List orders with pagination, filtering, and search
            int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
            int size = 10;
            String status = req.getParameter("status");
            String keyword = req.getParameter("keyword");
            
            List<Order> orders;
            int totalOrders;
            
            // Handle search by keyword (orderId or username)
            if (keyword != null && !keyword.trim().isEmpty()) {
                orders = orderDAO.searchOrders(keyword, page, size);
                totalOrders = orderDAO.countSearchOrders(keyword);
            } 
            // Handle filter by status
            else if (status != null && !status.isEmpty()) {
                orders = orderDAO.getOrdersByStatus(status, page, size);
                totalOrders = orderDAO.countOrdersByStatus(status);
            } 
            // Default: get all orders
            else {
                orders = orderDAO.getAllOrders(page, size);
                totalOrders = orderDAO.countAllOrders();
            }
            
            // Add user details to each order
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
        
        System.out.println("=== doPost called ===");
        System.out.println("Action: " + req.getParameter("action"));
        System.out.println("OrderId: " + req.getParameter("orderId"));
        
        if (!isAdmin(req, resp)) {
            System.out.println("isAdmin returned false");
            return;
        }
        
        String action = req.getParameter("action");
        
        if ("updateStatus".equals(action)) {
            try {
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                String status = req.getParameter("status");
                String paymentStatus = req.getParameter("paymentStatus");
                
                // Validate inputs
                if (status == null || status.isEmpty()) {
                    req.getSession().setAttribute("errorMessage", "Vui lòng chọn trạng thái đơn hàng!");
                    resp.sendRedirect(req.getContextPath() + "/admin/orders?action=detail&id=" + orderId);
                    return;
                }
                
                if (paymentStatus == null || paymentStatus.isEmpty()) {
                    req.getSession().setAttribute("errorMessage", "Vui lòng chọn trạng thái thanh toán!");
                    resp.sendRedirect(req.getContextPath() + "/admin/orders?action=detail&id=" + orderId);
                    return;
                }
                
                // Update both status and payment status in orders table
                boolean orderUpdated = orderDAO.updateOrderStatus(orderId, status);
                boolean paymentUpdated = orderDAO.updatePaymentStatus(orderId, paymentStatus);
                
                System.out.println("Order ID: " + orderId);
                System.out.println("New Status: " + status);
                System.out.println("New Payment Status: " + paymentStatus);
                System.out.println("Order Updated: " + orderUpdated);
                System.out.println("Payment Updated: " + paymentUpdated);
                
                if (orderUpdated || paymentUpdated) {
                    // Update payment record in payments table if needed
                    if ("paid".equals(paymentStatus)) {
                        var payment = paymentDAO.getPaymentByOrderId(orderId);
                        if (payment != null) {
                            paymentDAO.updatePaymentStatus(payment.getPaymentId(), "completed", payment.getTransactionId());
                        }
                    } else if ("refunded".equals(paymentStatus)) {
                        var payment = paymentDAO.getPaymentByOrderId(orderId);
                        if (payment != null) {
                            paymentDAO.updatePaymentStatus(payment.getPaymentId(), "refunded", payment.getTransactionId());
                        }
                    }
                    
                    req.getSession().setAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
                } else {
                    req.getSession().setAttribute("errorMessage", "Không có thay đổi nào được thực hiện!");
                }
                
                resp.sendRedirect(req.getContextPath() + "/admin/orders?action=detail&id=" + orderId);
                
            } catch (NumberFormatException e) {
                e.printStackTrace();
                req.getSession().setAttribute("errorMessage", "Mã đơn hàng không hợp lệ!");
                resp.sendRedirect(req.getContextPath() + "/admin/orders");
            } catch (Exception e) {
                e.printStackTrace();
                req.getSession().setAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/admin/orders?action=detail&id=" + req.getParameter("orderId"));
            }
        }
    }
    
    private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return false;
        }
        User currentUser = (User) session.getAttribute("currentUser");
        if (!"admin".equals(currentUser.getRole().name().toLowerCase())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return false;
        }
        return true;
    }
}