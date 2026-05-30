package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.ReviewDAO;
import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.model.Review;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all /books requests.
 *
 * action=list   (default) → home.jsp   — featured + best-sellers for homepage
 * action=search            → search.jsp — keyword search results
 * action=genre             → search.jsp — genre filter results
 * action=detail            → book-detail.jsp
 */
@WebServlet("/books")
public class BookController extends HttpServlet {

    private BookDAO bookDAO;
    private ReviewDAO reviewDAO;

    @Override
    public void init() throws ServletException {
        bookDAO   = new BookDAO();
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "detail": showBookDetail(request, response); break;
            case "search": searchBooks(request, response);    break;
            case "genre":  filterByGenre(request, response);  break;
            case "list":
            default:       listAllBooks(request, response);   break;
        }
    }

    // ─── Book detail ─────────────────────────────────────────────────────────
    private void showBookDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookIdStr = request.getParameter("id");
        if (bookIdStr == null || bookIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr);
            Book book  = bookDAO.getBookById(bookId);

            if (book == null) {
                response.sendRedirect(request.getContextPath() + "/books");
                return;
            }

            // Reviews
            List<Review> reviews = reviewDAO.getReviewsByBookId(bookId, 0, 10);
            book.setReviews(new java.util.HashSet<>(reviews));

            double avgRating  = reviewDAO.getAverageRating(bookId);
            int    reviewCount = reviewDAO.getRatingCount(bookId);

            // Rating breakdown
            java.util.Map<String, Integer> ratingCounts = new java.util.LinkedHashMap<>();
            for (int i = 1; i <= 5; i++) {
                ratingCounts.put(String.valueOf(i), reviewDAO.getRatingCountByStar(bookId, i));
            }
            request.setAttribute("ratingCounts", ratingCounts);

            // Related books (same genre, exclude current)
            List<Book> relatedBooks = new ArrayList<>();
            if (book.getGenres() != null && !book.getGenres().isEmpty()) {
                String genreName = book.getGenres().iterator().next().getGenreName();
                relatedBooks = bookDAO.filterByGenre(genreName, 0, 5);
                if (relatedBooks != null) {
                    relatedBooks.removeIf(b -> b.getBookId().equals(bookId));
                }
            }

            request.setAttribute("book",         book);
            request.setAttribute("avgRating",    avgRating);
            request.setAttribute("reviewCount",  reviewCount);
            request.setAttribute("ratingCounts", ratingCounts);
            request.setAttribute("relatedBooks", relatedBooks);
            request.setAttribute("inWishlist",   false);

            request.getRequestDispatcher("/WEB-INF/views/book-detail.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/books");
        }
    }

    // ─── Keyword search ───────────────────────────────────────────────────────
    private void searchBooks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        if (keyword != null) keyword = keyword.trim();

        try {
            List<Book> books = (keyword != null && !keyword.isEmpty())
                    ? bookDAO.searchBooks(keyword, 0, 24)
                    : bookDAO.getAllBooks(0, 24);

            // Expose to JSP
            request.setAttribute("books",   books);
            request.setAttribute("keyword", keyword);  // ← dùng trong search.jsp

            request.getRequestDispatcher("/WEB-INF/views/search.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/books");
        }
    }

    // ─── Genre filter ─────────────────────────────────────────────────────────
    private void filterByGenre(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String genreName = request.getParameter("name");

        try {
            List<Book> books = bookDAO.filterByGenre(genreName, 0, 24);

            request.setAttribute("books",         books);
            request.setAttribute("selectedGenre", genreName);  // dùng trong search.jsp header

            request.getRequestDispatcher("/WEB-INF/views/search.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/books");
        }
    }

    // ─── List all books (homepage / navbar "Tất cả sách") ────────────────────
    /**
     * /books  hoặc  /books?action=list
     *
     * - Nếu có ?keyword  → forward sang search.jsp với kết quả tìm kiếm
     *   (trường hợp navbar search form submit mà action bị null)
     * - Không có keyword → forward sang search.jsp hiển thị toàn bộ sách
     *   (đây là trang "Tất cả sách")
     *
     * featured / bestSellers vẫn được set để home.jsp có thể reuse nếu cần.
     */
    private void listAllBooks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Nếu người dùng gõ keyword vào form mà action không được gửi đúng
        String keyword = request.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Redirect sang action=search để đồng nhất URL
            response.sendRedirect(request.getContextPath()
                    + "/books?action=search&keyword=" + java.net.URLEncoder.encode(keyword.trim(), "UTF-8"));
            return;
        }

        try {
            // Featured + best-sellers cho home.jsp (nếu cần)
            request.setAttribute("featuredBooks", bookDAO.getNewArrivals(8));
            request.setAttribute("bestSellers",   bookDAO.getBestSellers(8));

            // Toàn bộ sách cho trang "Tất cả sách"
            List<Book> books = bookDAO.getAllBooks(0, 24);
            request.setAttribute("books",   books);
            request.setAttribute("keyword", null);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("featuredBooks", new ArrayList<>());
            request.setAttribute("bestSellers",   new ArrayList<>());
            request.setAttribute("books",         new ArrayList<>());
        }

        // → search.jsp đóng vai "trang tất cả sách"
        request.getRequestDispatcher("/WEB-INF/views/search.jsp")
               .forward(request, response);
}