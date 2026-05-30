package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.NotificationDAO;
import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.model.Notification;
import com.student.onlinebookstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/notifications")
public class AdminNotificationController extends HttpServlet {
    
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        notificationDAO = new NotificationDAO();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("delete".equals(action)) {
            int notificationId = Integer.parseInt(req.getParameter("id"));
            notificationDAO.deleteNotification(notificationId);
            resp.sendRedirect(req.getContextPath() + "/admin/notifications");
            return;
        }
        
        // Get all notifications (with pagination if needed)
        List<Notification> notifications = notificationDAO.getAllNotifications(0, 100);
        
        // Fetch user details for each notification to display username/email in the admin panel
        for (Notification notif : notifications) {
            User user = userDAO.getUserById(notif.getUser().getUserId());
            if (user != null) {
                notif.getUser().setUsername(user.getUsername());
                notif.getUser().setEmail(user.getEmail());
            }
        }
        
        req.setAttribute("notifications", notifications);
        req.getRequestDispatcher("/WEB-INF/views/admin/notifications.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("send".equals(action)) {
            String target = req.getParameter("target");
            String title = req.getParameter("title");
            String message = req.getParameter("message");
            
            boolean success = false;
            
            if ("all".equals(target)) {
                success = notificationDAO.createNotificationForAllUsers(title, message);
            } else if ("users".equals(target)) {
                success = notificationDAO.createNotificationForRole("user", title, message);
            } else if ("admins".equals(target)) {
                success = notificationDAO.createNotificationForRole("admin", title, message);
            }
            
            if (success) {
                req.getSession().setAttribute("notifySuccess", "Đã gửi thông báo thành công!");
            } else {
                req.getSession().setAttribute("notifyError", "Gửi thông báo thất bại!");
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/notifications");
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