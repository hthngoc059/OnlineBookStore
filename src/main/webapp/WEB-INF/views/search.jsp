<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Kết quả tìm kiếm</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<h2 style="margin:30px;">
    Kết quả tìm kiếm cho:
    "<span style="color:#e74c3c">${keyword}</span>"
</h2>

<c:choose>

    <c:when test="${empty books}">
        <div style="padding:30px;">
            Không tìm thấy sách phù hợp.
        </div>
    </c:when>

    <c:otherwise>

        <div class="books-grid">

            <c:forEach var="book" items="${books}">

                <div class="book-card">

                    <a href="${pageContext.request.contextPath}/books?action=detail&id=${book.bookId}">
                        <img class="book-image"
                             src="${not empty book.coverImageUrl
                                   ? book.coverImageUrl
                                   : pageContext.request.contextPath.concat('/images/default-book.jpg')}"
                             alt="${book.title}">
                    </a>

                    <div class="book-info">

                        <h3 class="book-title">
                            ${book.title}
                        </h3>

                        <p class="book-author">
                            ${book.author}
                        </p>

                        <p class="book-price">
                            ${book.price} ₫
                        </p>

                        <div class="book-actions">

                            <a href="${pageContext.request.contextPath}/books?action=detail&id=${book.bookId}"
                               class="btn-detail">
                                Chi tiết
                            </a>

                            <a href="${pageContext.request.contextPath}/cart?action=add&id=${book.bookId}"
                               class="btn-add-cart">
                                Thêm giỏ
                            </a>

                        </div>

                    </div>

                </div>

            </c:forEach>

        </div>

    </c:otherwise>

</c:choose>

</body>
</html>
