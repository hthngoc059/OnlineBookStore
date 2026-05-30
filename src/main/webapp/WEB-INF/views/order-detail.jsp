<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Chi tiết đơn hàng #${order.orderId} – Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
</head>
<body>

<!-- ===== NAVBAR ===== -->
<nav class="navbar">
            <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"></h1>
            <!--NAV LINKS -->
            <ul class="navbar__nav">
                <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/books">Tất cả sách</a></li>
                <li><a href="${pageContext.request.contextPath}/about">Giới thiệu</a></li>
                <li><a href="${pageContext.request.contextPath}/contact">Liên hệ</a></li>
                <li class="navbar__search-item">
                    <form action="${pageContext.request.contextPath}/books" method="get">
                        <input type="hidden" 
                                name="${_csrf.parameterName}" 
                                value="${_csrf.token}"/>
                        <input type="hidden" name="action" value="search">
                        <input type="text" name="keyword" placeholder="Tìm sách..." value="${param.keyword}" autocomplete="off">
                        <button type="submit"><img src="${pageContext.request.contextPath}/images/magnifying-glass.png" width="30" height="30" alt="search"/></button>
                    </form>
                </li>
                <c:if test="${sessionScope.currentUser.role=='admin'}">
                    <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dành cho quản trị viên</a></li>
                </c:if>
            </ul>
            <!--USER ACTION-->
            <div class="navbar__action">
                        <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
                            <img src="${pageContext.request.contextPath}/images/online-shopping.png" width="30" height="30" alt="cart"/>
                            <c:if test="${sessionScope.cartCount > 0}">
                                <span class="cart-count">${sessionScope.cartCount}</span>
                            </c:if>
                        </a>
                        <a href="${pageContext.request.contextPath}/notifications" class="btn-cart">
                            <img src="${pageContext.request.contextPath}/images/bell.png" width="30" height="30" alt="cart"/>
                          
                        </a>
                        <a href="${pageContext.request.contextPath}/wishlist" class="btn-cart">
                            <img src="${pageContext.request.contextPath}/images/e-commerce.png" width="30" height="30" alt="cart"/>
                            
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
                                <%-- User is NOT logged in --%>
                                <a href="javascript:void(0)" onclick="openModal('login')" class="btn-link">Tài khoản</a>
                            </c:otherwise>    
                        </c:choose>
            </div>
        </nav>

