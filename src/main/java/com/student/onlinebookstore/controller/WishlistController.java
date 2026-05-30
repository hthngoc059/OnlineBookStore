package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dto.response.WishlistResponse;
import com.student.onlinebookstore.exception.DuplicateResourceException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.service.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {  // ← KHÔNG extends HttpServlet nữa

    @Autowired
    private WishlistService wishlistService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WishlistController() {
        objectMapper.findAndRegisterModules();
    }

    // GET: hiển thị trang wishlist
    @GetMapping
    public String getWishlistPage(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        User user = getLoggedInUser(req);
        
        if (user == null) {
            return "redirect:/";
        }
        
        WishlistResponse wishlist = wishlistService.getWishlist(user.getUserId());
        req.setAttribute("wishlist", wishlist);
        req.setAttribute("currentUser", user);
        return "wishlist";
    }
    
    // GET: kiểm tra sách có trong wishlist không (AJAX)
    @GetMapping("/check")
    @ResponseBody
    public Map<String, Boolean> checkInWishlist(
            @RequestParam int bookId,
            HttpServletRequest req) {
        
        User user = getLoggedInUser(req);
        if (user == null) {
            return Map.of("inWishlist", false);
        }
        
        boolean inWishlist = wishlistService.isInWishlist(user.getUserId(), bookId);
        return Map.of("inWishlist", inWishlist);
    }

    // POST: thêm vào wishlist
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToWishlist(
            @RequestParam int bookId,
            HttpServletRequest req) {
        
        User user = getLoggedInUser(req);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Vui lòng đăng nhập"));
        }
        
        try {
            wishlistService.addToWishlist(user.getUserId(), bookId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã thêm vào danh sách yêu thích");
            result.put("wishlistCount", wishlistService.getWishlistCount(user.getUserId()));
            return ResponseEntity.ok(result);
            
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("success", false, "message", "Sách đã có trong danh sách yêu thích"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Không tìm thấy sách"));
        }
    }
    
    // POST: xóa khỏi wishlist
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromWishlist(
            @RequestParam int itemId,
            HttpServletRequest req) {
        
        User user = getLoggedInUser(req);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Vui lòng đăng nhập"));
        }
        
        try {
            wishlistService.removeFromWishlist(user.getUserId(), itemId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã xóa khỏi danh sách yêu thích");
            result.put("wishlistCount", wishlistService.getWishlistCount(user.getUserId()));
            return ResponseEntity.ok(result);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Không tìm thấy sản phẩm trong wishlist"));
        }
    }
    
    // POST: chuyển vào giỏ hàng
    @PostMapping("/moveToCart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> moveToCart(
            @RequestParam int itemId,
            HttpServletRequest req) {
        
        User user = getLoggedInUser(req);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Vui lòng đăng nhập"));
        }
        
        try {
            wishlistService.moveToCart(user.getUserId(), itemId);
            
            // Cập nhật cartCount trong session
            HttpSession session = req.getSession();
            Integer cartCount = (Integer) session.getAttribute("cartCount");
            session.setAttribute("cartCount", (cartCount == null ? 1 : cartCount + 1));
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã chuyển vào giỏ hàng"));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    // POST: thêm tất cả vào giỏ hàng
    @PostMapping("/addAllToCart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addAllToCart(HttpServletRequest req) {
        
        User user = getLoggedInUser(req);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Vui lòng đăng nhập"));
        }
        
        try {
            wishlistService.addAllToCart(user.getUserId());
            req.getSession().removeAttribute("cartCount");
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã thêm tất cả vào giỏ hàng"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Lỗi khi thêm vào giỏ hàng"));
        }
    }

    private User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute("currentUser");
    }
    // POST: xử lý wishlist với action parameter (cho book-detail.jsp)
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleWishlistAction(
            @RequestParam String action,
            @RequestParam(required = false) Integer bookId,
            @RequestParam(required = false) Integer itemId,
            HttpServletRequest req) {

        User user = getLoggedInUser(req);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Vui lòng đăng nhập"));
        }

        switch (action) {
            case "add":
                try {
                    wishlistService.addToWishlist(user.getUserId(), bookId);
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Đã thêm vào danh sách yêu thích",
                        "wishlistCount", wishlistService.getWishlistCount(user.getUserId())
                    ));
                } catch (DuplicateResourceException e) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "message", "Sách đã có trong danh sách yêu thích"));
                } catch (ResourceNotFoundException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Không tìm thấy sách"));
                }

            case "remove":
                try {
                    wishlistService.removeFromWishlist(user.getUserId(), itemId);
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Đã xóa khỏi danh sách yêu thích",
                        "wishlistCount", wishlistService.getWishlistCount(user.getUserId())
                    ));
                } catch (ResourceNotFoundException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Không tìm thấy sản phẩm"));
                }
            case "moveToCart":
                try {

                    wishlistService.moveToCart(user.getUserId(), itemId);

                    // update cart count
                    HttpSession session = req.getSession();
                    Integer cartCount = (Integer) session.getAttribute("cartCount");

                    session.setAttribute(
                        "cartCount",
                        cartCount == null ? 1 : cartCount + 1
                    );

                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Đã thêm vào giỏ hàng"
                    ));

                } catch (ResourceNotFoundException e) {

                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                        ));
                }

            default:
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Action không hợp lệ"));
        }
    }
}