package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.config.ApplicationContextProvider;
import com.student.onlinebookstore.dao.AddressDAO;
import com.student.onlinebookstore.model.Address;
import com.student.onlinebookstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/profile/addresses")
public class AddressController extends HttpServlet {

    private AddressDAO addressDAO;

    @Override
    public void init() throws ServletException {
        // Dùng ApplicationContextProvider nếu project dùng Spring, hoặc new AddressDAO() nếu không
        this.addressDAO = ApplicationContextProvider.getBean(AddressDAO.class);
    }

    // ── GET: hiển thị danh sách địa chỉ ────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        List<Address> addresses = addressDAO.getAddressesByUserId(user.getUserId());
        request.setAttribute("addresses", addresses);
        request.getRequestDispatcher("/WEB-INF/views/address-management.jsp")
               .forward(request, response);
    }

    // ── POST: setDefault / delete ────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action    = request.getParameter("action");
        String addressIdStr = request.getParameter("addressId");

        try {
            int addressId = Integer.parseInt(addressIdStr);

            if ("setDefault".equals(action)) {
                // Bỏ mặc định tất cả → đặt mặc định cho địa chỉ được chọn
                addressDAO.setDefaultAddress(user.getUserId(), addressId);
                session.setAttribute("successMsg", "Đã thiết lập địa chỉ mặc định.");

            } else if ("delete".equals(action)) {
                // Kiểm tra địa chỉ thuộc về user trước khi xóa (tránh IDOR)
                // getAddressById không set user nên kiểm tra qua getAddressesByUserId
                boolean owned = addressDAO.getAddressesByUserId(user.getUserId())
                        .stream().anyMatch(a -> a.getAddressId() == addressId);
                if (owned) {
                    addressDAO.deleteAddress(addressId);
                    session.setAttribute("successMsg", "Đã xóa địa chỉ.");
                } else {
                    session.setAttribute("errorMsg", "Không thể xóa địa chỉ này.");
                }
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMsg", "Yêu cầu không hợp lệ.");
        }

        response.sendRedirect(request.getContextPath() + "/profile/addresses");
    }
}