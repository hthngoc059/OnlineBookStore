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

@WebServlet("/books")
public class BookController extends HttpServlet {
    
    private BookDAO bookDAO;
    private ReviewDAO reviewDAO;
    
    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        reviewDAO = new ReviewDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        switch (action) {
            case "detail":   showBookDetail(request, response); break;
            case "search":   searchBooks(request, response); break;
            case "genre":    filterByGenre(request, response); break;
            case "list":
            default:         listAllBooks(request, response); break;
        }
    }
    
    private void showBookDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookIdStr = request.getParameter("id");
        if (bookIdStr == null || bookIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr);
            Book book = bookDAO.getBookById(bookId);

            if (book == null) {
                response.sendRedirect(request.getContextPath() + "/books");
                return;
            }

            // Reviews
            List<Review> reviews = reviewDAO.getReviewsByBookId(bookId, 0, 10);
            book.setReviews(new java.util.HashSet<>(reviews));

            double avgRating = reviewDAO.getAverageRating(bookId);
            int reviewCount = reviewDAO.getRatingCount(bookId);

            // Rating counts - đếm từ reviews
            java.util.Map<Integer, Integer> ratingCounts = new java.util.HashMap<>();
            for (int i = 1; i <= 5; i++) ratingCounts.put(i, 0);
            for (Review r : reviews) {
                int star = r.getRating();
                if (star >= 1 && star <= 5) {
                    ratingCounts.put(star, ratingCounts.get(star) + 1);
                }
            }

            // Related books
            List<Book> relatedBooks = new ArrayList<>();
            if (book.getGenres() != null && !book.getGenres().isEmpty()) {
                String genreName = book.getGenres().iterator().next().getGenreName();
                relatedBooks = bookDAO.filterByGenre(genreName, 0, 5);
                if (relatedBooks != null) {
                    relatedBooks.removeIf(b -> b.getBookId().equals(bookId));
                }
            }

            request.setAttribute("book", book);
            request.setAttribute("avgRating", avgRating);
            request.setAttribute("reviewCount", reviewCount);
            request.setAttribute("ratingCounts", ratingCounts);
            request.setAttribute("relatedBooks", relatedBooks);
            request.setAttribute("inWishlist", false);

            request.getRequestDispatcher("/WEB-INF/views/book-detail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/books");
        }
    }
    
    private void searchBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        try {
            List<Book> books = (keyword != null && !keyword.trim().isEmpty())
                ? bookDAO.searchBooks(keyword.trim(), 0, 12)
                : bookDAO.getAllBooks(0, 12);
            request.setAttribute("books", books);
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/books");
        }
    }
    
    private void filterByGenre(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String genreName = request.getParameter("name");
        try {
            List<Book> books = bookDAO.filterByGenre(genreName, 0, 12);
            request.setAttribute("books", books);
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/books");
        }
    }
    
    private void listAllBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            request.setAttribute("featuredBooks", bookDAO.getNewArrivals(8));
            request.setAttribute("bestSellers", bookDAO.getBestSellers(8));
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("featuredBooks", new ArrayList<>());
            request.setAttribute("bestSellers", new ArrayList<>());
        }
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}