<!-- ===== NỘI DUNG ===== -->
<div class="order-history-container">

    <!-- Breadcrumb -->
    <nav class="breadcrumb" style="margin-bottom:24px;">
        <a href="${pageContext.request.contextPath}/">Trang chủ</a>
        <span>›</span>
        <a href="${pageContext.request.contextPath}/orders">Lịch sử đơn hàng</a>
        <span>›</span>
        <span>Đơn hàng #${order.orderId}</span>
    </nav>

    <!-- Header đơn hàng -->
    <div class="section-header" style="margin-bottom:28px; justify-content:space-between;">
        <h2 class="section-title">Đơn hàng #${order.orderId}</h2>
        <span class="order-status order-status--${order.status}"
              style="padding:6px 18px; border-radius:30px; font-size:0.82rem; font-weight:700;
                     background:#f0f5eb; color:#405a28; border:1.5px solid #405a28;">
            ${order.status}
        </span>
    </div>

    <div style="display:grid; grid-template-columns:1fr 340px; gap:28px; align-items:flex-start;">

        <!-- CỘT TRÁI: danh sách sách + thông tin giao hàng -->
        <div>

            <!-- Danh sách sách -->
            <div class="co-card" style="margin-bottom:20px;">
                <div class="co-card-header">
                    <i class="bi bi-bag co-icon"></i>
                    <h2>Sách đã đặt</h2>
                </div>
                <div class="co-card-body" style="padding:0;">
                    <ul class="summary-items" style="padding:0 22px;">
                        <c:forEach var="item" items="${order.items}">
                            <li class="summary-item">
                                <img src="${not empty item.coverImageUrl
                                            ? item.coverImageUrl
                                            : pageContext.request.contextPath.concat('/images/default-book.jpg')}"
                                     alt="${item.bookTitle}"
                                     onerror="this.src='${pageContext.request.contextPath}/images/default-book.jpg'"/>
                                <div class="si-info">
                                    <div class="si-title">${item.bookTitle}</div>
                                    <div class="si-author">${item.bookAuthor}</div>
                                    <div class="si-bottom">
                                        <span class="si-qty">×${item.quantity}</span>
                                        <span class="si-subtotal">
                                            <fmt:formatNumber
                                                value="${item.subtotal}"
                                                type="number" maxFractionDigits="0"/>đ
                                        </span>
                                    </div>
                                </div>
                            </li>
                        </c:forEach>                    
                    </ul>
                </div>
            </div>

            <!-- Địa chỉ giao hàng -->
            <c:if test="${not empty order.address}">
            <div class="co-card">
                <div class="co-card-header">
                    <i class="bi bi-geo-alt-fill co-icon"></i>
                    <h2>Địa chỉ giao hàng</h2>
                </div>
                <div class="co-card-body">
                    <div class="addr-name" style="font-size:0.95rem;">${order.address.fullName}</div>
                    <div class="addr-text" style="font-size:0.88rem; margin-top:6px; line-height:1.7;">
                        <i class="bi bi-telephone" style="margin-right:6px;"></i>${order.address.phone}<br/>
                        <i class="bi bi-house" style="margin-right:6px;"></i>
                        ${order.address.addressLine}, ${order.address.ward},
                        ${order.address.district}, ${order.address.city}
                    </div>
                </div>
            </div>
            </c:if>

        </div>

        <!-- CỘT PHẢI: tóm tắt thanh toán + hành động -->
        <div>

            <!-- Tóm tắt tiền -->
            <div class="co-card" style="margin-bottom:20px;">
                <div class="co-card-header">
                    <i class="bi bi-receipt co-icon"></i>
                    <h2>Tóm tắt thanh toán</h2>
                </div>
                <div class="co-card-body">
                    <table class="totals-table">
                        <tr>
                            <td class="label-cell">Tạm tính</td>
                            <td><fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/>đ</td>
                        </tr>
                        <c:if test="${order.discountAmount != null && order.discountAmount > 0}">
                        <tr>
                            <td class="label-cell">Giảm giá</td>
                            <td class="discount-cell">
                                -<fmt:formatNumber value="${order.discountAmount}" type="number" maxFractionDigits="0"/>đ
                            </td>
                        </tr>
                        </c:if>
                        <tr>
                            <td class="label-cell">Vận chuyển</td>
                            <td style="color:#1a7a3c; font-weight:700;">Miễn phí</td>
                        </tr>
                        <tr class="grand-row">
                            <td>Tổng cộng</td>
                            <td><fmt:formatNumber value="${order.finalAmount}" type="number" maxFractionDigits="0"/>đ</td>
                        </tr>
                    </table>
                </div>
            </div>

            <!-- Thông tin thanh toán -->
            <div class="co-card" style="margin-bottom:20px;">
                <div class="co-card-header">
                    <i class="bi bi-credit-card co-icon"></i>
                    <h2>Thanh toán</h2>
                </div>
                <div class="co-card-body">
                    <c:if test="${not empty order.payment}">
                        <p style="font-size:0.88rem; color:#555; margin:0 0 6px;">
                            <strong>Phương thức:</strong>
                            <c:choose>
                                <c:when test="${order.payment.paymentMethod == 'cod'}">Tiền mặt khi nhận hàng</c:when>
                                <c:when test="${order.payment.paymentMethod == 'banking'}">Chuyển khoản ngân hàng</c:when>
                                <c:otherwise>${order.payment.paymentMethod}</c:otherwise>
                            </c:choose>
                        </p>
                        <p style="font-size:0.88rem; color:#555; margin:0;">
                            <strong>Trạng thái:</strong>
                            <span style="color:${order.payment.paymentStatus == 'paid' ? '#1a7a3c' : '#e67e00'}; font-weight:700;">
                                <c:choose>
                                    <c:when test="${order.payment.paymentStatus == 'paid'}">Đã thanh toán</c:when>
                                    <c:when test="${order.payment.paymentStatus == 'pending'}">Chờ thanh toán</c:when>
                                    <c:when test="${order.payment.paymentStatus == 'refunded'}">Đã hoàn tiền</c:when>
                                    <c:otherwise>${order.payment.paymentStatus}</c:otherwise>
                                </c:choose>
                            </span>
                        </p>
                    </c:if>
                    <c:if test="${empty order.payment}">
                        <p style="font-size:0.88rem; color:#aaa; margin:0;">Chưa có thông tin thanh toán.</p>
                    </c:if>
                </div>
            </div>

            <!-- Nút hành động -->
            <div style="display:flex; flex-direction:column; gap:10px;">
                <a href="${pageContext.request.contextPath}/orders" class="btn-continue"
                   style="text-align:center;">
                    Xem tất cả đơn hàng
                </a>
                <c:if test="${order.status == 'pending'}">
                    <form method="post"
                          action="${pageContext.request.contextPath}/orders/${order.orderId}/cancel"
                          onsubmit="return confirm('Bạn có chắc muốn hủy đơn hàng này?')">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit" class="btn-clear"
                                style="width:100%; padding:10px; border:none; cursor:pointer;
                                       background:none; font-size:0.88rem;">
                            ✕ Hủy đơn hàng
                        </button>
                    </form>
                </c:if>
                <c:if test="${param.cancelled == 'true'}">
                    <div class="modal-alert modal-alert-error" style="text-align:center;">
                        Đơn hàng đã được hủy thành công.
                    </div>
                </c:if>
            </div>

        </div>
    </div>
</div>

<!-- ===== FOOTER ===== -->
<footer>
  <div class="footer__inner">
    <p class="footer__copy">© 2024 BookStore. All rights reserved.</p>
    <div class="footer__social">
      <span class="footer__social-label">Theo dõi chúng tôi</span>
      <div class="footer__social-links">
        <a href="#" class="footer__social-btn" title="Facebook">
          <i class="bi bi-facebook"></i>
        </a>
        <a href="#" class="footer__social-btn" title="Instagram">
          <i class="bi bi-instagram"></i>
        </a>
        <a href="#" class="footer__social-btn" title="Zalo">
          <i class="bi bi-chat-dots-fill"></i>
        </a>
        <a href="#" class="footer__social-btn" title="YouTube">
          <i class="bi bi-youtube"></i>
        </a>
      </div>
    </div>
  </div>
</footer>


<!-- ===== MODAL ĐẶT HÀNG THÀNH CÔNG ===== -->
<c:if test="${justPlaced}">
<div id="successModal" style="display:flex; position:fixed; inset:0;
     background:rgba(0,0,0,0.52); z-index:9999;
     align-items:center; justify-content:center; padding:20px;">
    <div class="success-modal-box">
        <div class="success-modal-header">
            <div class="success-modal-icon">✓</div>
            <h2>Đặt hàng thành công!</h2>
            <p>Cảm ơn bạn. Đơn hàng #${order.orderId} đang được xử lý.</p>
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
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') closeSuccessModal();
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
