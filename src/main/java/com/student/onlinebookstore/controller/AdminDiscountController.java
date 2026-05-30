package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.DiscountDAO;
import com.student.onlinebookstore.model.Discount;
import com.student.onlinebookstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/admin/discounts")
public class AdminDiscountController extends HttpServlet {
    
    private DiscountDAO discountDAO;
    
    @Override
    public void init() throws ServletException {
        discountDAO = new DiscountDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("add".equals(action)) {
            // Display add discount form
            req.getRequestDispatcher("/WEB-INF/views/admin/add-discount.jsp").forward(req, resp);
            return;
            
        } else if ("edit".equals(action)) {
            // Display edit discount form
            int id = Integer.parseInt(req.getParameter("id"));
            Discount discount = discountDAO.getDiscountById(id);
            req.setAttribute("discount", discount);
            req.getRequestDispatcher("/WEB-INF/views/admin/edit-discount.jsp").forward(req, resp);
            return;
            
        } else if ("delete".equals(action)) {
            // Delete discount
            int id = Integer.parseInt(req.getParameter("id"));
            discountDAO.deleteDiscount(id);
            resp.sendRedirect(req.getContextPath() + "/admin/discounts");
            return;
            
        } else if ("toggle".equals(action)) {
            // Toggle discount active status
            int id = Integer.parseInt(req.getParameter("id"));
            Discount discount = discountDAO.getDiscountById(id);
            if (discount != null) {
                discountDAO.updateDiscountStatus(id, !discount.getIsActive());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/discounts");
            return;
        }
        
        // List discounts with pagination
        int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
        int size = 10;
        
        List<Discount> discounts = discountDAO.getAllDiscounts(page, size);
        int totalDiscounts = discountDAO.countDiscounts();
        
        req.setAttribute("discounts", discounts);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", (int) Math.ceil((double) totalDiscounts / size));
        req.setAttribute("now", LocalDateTime.now());
        
        req.getRequestDispatcher("/WEB-INF/views/admin/discounts.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("create".equals(action)) {
            // Add new discount
            Discount discount = new Discount();
            discount.setCode(req.getParameter("code").toUpperCase());
            discount.setDescription(req.getParameter("description"));
            discount.setDiscountType(Discount.DiscountType.valueOf(req.getParameter("discountType")));
            discount.setDiscountValue(new BigDecimal(req.getParameter("discountValue")));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            discount.setStartDate(LocalDateTime.parse(req.getParameter("startDate"), formatter));
            discount.setEndDate(LocalDateTime.parse(req.getParameter("endDate"), formatter));
            
            String maxUsage = req.getParameter("maxUsage");
            if (maxUsage != null && !maxUsage.isEmpty()) {
                discount.setMaxUsage(Integer.parseInt(maxUsage));
            }
            discount.setIsActive(true);
            
            boolean created = discountDAO.createDiscount(discount);
            
            if (created) {
                req.getSession().setAttribute("successMsg", "Thêm mã giảm giá thành công!");
            } else {
                req.getSession().setAttribute("errorMsg", "Thêm mã giảm giá thất bại!");
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/discounts");
            
        } else if ("update".equals(action)) {
            // Update existing discount
            int discountId = Integer.parseInt(req.getParameter("discountId"));
            Discount discount = discountDAO.getDiscountById(discountId);
            
            if (discount != null) {
                discount.setDescription(req.getParameter("description"));
                discount.setDiscountType(Discount.DiscountType.valueOf(req.getParameter("discountType")));
                discount.setDiscountValue(new BigDecimal(req.getParameter("discountValue")));
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                discount.setStartDate(LocalDateTime.parse(req.getParameter("startDate"), formatter));
                discount.setEndDate(LocalDateTime.parse(req.getParameter("endDate"), formatter));
                
                String maxUsage = req.getParameter("maxUsage");
                if (maxUsage != null && !maxUsage.isEmpty()) {
                    discount.setMaxUsage(Integer.parseInt(maxUsage));
                } else {
                    discount.setMaxUsage(null);
                }
                
                String isActiveStr = req.getParameter("isActive");
                if (isActiveStr != null) {
                    discount.setIsActive(Boolean.parseBoolean(isActiveStr));
                }
                
                boolean updated = discountDAO.updateDiscount(discount);
                
                if (updated) {
                    req.getSession().setAttribute("successMsg", "Cập nhật mã giảm giá thành công!");
                } else {
                    req.getSession().setAttribute("errorMsg", "Cập nhật mã giảm giá thất bại!");
                }
            }
              
            resp.sendRedirect(req.getContextPath() + "/admin/discounts");
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