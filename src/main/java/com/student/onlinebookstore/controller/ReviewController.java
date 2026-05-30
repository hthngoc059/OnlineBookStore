package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.config.ApplicationContextProvider;
import com.student.onlinebookstore.dto.request.ReviewRequest;
import com.student.onlinebookstore.exception.DuplicateResourceException;
import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/reviews")
public class ReviewController extends HttpServlet {

    private ReviewService reviewService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.reviewService = ApplicationContextProvider.getBean(ReviewService.class);
        this.objectMapper  = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // thêm dòng này
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "Vui lòng đăng nhập để đánh giá");
            objectMapper.writeValue(resp.getWriter(), result);
            return;
        }

        try {
            int bookId = Integer.parseInt(req.getParameter("bookId"));
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("comment");

            ReviewRequest request = new ReviewRequest();
            request.setBookId(bookId);
            request.setRating(rating);
            request.setComment(comment);

            // Gọi addReview nhưng KHÔNG dùng ReviewResponse trả về (dễ lỗi null)
            reviewService.addReview(user.getUserId(), request);

            // Build response thủ công từ data đã biết
            result.put("success",  true);
            result.put("username", user.getUsername());
            result.put("rating",   rating);
            result.put("comment",  comment != null ? comment : "");

            double avg   = reviewService.getAverageRating(bookId);
            int    count = reviewService.getRatingCount(bookId);
            result.put("avgRating",   Math.round(avg * 10.0) / 10.0);
            result.put("reviewCount", count);

        } catch (DuplicateResourceException e) {
            result.put("success", false);
            result.put("message", "Bạn đã đánh giá sách này rồi");
        } catch (Exception e) {
            e.printStackTrace(); // xem log chi tiết
            result.put("success", false);
            result.put("message", e.getMessage() != null ? e.getMessage() : "Lỗi server");
        }

        objectMapper.writeValue(resp.getWriter(), result);
    }
}