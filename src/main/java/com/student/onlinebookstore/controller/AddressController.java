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
        this.addressDAO = ApplicationContextProvider.getBean(AddressDAO.class);
    }

    // ── GET: hiển thị danh sách địa chỉ ────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        
        // Kiểm tra session ngay từ đầu
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        List<Address> addresses = addressDAO.getAddressesByUserId(user.getUserId());
        request.setAttribute("addresses", addresses);
        request.getRequestDispatcher("/WEB-INF/views/address-management.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        
        // Kiểm tra session ngay từ đầu
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action    = request.getParameter("action");
        String addressIdStr = request.getParameter("addressId");

        // Kiểm tra addressId có tồn tại không (cho các action cần addressId)
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Thiếu thông tin địa chỉ.");
            response.sendRedirect(request.getContextPath() + "/profile/addresses");
            return;
        }

        try {
            int addressId = Integer.parseInt(addressIdStr);

            if ("setDefault".equals(action)) {
                addressDAO.setDefaultAddress(user.getUserId(), addressId);
                session.setAttribute("successMsg", "Đã thiết lập địa chỉ mặc định.");

            } else if ("delete".equals(action)) {
                boolean owned = addressDAO.getAddressesByUserId(user.getUserId())
                        .stream().anyMatch(a -> a.getAddressId() == addressId);
                if (owned) {
                    addressDAO.deleteAddress(addressId);
                    session.setAttribute("successMsg", "Đã xóa địa chỉ.");
                } else {
                    session.setAttribute("errorMsg", "Không thể xóa địa chỉ này.");
                }
            } else {
                session.setAttribute("errorMsg", "Hành động không hợp lệ.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMsg", "ID địa chỉ không hợp lệ.");
        }

        response.sendRedirect(request.getContextPath() + "/profile/addresses");
    }
}