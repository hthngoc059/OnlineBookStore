package com.student.onlinebookstore.controller;

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

@WebServlet("/profile/addresses")
public class AddressController extends HttpServlet {
    
    private AddressDAO addressDAO;
    
    @Override
    public void init() throws ServletException {
        addressDAO = new AddressDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }
        
        request.setAttribute("addresses", addressDAO.getAddressesByUserId(user.getUserId()));
        request.setAttribute("currentUser", user);
        request.getRequestDispatcher("/WEB-INF/views/address.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            Address address = new Address();
            address.setUser(user);
            address.setFullName(request.getParameter("fullName"));
            address.setPhone(request.getParameter("phone"));
            address.setAddressLine(request.getParameter("addressLine"));
            address.setWard(request.getParameter("ward"));
            address.setDistrict(request.getParameter("district"));
            address.setCity(request.getParameter("city"));
            address.setIsDefault("true".equals(request.getParameter("isDefault")));
            
            addressDAO.createAddress(address);
        }
        
        response.sendRedirect(request.getContextPath() + "/checkout");
    }
}