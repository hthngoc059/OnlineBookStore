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
    private UserDAO userDAO;

    @Override
    public void init() {
        this.addressDAO = ApplicationContextProvider.getBean(AddressDAO.class);
        this.userDAO    = ApplicationContextProvider.getBean(UserDAO.class);
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

        // Load địa chỉ cho tab Địa chỉ
        req.setAttribute("addresses",
            addressDAO.getAddressesByUserId(user.getUserId()));

        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        // Kiểm tra session ngay từ đầu
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String action = req.getParameter("action");

        // Xử lý đổi mật khẩu
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
                boolean updated = userDAO.updatePassword(user.getUserId(), newPassword);
                if (updated) {
                    // Lấy lại user mới từ database
                    User updatedUser = userDAO.getUserById(user.getUserId());
                    session.setAttribute("currentUser", updatedUser);
                    req.setAttribute("successMsg", "Đổi mật khẩu thành công!");
                    user = updatedUser; // Cập nhật user để dùng sau
                } else {
                    req.setAttribute("errorMsg", "Đổi mật khẩu thất bại, vui lòng thử lại.");
                }
            }

        } 
        // Xử lý thiết lập địa chỉ mặc định
        else if ("setDefault".equals(action)) {
            try {
                int addressId = Integer.parseInt(req.getParameter("addressId"));
                boolean owned = addressDAO.getAddressesByUserId(user.getUserId())
                        .stream().anyMatch(a -> a.getAddressId() == addressId);
                if (owned) {
                    addressDAO.setDefaultAddress(user.getUserId(), addressId);
                    req.setAttribute("successMsg", "Đã thiết lập địa chỉ mặc định.");
                } else {
                    req.setAttribute("errorMsg", "Không thể thiết lập địa chỉ này.");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("errorMsg", "Địa chỉ không hợp lệ.");
            }

        } 
        // Xử lý xóa địa chỉ
        else if ("deleteAddress".equals(action)) {
            try {
                int addressId = Integer.parseInt(req.getParameter("addressId"));
                boolean owned = addressDAO.getAddressesByUserId(user.getUserId())
                        .stream().anyMatch(a -> a.getAddressId() == addressId);
                if (owned) {
                    addressDAO.deleteAddress(addressId);
                    req.setAttribute("successMsg", "Đã xóa địa chỉ.");
                } else {
                    req.setAttribute("errorMsg", "Không thể xóa địa chỉ này.");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("errorMsg", "Địa chỉ không hợp lệ.");
            }

        } 
        // Cập nhật số điện thoại (mặc định)
        else {
            String phoneNumber = req.getParameter("phoneNumber");
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                try {
                    userDAO.updatePhoneNumber(user.getUserId(), phoneNumber);
                    user.setPhoneNumber(phoneNumber);
                    session.setAttribute("currentUser", user);
                    req.setAttribute("successMsg", "Cập nhật thành công!");
                } catch (Exception e) {
                    req.setAttribute("errorMsg", "Có lỗi xảy ra, vui lòng thử lại.");
                }
            } else {
                req.setAttribute("errorMsg", "Số điện thoại không được để trống.");
            }
        }

        // Reload địa chỉ trước khi forward lại
        req.setAttribute("addresses", addressDAO.getAddressesByUserId(user.getUserId()));
        req.setAttribute("user", user);
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }
}