package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.config.ApplicationContextProvider;
import com.student.onlinebookstore.dao.AddressDAO;
import com.student.onlinebookstore.dao.UserDAO;
import com.student.onlinebookstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private AddressDAO addressDAO;

    @Override
    public void init() {
        this.addressDAO = ApplicationContextProvider.getBean(AddressDAO.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        req.setAttribute("user", user);
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/"); return; }

        String action = req.getParameter("action");
        UserDAO userDAO = ApplicationContextProvider.getBean(UserDAO.class);

        if ("password".equals(action)) {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword     = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            if (!org.mindrot.jbcrypt.BCrypt.checkpw(currentPassword, user.getPassword())) {
                req.setAttribute("errorMsg", "Mật khẩu hiện tại không đúng.");

            } else if (newPassword == null || newPassword.length() < 6) {
                req.setAttribute("errorMsg", "Mật khẩu mới phải có ít nhất 6 ký tự.");

            } else if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("errorMsg", "Xác nhận mật khẩu không khớp.");

            } else {
                userDAO.updatePassword(user.getUserId(), newPassword);

                // Cập nhật session với hash mới
                String newHashed = org.mindrot.jbcrypt.BCrypt.hashpw(
                    newPassword, org.mindrot.jbcrypt.BCrypt.gensalt()
                );
                user.setPassword(newHashed);
                session.setAttribute("currentUser", user);
                req.setAttribute("successMsg", "Đổi mật khẩu thành công!");
            }

        } else {
            // Cập nhật số điện thoại
            String phoneNumber = req.getParameter("phoneNumber");
            try {
                userDAO.updatePhoneNumber(user.getUserId(), phoneNumber);
                user.setPhoneNumber(phoneNumber);
                session.setAttribute("currentUser", user);
                req.setAttribute("successMsg", "Cập nhật thành công!");
            } catch (Exception e) {
                req.setAttribute("errorMsg", "Có lỗi xảy ra, vui lòng thử lại.");
            }
        }

        req.setAttribute("user", user);
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }
}