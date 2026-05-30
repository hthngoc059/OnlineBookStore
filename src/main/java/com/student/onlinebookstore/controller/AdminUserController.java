package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUserController extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("delete".equals(action)) {
            int userId = Integer.parseInt(req.getParameter("id"));
            userDAO.deleteUser(userId);
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }
        
        // Lấy tham số tìm kiếm và lọc
        int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
        int size = 10;
        String keyword = req.getParameter("keyword");
        String role = req.getParameter("role");
        
        List<User> users;
        int totalUsers;
        
        // Xử lý tìm kiếm và lọc
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm kiếm theo keyword (username hoặc email)
            users = userDAO.searchUsers(keyword, page, size);
            totalUsers = userDAO.getSearchCount(keyword);
        } else if (role != null && !role.isEmpty()) {
            // Lọc theo role
            users = userDAO.getUsersByRole(role, page, size);
            totalUsers = userDAO.getCountByRole(role);
        } else {
            // Lấy tất cả users
            users = userDAO.getAllUsers(page, size);
            totalUsers = userDAO.getTotalUsers();
        }
        
        req.setAttribute("users", users);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", (int) Math.ceil((double) totalUsers / size));
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
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