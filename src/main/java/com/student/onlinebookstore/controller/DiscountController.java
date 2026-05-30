package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.config.ApplicationContextProvider;
import com.student.onlinebookstore.exception.InvalidCredentialsException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.Discount;
import com.student.onlinebookstore.service.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/checkout/validate-discount")
public class DiscountController extends HttpServlet {

    private DiscountService discountService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.discountService = ApplicationContextProvider.getBean(DiscountService.class);
        this.objectMapper    = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String code           = req.getParameter("code");
        String totalAmountStr = req.getParameter("totalAmount");

        Map<String, Object> result = new HashMap<>();

        // Validate input
        if (code == null || code.isBlank() || totalAmountStr == null) {
            result.put("success", false);
            result.put("message", "Thiếu thông tin");
            objectMapper.writeValue(resp.getWriter(), result);
            return;
        }

        try {
            BigDecimal totalAmount = new BigDecimal(totalAmountStr);
            
            // Lấy user hiện tại từ session
            com.student.onlinebookstore.model.User currentUser =
                (com.student.onlinebookstore.model.User) req.getSession().getAttribute("currentUser");

            if (currentUser == null) {
                result.put("success", false);
                result.put("message", "Vui lòng đăng nhập để sử dụng mã giảm giá");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            // Validate discount cho user cụ thể
            Discount discount = discountService.validateDiscountForUser(
                code.trim().toUpperCase(), currentUser.getUserId()
            );
            
            BigDecimal discountAmt  = discountService.calculateDiscount(totalAmount, discount);
            BigDecimal finalAmount  = totalAmount.subtract(discountAmt);

            result.put("success",       true);
            result.put("discountId",    discount.getDiscountId());
            result.put("description",   discount.getDescription() != null 
                                            ? discount.getDescription() : code);
            result.put("discountType",  discount.getDiscountType().name());
            result.put("discountValue", discount.getDiscountValue());
            result.put("discountAmount", discountAmt);
            result.put("finalAmount",   finalAmount);

        } catch (InvalidCredentialsException | ResourceNotFoundException e) {
            result.put("success", false);
            result.put("message", "Mã không hợp lệ hoặc đã hết hạn");

        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Số tiền không hợp lệ");
        }

        objectMapper.writeValue(resp.getWriter(), result);
    }
}