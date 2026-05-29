package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.CartDAO;
import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.model.Cart;
import com.student.onlinebookstore.model.CartItem;
import com.student.onlinebookstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/cart")
public class CartController extends HttpServlet {

    private CartDAO cartDAO;
    private BookDAO bookDAO;

    @Override
    public void init() throws ServletException {
        cartDAO = new CartDAO();
        bookDAO = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "view";

        switch (action) {
            case "add":    addToCart(request, response); break;
            case "remove": removeFromCart(request, response); break;
            case "update": updateCartItem(request, response); break;
            case "clear":  clearCart(request, response); break;
            case "view":
            default:       viewCart(request, response); break;
        }
    }

    /**
     * Xem giỏ hàng
     */
    private void viewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            request.setAttribute("error", "Vui lòng đăng nhập để xem giỏ hàng!");
            request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
            return;
        }

        try {
            Cart cart = cartDAO.getCartByUserId(user.getUserId());
            
            if (cart != null) {
                List<CartItem> items = cartDAO.getCartItems(cart.getCartId());
                
                // Lấy thêm stock_quantity cho mỗi item
                if (items != null) {
                    for (CartItem item : items) {
                        Book book = bookDAO.getBookById(item.getBook().getBookId());
                        if (book != null) {
                            item.getBook().setStockQuantity(book.getStockQuantity());
                        }
                    }
                }
                
                request.setAttribute("cartItems", items);
                
                // Tính tổng tiền
                double total = 0;
                if (items != null) {
                    for (CartItem item : items) {
                        total += item.getBook().getPrice().doubleValue() * item.getQuantity();
                    }
                }
                request.setAttribute("cartTotal", total);
            } else {
                request.setAttribute("cartItems", null);
                request.setAttribute("cartTotal", 0.0);
            }

            request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tải giỏ hàng!");
            request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
        }
    }

    /**
     * Thêm sách vào giỏ hàng
     */
    private void addToCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        String bookIdStr = request.getParameter("id");
        String qtyStr = request.getParameter("qty");

        if (bookIdStr == null || bookIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr);
            int qty = (qtyStr != null && !qtyStr.isEmpty()) ? Integer.parseInt(qtyStr) : 1;

            // Lấy hoặc tạo giỏ hàng
            int cartId = cartDAO.getOrCreateCart(user.getUserId());

            // Thêm sách vào giỏ
            cartDAO.addItem(cartId, bookId, qty);

            // Cập nhật cartCount trong session
            updateCartCount(session, user.getUserId());

            // Redirect về trang chi tiết sách
            response.sendRedirect(request.getContextPath() + "/books?action=detail&id=" + bookId);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/books");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        String itemIdStr = request.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            int cartItemId = Integer.parseInt(itemIdStr);
            cartDAO.removeItem(cartItemId);
            updateCartCount(session, user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    /**
     * Cập nhật số lượng
     */
    private void updateCartItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        String itemIdStr = request.getParameter("itemId");
        String qtyStr = request.getParameter("qty");

        if (itemIdStr == null || qtyStr == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            int cartItemId = Integer.parseInt(itemIdStr);
            int qty = Integer.parseInt(qtyStr);

            if (qty <= 0) {
                cartDAO.removeItem(cartItemId);
            } else {
                cartDAO.updateItemQuantity(cartItemId, qty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        try {
            Cart cart = cartDAO.getCartByUserId(user.getUserId());
            if (cart != null) {
                cartDAO.clearCart(cart.getCartId());
                session.setAttribute("cartCount", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    /**
     * Cập nhật cartCount trong session
     */
    private void updateCartCount(HttpSession session, int userId) {
        try {
            Cart cart = cartDAO.getCartByUserId(userId);
            if (cart != null) {
                List<CartItem> items = cartDAO.getCartItems(cart.getCartId());
                session.setAttribute("cartCount", (items != null) ? items.size() : 0);
            } else {
                session.setAttribute("cartCount", 0);
            }
        } catch (Exception e) {
            session.setAttribute("cartCount", 0);
        }
    }
}