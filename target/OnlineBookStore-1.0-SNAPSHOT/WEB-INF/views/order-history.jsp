<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử đặt hàng - Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
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
                <input type="hidden" 
           name="${_csrf.parameterName}" 
           value="${_csrf.token}"/>
                <input type="hidden" name="action" value="search">
                <input type="text" name="keyword" placeholder="Tìm sách..." autocomplete="off">
                <button type="submit">
                    <img src="${pageContext.request.contextPath}/images/magnifying-glass.png"
                         width="30" height="30" alt="search"/>
                </button>
            </form>
        </li>
        <c:if test="${sessionScope.currentUser.role == 'admin'}">
            <li><a href="${pageContext.request.contextPath}/admin/dashboard">Quản trị</a></li>
        </c:if>
    </ul>
    <div class="navbar__action">
        <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
            <img src="${pageContext.request.contextPath}/images/online-shopping.png"
                 width="30" height="30" alt="cart"/>
            <c:if test="${sessionScope.cartCount > 0}">
                <span class="cart-count">${sessionScope.cartCount}</span>
            </c:if>
        </a>
        <c:choose>
            <c:when test="${sessionScope.currentUser != null}">
                <div class="user-dropdown">
                    <div class="user-dropdown__trigger">
                        <img src="${pageContext.request.contextPath}/images/user.png" 
                             width="26" height="26" alt="user"/>
                        <span>Xin chào, <strong>${sessionScope.currentUser.username}</strong></span>
                        <i class="bi bi-chevron-down" style="font-size:0.7rem;"></i>
                    </div>
                    <div class="user-dropdown__menu">
                        <div class="user-dropdown__arrow"></div>
                        <a href="${pageContext.request.contextPath}/profile" class="user-dropdown__item">
                            <i class="bi bi-person-circle"></i>
                            Tài khoản của tôi
                        </a>
                        <a href="${pageContext.request.contextPath}/orders" class="user-dropdown__item">
                            <i class="bi bi-bag-check"></i>
                            Đơn mua
                        </a>
                        <div class="user-dropdown__divider"></div>
                        <a href="${pageContext.request.contextPath}/user?action=logout" class="user-dropdown__item user-dropdown__item--logout">
                            <i class="bi bi-box-arrow-right"></i>
                            Đăng xuất
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/" class="btn-link">Tài khoản</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>

<!-- ===== NỘI DUNG ===== -->
<div class="order-history-container">
    <div class="section-header" style="margin-bottom: 28px;">
        <h2 class="section-title">Lịch sử đặt hàng</h2>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="cart-empty">
                <h3>Bạn chưa có đơn hàng nào</h3>
                <p>Hãy khám phá và mua sắm ngay!</p>
                <a href="${pageContext.request.contextPath}/books">Tiếp tục mua sắm →</a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Ngày đặt</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th>Thanh toán</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td data-label="Mã đơn">
                                <a href="${pageContext.request.contextPath}/orders/${order.orderId}"
                                   style="color:#405a28; font-weight:700; text-decoration:none;">
                                    #${order.orderId}
                                </a>
                            </td>
                            <td data-label="Ngày đặt">
                                ${order.orderDateFormatted}
                            </td>
                            <td data-label="Tổng tiền" class="cart-price">
                                <fmt:formatNumber value="${order.totalAmount}"
                                                  type="number" maxFractionDigits="0"/>đ
                            </td>
                            <td data-label="Trạng thái">
                                <span class="order-status order-status--${order.status}">
                                    ${order.status}
                                </span>
                            </td>
                            <td data-label="Thanh toán">${order.paymentStatus}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <%-- Pagination --%>
            <c:if test="${totalPages > 1}">
                <div style="text-align:center; margin-top: 32px; display:flex;
                            justify-content:center; gap:8px;">
                    <c:forEach begin="0" end="${totalPages - 1}" var="p">
                        <a href="${pageContext.request.contextPath}/orders?page=${p}"
                           style="padding: 8px 14px; border-radius: 20px; border: 2px solid #405a28;
                                  font-weight: 700; font-size: 0.85rem;
                                  background: ${p == currentPage ? '#405a28' : '#fff'};
                                  color: ${p == currentPage ? '#fff' : '#405a28'};">
                            ${p + 1}
                        </a>
                    </c:forEach>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>

<!-- ===== FOOTER ===== -->
<footer>
    <p>&copy; 2024 Nhà Sách Online. All rights reserved.</p>
</footer>

<!-- ===== MODAL ĐẶT HÀNG THÀNH CÔNG ===== -->
<c:if test="${orderSuccess == true}">
<div id="successModal" style="display:flex; position:fixed; inset:0;
     background:rgba(0,0,0,0.52); z-index:9999;
     align-items:center; justify-content:center; padding:20px;">
    <div class="success-modal-box">
        <div class="success-modal-header">
            <div class="success-modal-icon">✓</div>
            <h2>Đặt hàng thành công!</h2>
            <p>Cảm ơn bạn. Đơn hàng đang được xử lý.</p>
        </div>
        <div class="success-modal-body">
            <div class="success-modal-btns">
                <a href="${pageContext.request.contextPath}/"
                   class="btn-modal-continue">Tiếp tục mua sắm</a>
                <a href="javascript:void(0)" onclick="closeSuccessModal()"
                   class="btn-modal-view">Xem đơn hàng →</a>
            </div>
        </div>
    </div>
</div>
<script>
    function closeSuccessModal() {
        document.getElementById('successModal').style.display = 'none';
    }
    document.getElementById('successModal').addEventListener('click', function(e) {
        if (e.target === this) closeSuccessModal();
    });
</script>
</c:if>
<script>
(function() {
    const trigger = document.querySelector('.user-dropdown__trigger');
    const menu    = document.querySelector('.user-dropdown__menu');
    if (!trigger || !menu) return;

    // Bấm vào trigger → toggle menu
    trigger.addEventListener('click', function(e) {
        e.stopPropagation();
        menu.classList.toggle('open');
    });

    // Bấm ra ngoài → đóng menu
    document.addEventListener('click', function() {
        menu.classList.remove('open');
    });

    // Bấm vào menu không đóng
    menu.addEventListener('click', function(e) {
        e.stopPropagation();
    });
})();
</script>
</body>
</html>