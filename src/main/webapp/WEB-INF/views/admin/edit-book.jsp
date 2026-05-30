<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sửa sách - Admin</title>
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
                    <h1>Sửa sách</h1>
                    <p>Cập nhật thông tin sách</p>
                </div>
            </header>

            <div class="form-container">
                <form action="${pageContext.request.contextPath}/admin/books" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="bookId" value="${book.bookId}">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Tên sách <span class="required">*</span></label>
                            <input type="text" name="title" value="${book.title}" required>
                        </div>
                        <div class="form-group">
                            <label>Tác giả <span class="required">*</span></label>
                            <input type="text" name="author" value="${book.author}" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>ISBN</label>
                            <input type="text" name="isbn" value="${book.isbn}">
                        </div>
                        <div class="form-group">
                            <label>Nhà xuất bản</label>
                            <input type="text" name="publisher" value="${book.publisher}">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Năm xuất bản</label>
                            <input type="number" name="publishedYear" value="${book.publishedYear}">
                        </div>
                        <div class="form-group">
                            <label>Ngôn ngữ</label>
                            <select name="language">
                                <option value="Tiếng Việt" ${book.language == 'Tiếng Việt' ? 'selected' : ''}>Tiếng Việt</option>
                                <option value="Tiếng Anh" ${book.language == 'Tiếng Anh' ? 'selected' : ''}>Tiếng Anh</option>
                                <option value="Tiếng Pháp" ${book.language == 'Tiếng Pháp' ? 'selected' : ''}>Tiếng Pháp</option>
                                <option value="Tiếng Trung" ${book.language == 'Tiếng Trung' ? 'selected' : ''}>Tiếng Trung</option>
                                <option value="Tiếng Nhật" ${book.language == 'Tiếng Nhật' ? 'selected' : ''}>Tiếng Nhật</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Giá (VNĐ) <span class="required">*</span></label>
                            <input type="number" name="price" step="1000" value="${book.price}" required>
                        </div>
                        <div class="form-group">
                            <label>Số lượng tồn kho <span class="required">*</span></label>
                            <input type="number" name="stockQuantity" min="0" value="${book.stockQuantity}" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Định dạng sách</label>
                            <select name="format">
                                <option value="paperback" ${book.format == 'paperback' ? 'selected' : ''}>📖 Bìa mềm</option>
                                <option value="hardcover" ${book.format == 'hardcover' ? 'selected' : ''}>📕 Bìa cứng</option>
                                <option value="ebook" ${book.format == 'ebook' ? 'selected' : ''}>💻 Ebook</option>
                                <option value="audiobook" ${book.format == 'audiobook' ? 'selected' : ''}>🎧 Audiobook</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Thể loại</label>
                            <select name="genreIds" multiple size="4">
                                <c:forEach var="genre" items="${genres}">
                                    <option value="${genre.genreId}" ${book.genreIds.contains(genre.genreId) ? 'selected' : ''}>${genre.genreName}</option>
                                </c:forEach>
                            </select>
                            <small style="color:#888;">Giữ Ctrl để chọn nhiều thể loại</small>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Ảnh bìa hiện tại</label>
                        <c:if test="${not empty book.coverImageUrl}">
                            <div style="margin-bottom: 8px;">
                                <img src="${book.coverImageUrl}" style="width: 80px; height: 100px; object-fit: cover; border-radius: 8px;">
                            </div>
                        </c:if>
                        <input type="file" name="coverImage" accept="image/*">
                        <small style="color:#888;">Để trống nếu không muốn thay đổi ảnh</small>
                    </div>

                    <div class="form-group">
                        <label>Mô tả sách</label>
                        <textarea name="description" rows="5">${book.description}</textarea>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/admin/books" class="btn-secondary">Hủy</a>
                        <button type="submit" class="btn-primary">💾 Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</body>
</html>