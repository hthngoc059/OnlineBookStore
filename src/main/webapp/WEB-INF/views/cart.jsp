<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ hàng - Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- ===== NAVBAR ===== -->
    <nav class="navbar">
        <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"></h1>
        <ul class="navbar__nav">
            <li><a href="${pageContext.request.contextPath}/">Trang chủ</a></li>
            <li><a href="${pageContext.request.contextPath}/books">Tất cả sách</a></li>
            <li><a href="${pageContext.request.contextPath}/">Giới thiệu</a></li>
            <li><a href="${pageContext.request.contextPath}/">Liên hệ</a></li>
            <li class="navbar__search-item">
                <form action="${pageContext.request.contextPath}/books" method="get">
                    <input type="hidden" name="action" value="search">
                    <input type="text" name="keyword" placeholder="Tìm sách..." autocomplete="off">
                    <button type="submit">
                        <img src="${pageContext.request.contextPath}/images/magnifying-glass.png" width="30" height="30" alt="search"/>
                    </button>
                </form>
            </li>
            <c:if test="${sessionScope.user.role=='admin'}">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard">Quản trị</a></li>
            </c:if>
        </ul>
        <div class="navbar__action">
            <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
                <img src="${pageContext.request.contextPath}/images/online-shopping.png" width="30" height="30" alt="cart"/>
                <c:if test="${sessionScope.cartCount > 0}">
                    <span class="cart-count">${sessionScope.cartCount}</span>
                </c:if>
            </a>
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <span>Xin chào, ${sessionScope.user.username}</span>
                    <a href="${pageContext.request.contextPath}/user?action=logout" class="btn-link">Đăng xuất</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/books" class="btn-link">Tài khoản</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>

    <!-- ===== CART CONTENT ===== -->
    <div class="cart-container">
        <h1 class="cart-title">🛒 Giỏ hàng của bạn</h1>

        <c:choose>
            <c:when test="${sessionScope.user == null}">
                <div class="cart-empty">
                    <h3>Vui lòng đăng nhập để xem giỏ hàng</h3>
                    <a href="${pageContext.request.contextPath}/books">Đăng nhập ngay</a>
                </div>
            </c:when>
            <c:when test="${empty cartItems}">
                <div class="cart-empty">
                    <h3>Giỏ hàng trống</h3>
                    <p>Hãy thêm sách vào giỏ hàng của bạn!</p>
                    <a href="${pageContext.request.contextPath}/books">Tiếp tục mua sắm →</a>
                </div>
            </c:when>
            <c:otherwise>
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th>Giá</th>
                            <th>Số lượng</th>
                            <th>Tạm tính</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}">
                            <tr>
                                <td data-label="Sản phẩm">
                                    <div class="cart-product">
                                        <img src="${not empty item.book.coverImageUrl 
                                                      ? item.book.coverImageUrl 
                                                      : pageContext.request.contextPath.concat('/images/default-book.jpg')}"
                                             alt="${item.book.title}"
                                             onerror="this.src='${pageContext.request.contextPath}/images/default-book.jpg'">
                                        <div class="cart-product-info">
                                            <h4>
                                                <a href="${pageContext.request.contextPath}/books?action=detail&id=${item.book.bookId}">
                                                    ${item.book.title}
                                                </a>
                                            </h4>
                                            <p>${item.book.author}</p>
                                        </div>
                                    </div>
                                </td>
                                <td data-label="Giá" class="cart-price">
                                    <fmt:formatNumber value="${item.book.price}" type="number" groupingUsed="true"/> ₫
                                </td>
                                <td data-label="Số lượng">
                                    <form action="${pageContext.request.contextPath}/cart" method="get" style="display:inline;">
                                        <input type="hidden" name="action" value="update">
                                        <input type="hidden" name="itemId" value="${item.cartItemId}">
                                        <div class="cart-qty">
                                            <button type="button" onclick="updateQty(${item.cartItemId}, -1, ${item.book.stockQuantity})">−</button>
                                            <input type="number" name="qty" value="${item.quantity}" 
                                                   min="1" max="${item.book.stockQuantity}"
                                                   onchange="this.form.submit()">
                                            <button type="button" onclick="updateQty(${item.cartItemId}, 1, ${item.book.stockQuantity})">+</button>
                                        </div>
                                    </form>
                                </td>
                                <td data-label="Tạm tính" class="cart-subtotal">
                                    <fmt:formatNumber value="${item.book.price * item.quantity}" type="number" groupingUsed="true"/> ₫
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/cart?action=remove&itemId=${item.cartItemId}" 
                                       class="cart-remove"
                                       onclick="return confirm('Xóa sản phẩm này khỏi giỏ hàng?')">
                                        ✕ Xóa
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- Cart Summary -->
                <div class="cart-summary">
                    <div class="cart-summary-left">
                        <a href="${pageContext.request.contextPath}/books" class="btn-continue">← Tiếp tục mua sắm</a>
                        <a href="${pageContext.request.contextPath}/cart?action=clear" 
                           class="btn-clear"
                           onclick="return confirm('Xóa toàn bộ giỏ hàng?')">
                            🗑 Xóa tất cả
                        </a>
                    </div>
                    <div class="cart-summary-right">
                        <p class="cart-total-label">Tổng cộng</p>
                        <p class="cart-total-price">
                            <fmt:formatNumber value="${cartTotal}" type="number" groupingUsed="true"/> ₫
                        </p>
                        <a href="${pageContext.request.contextPath}/checkout" class="btn-checkout">
                            🛒 Thanh toán
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- ===== FOOTER ===== -->
    <footer>
        <p>&copy; 2024 Nhà Sách Online. All rights reserved.</p>
    </footer>

    <script>
        function updateQty(itemId, delta, maxQty) {
            var form = event.target.closest('form');
            var input = form.querySelector('input[name="qty"]');
            var currentQty = parseInt(input.value);
            var newQty = currentQty + delta;
            
            if (newQty >= 1 && newQty <= maxQty) {
                input.value = newQty;
                form.submit();
            }
        }
    </script>

</body>
</html>