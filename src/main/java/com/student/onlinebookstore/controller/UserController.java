package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserController", urlPatterns = {"/user"})
public class UserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        forwardToHome(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) action = "";
        
        System.out.println("=== UserController.doPost() ===");
        System.out.println("Action: " + action);
        
        switch (action) {
            case "register" -> handleRegister(request, response);
            case "login"    -> handleLogin(request, response);
            default         -> forwardToHome(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username        = trim(request.getParameter("username"));
        String email           = trim(request.getParameter("email"));
        String phoneNumber     = trim(request.getParameter("phoneNumber"));
        String password        = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        boolean hasError = false;

        if (username.isEmpty()) {
            request.setAttribute("usernameError", "Vui lòng nhập tên đăng nhập."); hasError = true;
        } else if (username.length() < 3) {
            request.setAttribute("usernameError", "Tên đăng nhập tối thiểu 3 ký tự."); hasError = true;
        } else if (userDAO.usernameExists(username)) {
            request.setAttribute("usernameError", "Tên đăng nhập đã tồn tại."); hasError = true;
        }

        if (email.isEmpty()) {
            request.setAttribute("emailError", "Vui lòng nhập email."); hasError = true;
        } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            request.setAttribute("emailError", "Email không hợp lệ."); hasError = true;
        } else if (userDAO.emailExists(email)) {
            request.setAttribute("emailError", "Email này đã được đăng ký."); hasError = true;
        }

        if (password == null || password.isEmpty()) {
            request.setAttribute("passwordError", "Vui lòng nhập mật khẩu."); hasError = true;
        } else if (password.length() < 6) {
            request.setAttribute("passwordError", "Mật khẩu tối thiểu 6 ký tự."); hasError = true;
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("confirmPasswordError", "Vui lòng xác nhận mật khẩu."); hasError = true;
        } else if (!confirmPassword.equals(password)) {
            request.setAttribute("confirmPasswordError", "Mật khẩu xác nhận không khớp."); hasError = true;
        }

        if (hasError) { forwardToHome(request, response); return; }

        User user = new User(username, email, password,
                     phoneNumber.isEmpty() ? null : phoneNumber);
        if (userDAO.createUser(user)) {
            request.setAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        } else {
            request.setAttribute("registerError", "Đăng ký thất bại. Vui lòng thử lại.");
        }
        forwardToHome(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");

        System.out.println("=== handleLogin ===");
        System.out.println("Username input: " + username);
        System.out.println("Password: " + (password != null ? "***" : "null"));

        if (username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Login failed: Empty username or password");
            request.setAttribute("loginError", "Vui lòng nhập đầy đủ thông tin.");
            forwardToHome(request, response);
            return;
        }

        // ✅ SỬA: Lấy user bằng username hoặc email
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            user = userDAO.getUserByEmail(username);
            System.out.println("Trying with email: " + username);
        }
        
        if (user == null) {
            System.out.println("Login failed: User not found - " + username);
            request.setAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng.");
            forwardToHome(request, response);
            return;
        }
        
        System.out.println("User found: " + user.getUsername());
        System.out.println("User role: " + user.getRole());
        
        // Kiểm tra mật khẩu
        boolean passwordMatch = BCrypt.checkpw(password, user.getPassword());
        System.out.println("Password match: " + passwordMatch);
        
        if (!passwordMatch) {
            System.out.println("Login failed: Wrong password");
            request.setAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng.");
            forwardToHome(request, response);
            return;
        }

        // Lưu user vào session
        HttpSession session = request.getSession();
        session.setAttribute("currentUser", user);
        System.out.println("Session created for user: " + user.getUsername());
        
        // ✅ QUAN TRỌNG: Kiểm tra role để redirect
        if ("admin".equals(user.getRole().name())) {
            System.out.println("Admin user detected! Redirecting to /admin/dashboard");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        
        // User thường
        System.out.println("Regular user, forwarding to home");
        String redirectUrl = request.getParameter("redirectUrl");
        if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    private void forwardToHome(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Book> featuredBooks = bookDAO.getNewArrivals(8);
            List<Book> bestSellers = bookDAO.getBestSellers(8);
            request.setAttribute("featuredBooks", featuredBooks);
            request.setAttribute("bestSellers", bestSellers);
            System.out.println("Books loaded: featured=" + featuredBooks.size() + ", bestSellers=" + bestSellers.size());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("featuredBooks", new ArrayList<>());
            request.setAttribute("bestSellers", new ArrayList<>());
        }
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
}