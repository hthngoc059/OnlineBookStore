package com.student.onlinebookstore.service;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.CartDAO;
import com.student.onlinebookstore.dao.WishlistDAO;
import com.student.onlinebookstore.dto.response.PaginationResponse;
import com.student.onlinebookstore.dto.response.WishlistItemResponse;
import com.student.onlinebookstore.dto.response.WishlistResponse;
import com.student.onlinebookstore.exception.DuplicateResourceException;
import com.student.onlinebookstore.exception.ResourceNotFoundException;
import com.student.onlinebookstore.model.WishlistItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishlistServiceImpl implements WishlistService {
    
    private static final Logger logger = LoggerFactory.getLogger(WishlistServiceImpl.class);
    
    private WishlistDAO wishlistDAO;
    private CartDAO cartDAO;
    private BookDAO bookDAO;
    
    @Autowired
    public WishlistServiceImpl(WishlistDAO wishlistDAO, CartDAO cartDAO, BookDAO bookDAO) {
        this.wishlistDAO = wishlistDAO;
        this.cartDAO = cartDAO;
        this.bookDAO = bookDAO;
    }
    
    @Override
    public WishlistResponse addToWishlist(Integer userId, Integer bookId) {
        logger.info("Adding to wishlist - userId: {}, bookId: {}", userId, bookId);
        
        // Check if book exists
        if (bookDAO.getBookById(bookId) == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        
        // Check if already in wishlist
        if (wishlistDAO.isInWishlist(userId, bookId)) {
            throw new DuplicateResourceException("Book is already in the wishlist");
        }
        
        // Get or create wishlist
        int wishlistId = wishlistDAO.getOrCreateWishlist(userId);
        
        // Add to wishlist
        boolean added = wishlistDAO.addItem(wishlistId, bookId);
        if (!added) {
            throw new RuntimeException("Failed to add book to wishlist");
        }
        
        logger.info("Item added to wishlist - wishlistId: {}, bookId: {}", wishlistId, bookId);
        
        return getWishlist(userId);
    }
    
    @Override
    public WishlistResponse removeFromWishlist(Integer userId, Integer wishlistItemId) {
        logger.info("Removing from wishlist - userId: {}, wishlistItemId: {}", userId, wishlistItemId);
        
        boolean removed = wishlistDAO.removeItem(wishlistItemId);
        if (!removed) {
            throw new ResourceNotFoundException("Book not found in wishlist");
        }
        
        logger.info("Item removed from wishlist - wishlistItemId: {}", wishlistItemId);
        
        return getWishlist(userId);
    }
    
    @Override
    public WishlistResponse getWishlist(Integer userId) {
        logger.info("Getting wishlist for userId: {}", userId);
        
        int wishlistId = wishlistDAO.getOrCreateWishlist(userId);
        List<WishlistItem> items = wishlistDAO.getWishlistItems(wishlistId);
        
        List<WishlistItemResponse> itemResponses = items.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        WishlistResponse response = new WishlistResponse();
        response.setWishlistId(wishlistId);
        response.setItems(itemResponses);
        
        logger.info("Wishlist retrieved for userId: {}, total items: {}", userId, itemResponses.size());
        
        return response;
    }
    
    @Override
    public PaginationResponse getWishlistItems(Integer userId, int page, int size) {
        logger.info("Getting wishlist items - userId: {}, page: {}, size: {}", userId, page, size);
        
        WishlistResponse wishlist = getWishlist(userId);
        List<WishlistItemResponse> items = wishlist.getItems();
        
        // Paginate
        int start = page * size;
        int end = Math.min(start + size, items.size());
        List<WishlistItemResponse> paginatedItems = items.subList(start, end);
        
        return new PaginationResponse(paginatedItems, page, size, items.size());
    }
    
    @Override
    public boolean isInWishlist(Integer userId, Integer bookId) {
        return wishlistDAO.isInWishlist(userId, bookId);
    }
    
    @Override
    public int getWishlistCount(Integer userId) {
        WishlistResponse wishlist = getWishlist(userId);
        return wishlist.getItems().size();
    }
    
    @Override
    public WishlistResponse moveToCart(Integer userId, Integer wishlistItemId) {
        logger.info("Moving from wishlist to cart - userId: {}, wishlistItemId: {}", userId, wishlistItemId);
        
        // Get wishlist items to find book ID
        int wishlistId = wishlistDAO.getOrCreateWishlist(userId);
        List<WishlistItem> items = wishlistDAO.getWishlistItems(wishlistId);
        
        WishlistItem targetItem = items.stream()
            .filter(item -> item.getWishlistItemId().equals(wishlistItemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Book not found in wishlist"));
        
        // Add to cart
        int cartId = cartDAO.getOrCreateCart(userId);
        cartDAO.addItem(cartId, targetItem.getBook().getBookId(), 1);
        
        // Remove from wishlist
        wishlistDAO.removeItem(wishlistItemId);
        
        logger.info("Item moved from wishlist to cart - bookId: {}", targetItem.getBook().getBookId());
        
        return getWishlist(userId);
    }
    
    @Override
    public WishlistResponse addAllToCart(Integer userId) {
        logger.info("Adding all wishlist items to cart - userId: {}", userId);
        
        int wishlistId = wishlistDAO.getOrCreateWishlist(userId);
        List<WishlistItem> items = wishlistDAO.getWishlistItems(wishlistId);
        
        int cartId = cartDAO.getOrCreateCart(userId);
        
        for (WishlistItem item : items) {
            cartDAO.addItem(cartId, item.getBook().getBookId(), 1);
            wishlistDAO.removeItem(item.getWishlistItemId());
        }
        
        logger.info("All wishlist items moved to cart - userId: {}, count: {}", userId, items.size());
        
        return getWishlist(userId);
    }
    
    private WishlistItemResponse convertToResponse(WishlistItem item) {
        WishlistItemResponse response = new WishlistItemResponse();
        response.setWishlistItemId(item.getWishlistItemId());
        response.setBookId(item.getBook().getBookId());
        response.setBookTitle(item.getBook().getTitle());
        response.setBookAuthor(item.getBook().getAuthor());
        response.setCoverImageUrl(item.getBook().getCoverImageUrl());
        response.setPrice(item.getBook().getPrice());
        response.setIsAvailable(item.getBook().getIsAvailable());
        response.setStockQuantity(item.getBook().getStockQuantity());
        response.setAddedAt(item.getAddedAt());
        
        return response;
    }
    @Override
    public Integer getWishlistItemId(Integer userId, Integer bookId) {
        int wishlistId = wishlistDAO.getOrCreateWishlist(userId);
        return wishlistDAO.getWishlistItemId(wishlistId, bookId);
    }
}