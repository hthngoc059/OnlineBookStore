<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Kết quả tìm kiếm: "${keyword}"</h2>

<c:choose>
    <c:when test="${empty books}">
        <p>Không tìm thấy sách.</p>
    </c:when>

    <c:otherwise>
        <div class="books-grid">

            <c:forEach var="book" items="${books}">

                <div class="book-card">

                    <a href="${pageContext.request.contextPath}/books?action=detail&id=${book.bookId}">
                        <img class="book-image"
                             src="${not empty book.coverImageUrl
                                    ? book.coverImageUrl
                                    : pageContext.request.contextPath.concat('/images/default-book.jpg')}">
                    </a>

                    <div class="book-info">
                        <h3>${book.title}</h3>
                        <p>${book.author}</p>
                        <p>${book.price} ₫</p>
                    </div>

                </div>

            </c:forEach>

        </div>
    </c:otherwise>
</c:choose>