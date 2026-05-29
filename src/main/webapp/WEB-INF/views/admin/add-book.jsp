<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thêm sách mới - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
    <div class="admin-container">
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="books"/>
        </jsp:include>

        <main class="main-content">
            <header class="main-header">
                <div class="header-title">
                    <h1>Thêm sách mới</h1>
                    <p>Nhập thông tin sách để thêm vào hệ thống</p>
                </div>
            </header>

            <div class="form-container">
                <form action="${pageContext.request.contextPath}/admin/books" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Tên sách <span class="required">*</span></label>
                            <input type="text" name="title" required>
                        </div>
                        <div class="form-group">
                            <label>Tác giả <span class="required">*</span></label>
                            <input type="text" name="author" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>ISBN</label>
                            <input type="text" name="isbn" placeholder="978-...">
                        </div>
                        <div class="form-group">
                            <label>Nhà xuất bản</label>
                            <input type="text" name="publisher">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Năm xuất bản</label>
                            <input type="number" name="publishedYear" min="1000" max="2026">
                        </div>
                        <div class="form-group">
                            <label>Ngôn ngữ</label>
                            <select name="language">
                                <option value="Tiếng Việt">Tiếng Việt</option>
                                <option value="Tiếng Anh">Tiếng Anh</option>
                                <option value="Tiếng Pháp">Tiếng Pháp</option>
                                <option value="Tiếng Trung">Tiếng Trung</option>
                                <option value="Tiếng Nhật">Tiếng Nhật</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Giá (VNĐ) <span class="required">*</span></label>
                            <input type="number" name="price" step="1000" required>
                        </div>
                        <div class="form-group">
                            <label>Số lượng tồn kho <span class="required">*</span></label>
                            <input type="number" name="stockQuantity" min="0" value="0" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Định dạng sách</label>
                            <select name="format">
                                <option value="paperback">📖 Bìa mềm</option>
                                <option value="hardcover">📕 Bìa cứng</option>
                                <option value="ebook">💻 Ebook</option>
                                <option value="audiobook">🎧 Audiobook</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Thể loại</label>
                            <select name="genreIds" multiple size="4">
                                <c:forEach var="genre" items="${genres}">
                                    <option value="${genre.genreId}">${genre.genreName}</option>
                                </c:forEach>
                            </select>
                            <small style="color:#888;">Giữ Ctrl để chọn nhiều thể loại</small>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Ảnh bìa sách</label>
                        <input type="file" name="coverImage" accept="image/*">
                    </div>

                    <div class="form-group">
                        <label>Mô tả sách</label>
                        <textarea name="description" rows="5" placeholder="Mô tả chi tiết về sách..."></textarea>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/admin/books" class="btn-secondary">Hủy</a>
                        <button type="submit" class="btn-primary">📚 Thêm sách</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</body>
</html>