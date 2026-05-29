<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý sách - Admin</title>
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
                    <h1>Quản lý sách</h1>
                    <p>Thêm, sửa, xóa và quản lý thông tin sách</p>
                </div>
                <a href="${pageContext.request.contextPath}/admin/books?action=add" class="btn-primary">+ Thêm sách mới</a>
            </header>

            <!-- Filter Bar -->
            <div class="filter-bar">
                <form action="${pageContext.request.contextPath}/admin/books" method="get" class="search-form">
                    <input type="text" name="keyword" placeholder="Tìm theo tên, tác giả..." value="${param.keyword}">
                    <select name="genre">
                        <option value="">Tất cả thể loại</option>
                        <c:forEach var="genre" items="${genres}">
                            <option value="${genre.genreId}" ${param.genre == genre.genreId ? 'selected' : ''}>${genre.genreName}</option>
                        </c:forEach>
                    </select>
                    <select name="stockStatus">
                        <option value="">Tất cả</option>
                        <option value="inStock" ${param.stockStatus == 'inStock' ? 'selected' : ''}>Còn hàng</option>
                        <option value="lowStock" ${param.stockStatus == 'lowStock' ? 'selected' : ''}>Sắp hết (≤5)</option>
                        <option value="outStock" ${param.stockStatus == 'outStock' ? 'selected' : ''}>Hết hàng</option>
                    </select>
                    <button type="submit" class="btn-secondary">🔍 Tìm kiếm</button>
                </form>
            </div>

            <!-- Books Table -->
            <div class="data-table-container">
                <table class="data-table">
                    <thead>
                        <tr><th>ID</th><th>Ảnh bìa</th><th>Tên sách</th><th>Tác giả</th><th>Giá</th><th>Tồn kho</th><th>Thể loại</th><th>Thao tác</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="book" items="${books}">
                            <tr>
                                <td>${book.bookId}</td>
                                <td><img src="${not empty book.coverImageUrl ? book.coverImageUrl : pageContext.request.contextPath.concat('/images/default-book.jpg')}" class="table-avatar" alt="${book.title}"></td>
                                <td class="book-title-cell">${book.title}</td>
                                <td>${book.author}</td>
                                <td><fmt:formatNumber value="${book.price}" type="number" groupingUsed="true"/> ₫</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${book.stockQuantity <= 0}"><span class="stock-badge out">Hết hàng</span></c:when>
                                        <c:when test="${book.stockQuantity <= 5}"><span class="stock-badge low">Còn ${book.stockQuantity}</span></c:when>
                                        <c:otherwise><span class="stock-badge ok">${book.stockQuantity}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:forEach var="genre" items="${book.genres}" varStatus="st">
                                        ${genre.genreName}${!st.last ? ', ' : ''}
                                    </c:forEach>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/books?action=edit&id=${book.bookId}" class="btn-icon">✏️ Sửa</a>
                                    <a href="javascript:void(0)" onclick="confirmDelete(${book.bookId})" class="btn-icon btn-danger">🗑️ Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty books}">
                            <tr><td colspan="8" style="text-align:center;">Không có sách nào</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 0}"><a href="?page=${currentPage-1}&keyword=${param.keyword}&genre=${param.genre}&stockStatus=${param.stockStatus}">← Trước</a></c:if>
                    <span>Trang ${currentPage+1} / ${totalPages}</span>
                    <c:if test="${currentPage+1 < totalPages}"><a href="?page=${currentPage+1}&keyword=${param.keyword}&genre=${param.genre}&stockStatus=${param.stockStatus}">Sau →</a></c:if>
                </div>
            </c:if>
        </main>
    </div>

    <script>
        function confirmDelete(bookId) {
            if(confirm('Bạn có chắc muốn xóa sách này không?')) {
                window.location.href = '${pageContext.request.contextPath}/admin/books?action=delete&id=' + bookId;
            }
        }
    </script>
</body>
</html>