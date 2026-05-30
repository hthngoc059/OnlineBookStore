package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.model.Genre;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final BookDAO bookDAO = new BookDAO();

    @GetMapping({"/", "/home", "/trang-chu"})
    public String home(Model model) {

        // Sách nổi bật & giảm giá (giữ nguyên)
        List<Book> featuredBooks = bookDAO.getNewArrivals(8);
        List<Book> bestSellers   = bookDAO.getBestSellers(8);
        if (bestSellers == null || bestSellers.isEmpty()) {
            bestSellers = bookDAO.getAllBooks(0, 8);
        }
        model.addAttribute("featuredBooks", featuredBooks);
        model.addAttribute("bestSellers",   bestSellers);

        // Sách theo thể loại
        List<Genre> genres = bookDAO.getAllGenresWithBooks();
        Map<String, List<Book>> booksByGenre = new LinkedHashMap<>();
        for (Genre genre : genres) {
            List<Book> books = bookDAO.getBooksByGenreId(genre.getGenreId(), 8);
            if (books != null && !books.isEmpty()) {
                booksByGenre.put(genre.getGenreName(), books);
            }
        }
        model.addAttribute("booksByGenre", booksByGenre);
    
        return "home";
    }
}