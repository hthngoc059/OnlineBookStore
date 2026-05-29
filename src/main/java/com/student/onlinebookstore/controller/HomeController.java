package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.model.Book;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final BookDAO bookDAO = new BookDAO();

    @GetMapping({"/", "/home", "/trang-chu"})
    public String home(Model model) {
        List<Book> featuredBooks = bookDAO.getNewArrivals(8);

        List<Book> bestSellers = bookDAO.getBestSellers(8);
        if (bestSellers == null || bestSellers.isEmpty()) {
            bestSellers = bookDAO.getAllBooks(0, 8);
        }

        model.addAttribute("featuredBooks", featuredBooks);
        model.addAttribute("bestSellers", bestSellers);
        return "home";
    }
}