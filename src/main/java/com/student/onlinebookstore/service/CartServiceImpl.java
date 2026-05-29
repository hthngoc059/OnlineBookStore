package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.CartDAO;
import com.student.onlinebookstore.dto.request.AddToCartRequest;
import com.student.onlinebookstore.dto.request.UpdateCartItemRequest;
import com.student.onlinebookstore.dto.response.CartItemResponse;
import com.student.onlinebookstore.dto.response.CartResponse;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.exception.OutOfStockException;
import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.model.Cart;
import com.student.onlinebookstore.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartServiceImpl implements CartService {
    
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    
    private CartDAO cartDAO;
    private BookDAO bookDAO;
    
    public CartServiceImpl(CartDAO cartDAO, BookDAO bookDAO) {
        this.cartDAO = cartDAO;
        this.bookDAO = bookDAO;
    }
    
    @Override
    public CartResponse addToCart(Integer userId, AddToCartRequest request) {
        logger.info("Adding to cart - userId: {}, bookId: {}, quantity: {}", 
                   userId, request.getBookId(), request.getQuantity());
        
        // Check if book exists and has stock
        Book book = bookDAO.getBookById(request.getBookId());
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        
        if (book.getStockQuantity() < request.getQuantity()) {
            throw new OutOfStockException("Book \"" + book.getTitle() + "\" is out of stock");
        }
        
        // Get or create cart
        int cartId = cartDAO.getOrCreateCart(userId);
        
        // Add item to cart
        boolean added = cartDAO.addItem(cartId, request.getBookId(), request.getQuantity());
        if (!added) {
            throw new RuntimeException("Failed to add book to cart");
        }
        
        logger.info("Item added to cart successfully - cartId: {}, bookId: {}", cartId, request.getBookId());
        
        return getCart(userId);
    }
    
    @Override
    public CartResponse getCart(Integer userId) {
        logger.info("Getting cart for userId: {}", userId);
        
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) {
            return new CartResponse(null, new ArrayList<>(), BigDecimal.ZERO);
        }
        
        List<CartItem> cartItems = cartDAO.getCartItems(cart.getCartId());
        
        BigDecimal subtotal = BigDecimal.ZERO;
        List<CartItemResponse> itemResponses = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            Book book = item.getBook();
            BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            
            CartItemResponse itemResponse = new CartItemResponse();
            itemResponse.setCartItemId(item.getCartItemId());
            itemResponse.setBookId(book.getBookId());
            itemResponse.setBookTitle(book.getTitle());
            itemResponse.setBookAuthor(book.getAuthor());
            itemResponse.setCoverImageUrl(book.getCoverImageUrl());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPrice(book.getPrice());
            itemResponse.setSubtotal(itemTotal);
            
            itemResponses.add(itemResponse);
        }
        
        CartResponse response = new CartResponse();
        response.setCartId(cart.getCartId());
        response.setItems(itemResponses);
        response.setSubtotal(subtotal);
        response.setTotal(subtotal);
        
        logger.info("Cart retrieved for userId: {}, total items: {}, subtotal: {}", 
                   userId, itemResponses.size(), subtotal);
        
        return response;
    }
    
    @Override
    public CartResponse updateCartItem(Integer userId, UpdateCartItemRequest request) {
        logger.info("Updating cart item - userId: {}, cartItemId: {}, quantity: {}", 
                   userId, request.getCartItemId(), request.getQuantity());
        
        // Get cart item
        CartItem cartItem = cartDAO.getCartItemById(request.getCartItemId());
        if (cartItem == null) {
            throw new ResourceNotFoundException("Book not found in cart");
        }
        
        // Check stock
        Book book = cartItem.getBook();
        if (book.getStockQuantity() < request.getQuantity()) {
            throw new OutOfStockException("Book \"" + book.getTitle() + "\" is out of stock");
        }
        
        // Update quantity
        boolean updated = cartDAO.updateItemQuantity(request.getCartItemId(), request.getQuantity());
        if (!updated) {
            throw new RuntimeException("Failed to update item quantity");
        }
        
        logger.info("Cart item updated successfully - cartItemId: {}, new quantity: {}", 
                   request.getCartItemId(), request.getQuantity());
        
        return getCart(userId);
    }
    
    @Override
    public CartResponse removeCartItem(Integer userId, Integer cartItemId) {
        logger.info("Removing cart item - userId: {}, cartItemId: {}", userId, cartItemId);
        
        boolean removed = cartDAO.removeItem(cartItemId);
        if (!removed) {
            throw new ResourceNotFoundException("Book not found in cart");
        }
        
        logger.info("Cart item removed successfully - cartItemId: {}", cartItemId);
        
        return getCart(userId);
    }
    
    @Override
    public void clearCart(Integer userId) {
        logger.info("Clearing cart for userId: {}", userId);
        
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart != null) {
            cartDAO.clearCart(cart.getCartId());
            logger.info("Cart cleared for userId: {}", userId);
        }
    }
    
    @Override
    public int getCartItemCount(Integer userId) {
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) {
            return 0;
        }
        
        List<CartItem> items = cartDAO.getCartItems(cart.getCartId());
        return items.size();
    }
}