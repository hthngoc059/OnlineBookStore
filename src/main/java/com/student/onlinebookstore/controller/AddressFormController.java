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

@WebServlet("/address")
public class AddressFormController extends HttpServlet {

    private AddressDAO addressDAO;

    @Override
    public void init() throws ServletException {
        this.addressDAO = ApplicationContextProvider.getBean(AddressDAO.class);
    }

    // ── GET ─────────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = getUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String action = req.getParameter("action");

        if ("edit".equals(action)) {
            // Chỉnh sửa: load địa chỉ cũ lên form
            String idStr = req.getParameter("id");
            if (idStr == null) {
                resp.sendRedirect(req.getContextPath() + "/profile/addresses");
                return;
            }
            try {
                int addressId = Integer.parseInt(idStr);

                // Kiểm tra quyền sở hữu
                boolean owned = addressDAO.getAddressesByUserId(user.getUserId())
                        .stream().anyMatch(a -> a.getAddressId() == addressId);
                if (!owned) {
                    resp.sendRedirect(req.getContextPath() + "/profile/addresses");
                    return;
                }

                Address address = addressDAO.getAddressById(addressId);
                req.setAttribute("address", address);
                req.setAttribute("editMode", true);

            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/profile/addresses");
                return;
            }
        }
        // Cả thêm mới lẫn sửa đều dùng chung address.jsp
        req.getRequestDispatcher("/WEB-INF/views/address.jsp").forward(req, resp);
    }

    // ── POST ────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = getUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        // Đọc form fields
        String fullName    = trim(req.getParameter("fullName"));
        String phone       = trim(req.getParameter("phone"));
        String addressLine = trim(req.getParameter("addressLine"));
        String ward        = trim(req.getParameter("ward"));
        String district    = trim(req.getParameter("district"));
        String city        = trim(req.getParameter("city"));
        boolean isDefault  = "on".equals(req.getParameter("isDefault"))
                          || "true".equals(req.getParameter("isDefault"));

        // Validate
        if (fullName.isEmpty() || phone.isEmpty() || addressLine.isEmpty()
                || ward.isEmpty() || district.isEmpty() || city.isEmpty()) {
            session.setAttribute("errorMsg", "Vui lòng điền đầy đủ thông tin địa chỉ.");
            String redirect = "edit".equals(action)
                    ? req.getContextPath() + "/address?action=edit&id=" + req.getParameter("addressId")
                    : req.getContextPath() + "/address";
            resp.sendRedirect(redirect);
            return;
        }

        if ("edit".equals(action)) {
            // ── Cập nhật ──
            String idStr = req.getParameter("addressId");
            try {
                int addressId = Integer.parseInt(idStr);

                // Kiểm tra quyền
                boolean owned = addressDAO.getAddressesByUserId(user.getUserId())
                        .stream().anyMatch(a -> a.getAddressId() == addressId);
                if (!owned) {
                    session.setAttribute("errorMsg", "Không thể cập nhật địa chỉ này.");
                    resp.sendRedirect(req.getContextPath() + "/profile/addresses");
                    return;
                }

                Address address = new Address();
                address.setAddressId(addressId);
                address.setFullName(fullName);
                address.setPhone(phone);
                address.setAddressLine(addressLine);
                address.setWard(ward);
                address.setDistrict(district);
                address.setCity(city);

                boolean updated = addressDAO.updateAddress(address);

                if (updated) {
                    // Xử lý thiết lập mặc định nếu được tích
                    if (isDefault) {
                        addressDAO.setDefaultAddress(user.getUserId(), addressId);
                    }
                    session.setAttribute("successMsg", "Cập nhật địa chỉ thành công.");
                } else {
                    session.setAttribute("errorMsg", "Không thể cập nhật địa chỉ. Vui lòng thử lại.");
                }

            } catch (NumberFormatException e) {
                session.setAttribute("errorMsg", "Yêu cầu không hợp lệ.");
            }

        } else {
            // ── Thêm mới ──
            Address address = new Address();
            User ownerRef = new User();
            ownerRef.setUserId(user.getUserId());
            address.setUser(ownerRef);
            address.setFullName(fullName);
            address.setPhone(phone);
            address.setAddressLine(addressLine);
            address.setWard(ward);
            address.setDistrict(district);
            address.setCity(city);

            // Nếu user chưa có địa chỉ nào → tự động đặt làm mặc định
            List<Address> existing = addressDAO.getAddressesByUserId(user.getUserId());
            address.setIsDefault(isDefault || existing.isEmpty());

            boolean created = addressDAO.createAddress(address);

            if (created) {
                session.setAttribute("successMsg", "Thêm địa chỉ mới thành công.");
            } else {
                session.setAttribute("errorMsg", "Không thể thêm địa chỉ. Vui lòng thử lại.");
            }
        }

        resp.sendRedirect(req.getContextPath() + "/profile/addresses");
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private User getUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("currentUser") : null;
    }

    private String trim(String s) {
        return (s == null) ? "" : s.trim();
    }
}