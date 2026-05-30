package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.BookDAO;
import com.student.onlinebookstore.dao.GenreDAO;
import com.student.onlinebookstore.model.Book;
import com.student.onlinebookstore.model.Genre;
import com.student.onlinebookstore.model.User;
import com.student.onlinebookstore.util.SlugGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/books")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 10
)
public class AdminBookController extends HttpServlet {
    
    private BookDAO bookDAO;
    private GenreDAO genreDAO;
    
    // Thư mục upload ảnh
    private static final String UPLOAD_DIR = "uploads/books";
    
    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        genreDAO = new GenreDAO();
        
        // Tạo thư mục upload nếu chưa tồn tại (không cần ServletContext ở đây)
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("add".equals(action)) {
            // Hiển thị form thêm sách
            List<Genre> genres = genreDAO.getAllGenres();
            req.setAttribute("genres", genres);
            req.getRequestDispatcher("/WEB-INF/views/admin/add-book.jsp").forward(req, resp);
            
        } else if ("edit".equals(action)) {
            // Hiển thị form sửa sách
            int bookId = Integer.parseInt(req.getParameter("id"));
            Book book = bookDAO.getBookById(bookId);
            List<Genre> allGenres = genreDAO.getAllGenres();
            List<Integer> bookGenreIds = genreDAO.getGenreIdsByBookId(bookId);
            
            req.setAttribute("book", book);
            req.setAttribute("genres", allGenres);
            req.setAttribute("bookGenreIds", bookGenreIds);
            req.getRequestDispatcher("/WEB-INF/views/admin/edit-book.jsp").forward(req, resp);
            
        } else if ("delete".equals(action)) {
            // Xóa sách
            int bookId = Integer.parseInt(req.getParameter("id"));
            boolean deleted = bookDAO.deleteBook(bookId);
            if (deleted) {
                req.getSession().setAttribute("successMsg", "Xóa sách thành công!");
            } else {
                req.getSession().setAttribute("errorMsg", "Xóa sách thất bại!");
            }
            resp.sendRedirect(req.getContextPath() + "/admin/books");
            
        } else {
            // Hiển thị danh sách sách
            int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
            int size = 10;
            String keyword = req.getParameter("keyword");
            String genreId = req.getParameter("genre");
            String stockStatus = req.getParameter("stockStatus");
            
            List<Book> books;
            int totalBooks;
            
            if (keyword != null && !keyword.isEmpty()) {
                books = bookDAO.searchBooks(keyword, page, size);
                totalBooks = bookDAO.getSearchCount(keyword);
            } else if (genreId != null && !genreId.isEmpty()) {
                Genre genre = genreDAO.getGenreById(Integer.parseInt(genreId));
                books = bookDAO.filterByGenre(genre.getGenreName(), page, size);
                totalBooks = books.size();
            } else {
                books = bookDAO.getAllBooks(page, size);
                totalBooks = bookDAO.getTotalBooks();
            }
            
            // Lọc theo stock status
            if (stockStatus != null && !stockStatus.isEmpty()) {
                if ("lowStock".equals(stockStatus)) {
                    books = books.stream()
                        .filter(b -> b.getStockQuantity() > 0 && b.getStockQuantity() <= 5)
                        .collect(java.util.stream.Collectors.toList());
                } else if ("outStock".equals(stockStatus)) {
                    books = books.stream()
                        .filter(b -> b.getStockQuantity() <= 0)
                        .collect(java.util.stream.Collectors.toList());
                } else if ("inStock".equals(stockStatus)) {
                    books = books.stream()
                        .filter(b -> b.getStockQuantity() > 0)
                        .collect(java.util.stream.Collectors.toList());
                }
                totalBooks = books.size();
            }
            
            // Lấy genres cho từng book
            for (Book book : books) {
                book.setGenres(bookDAO.getGenresByBookId(book.getBookId()));
            }
            
            List<Genre> allGenres = genreDAO.getAllGenres();
            
            req.setAttribute("books", books);
            req.setAttribute("totalBooks", totalBooks);
            req.setAttribute("genres", allGenres);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", (int) Math.ceil((double) totalBooks / size));
            
            req.getRequestDispatcher("/WEB-INF/views/admin/books.jsp").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (!isAdmin(req, resp)) return;
        
        String action = req.getParameter("action");
        
        if ("create".equals(action)) {
            // Thêm sách mới
            Book book = new Book();
            book.setTitle(req.getParameter("title"));
            book.setAuthor(req.getParameter("author"));
            book.setDescription(req.getParameter("description"));
            book.setIsbn(req.getParameter("isbn"));
            book.setPublisher(req.getParameter("publisher"));
            
            String yearStr = req.getParameter("publishedYear");
            if (yearStr != null && !yearStr.isEmpty()) {
                book.setPublishedYear(Year.of(Integer.parseInt(yearStr)));
            }
            
            book.setLanguage(req.getParameter("language"));
            book.setPrice(new BigDecimal(req.getParameter("price")));
            book.setStockQuantity(Integer.parseInt(req.getParameter("stockQuantity")));
            book.setSlug(SlugGenerator.generateSlug(req.getParameter("title")));
            book.setFormat(Book.Format.valueOf(req.getParameter("format")));
            
            // Upload ảnh bìa
            Part filePart = req.getPart("coverImage");
            if (filePart != null && filePart.getSize() > 0) {
                String imageUrl = uploadFile(req, filePart);  // ← SỬA: truyền req vào
                book.setCoverImageUrl(imageUrl);
            }
            
            boolean created = bookDAO.createBook(book);
            
            if (created) {
                // Thêm thể loại cho sách
                String[] genreIds = req.getParameterValues("genreIds");
                if (genreIds != null && book.getBookId() != null) {
                    for (String gid : genreIds) {
                        genreDAO.addBookToGenre(book.getBookId(), Integer.parseInt(gid));
                    }
                }
                req.getSession().setAttribute("successMsg", "Thêm sách thành công!");
            } else {
                req.getSession().setAttribute("errorMsg", "Thêm sách thất bại!");
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/books");
            
        } else if ("update".equals(action)) {
            // Cập nhật sách
            int bookId = Integer.parseInt(req.getParameter("bookId"));
            Book book = bookDAO.getBookById(bookId);
            
            if (book != null) {
                book.setTitle(req.getParameter("title"));
                book.setAuthor(req.getParameter("author"));
                book.setDescription(req.getParameter("description"));
                book.setPublisher(req.getParameter("publisher"));
                book.setLanguage(req.getParameter("language"));
                book.setPrice(new BigDecimal(req.getParameter("price")));
                book.setStockQuantity(Integer.parseInt(req.getParameter("stockQuantity")));
                book.setFormat(Book.Format.valueOf(req.getParameter("format")));
                
                // Upload ảnh mới nếu có
                Part filePart = req.getPart("coverImage");
                if (filePart != null && filePart.getSize() > 0) {
                    String imageUrl = uploadFile(req, filePart);  
                    book.setCoverImageUrl(imageUrl);
                }
                
                boolean updated = bookDAO.updateBook(book);
                
                if (updated) {
                    // Cập nhật thể loại (xóa cũ, thêm mới)
                    genreDAO.removeAllGenresFromBook(bookId);
                    String[] genreIds = req.getParameterValues("genreIds");
                    if (genreIds != null) {
                        for (String gid : genreIds) {
                            genreDAO.addBookToGenre(bookId, Integer.parseInt(gid));
                        }
                    }
                    req.getSession().setAttribute("successMsg", "Cập nhật sách thành công!");
                } else {
                    req.getSession().setAttribute("errorMsg", "Cập nhật sách thất bại!");
                }
            }
            
            String redirectUrl = req.getContextPath() + "/admin/books";
            System.out.println("Redirecting to: " + redirectUrl);
            resp.sendRedirect(redirectUrl);
        }
    }
    
    /**
     * Upload file ảnh và trả về đường dẫn
     */
    private String uploadFile(HttpServletRequest req, Part filePart) throws IOException {  // ← THÊM req vào tham số
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        
        // Lấy tên file gốc và extension
        String fileName = getFileName(filePart);
        String extension = fileName.substring(fileName.lastIndexOf("."));
        
        // Tạo tên file duy nhất
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        
        // Đường dẫn tuyệt đối để lưu file
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // Lưu file
        String filePath = uploadPath + File.separator + uniqueFileName;
        filePart.write(filePath);
        
        // Trả về đường dẫn relative để hiển thị trên web
        return req.getContextPath() + "/" + UPLOAD_DIR + "/" + uniqueFileName;
    }
    
    /**
     * Lấy tên file từ Part
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf("=") + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
    
    private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return false;
        }
        User currentUser = (User) session.getAttribute("currentUser");
        if (!"admin".equals(currentUser.getRole().name())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return false;
        }
        return true;
    }
}