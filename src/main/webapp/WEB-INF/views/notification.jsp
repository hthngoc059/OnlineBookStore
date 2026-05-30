<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core"      prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông báo – Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
</head>
<body>

<!-- ═══════════ NAVBAR ═══════════ -->
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

<!-- ═══════════ PAGE HEADER ═══════════ -->
<div class="notif-page-header">
    <div class="notif-page-header__inner">
        <div class="search-breadcrumb">
            <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
            <span>›</span>
            <span>Thông báo</span>
        </div>
        <div class="notif-page-title-row">
            <h2 class="notif-page-title">
                Thông báo
                <c:if test="${unreadCount > 0}">
                    <span class="notif-unread-chip">${unreadCount} chưa đọc</span>
                </c:if>
            </h2>
            <div class="notif-bulk-actions">
                <c:if test="${unreadCount > 0}">
                    <form action="${pageContext.request.contextPath}/notifications"
                          method="post" style="display:inline;">
                        <input type="hidden" name="action" value="markAllRead">
                        <button type="submit" class="btn-notif-action btn-notif-action--secondary">
                            ✓ Đánh dấu tất cả đã đọc
                        </button>
                    </form>
                </c:if>
                <c:if test="${totalCount > 0}">
                    <form action="${pageContext.request.contextPath}/notifications"
                          method="post" style="display:inline;"
                          onsubmit="return confirm('Xóa tất cả thông báo?')">
                        <input type="hidden" name="action" value="deleteAll">
                        <button type="submit" class="btn-notif-action btn-notif-action--danger">
                            🗑 Xóa tất cả
                        </button>
                    </form>
                </c:if>
            </div>
        </div>
    </div>
</div>

<!-- ═══════════ MAIN ═══════════ -->
<main class="notif-container">
    <c:choose>
        <c:when test="${empty notifications}">
            <div class="notif-empty">
                <h3>Chưa có thông báo nào</h3>
                <p>Các thông báo về đơn hàng, sách mới và khuyến mãi sẽ xuất hiện ở đây.</p>
                <a href="${pageContext.request.contextPath}/books" class="btn-view-more">
                    Khám phá sách ngay
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="notif-list">
                <c:forEach var="notif" items="${notifications}">
                    <div class="notif-item ${notif.isRead ? '' : 'notif-item--unread'}">

                        <!-- Icon: resolved in Java Controller to avoid JSTL Unicode issues -->
                        <div class="notif-item__icon">
                            ${iconMap[notif.notificationId]}
                        </div>

                        <!-- Content -->
                        <div class="notif-item__body">
                            <div class="notif-item__title">
                                <c:if test="${!notif.isRead}">
                                    <span class="notif-dot"></span>
                                </c:if>
                                <c:out value="${notif.title}"/>
                            </div>
                            <div class="notif-item__message">
                                <c:out value="${notif.message}"/>
                            </div>
                            <%-- Dùng dateMap (String) thay vì fmt:formatDate để tránh lỗi LocalDateTime --%>
                            <div class="notif-item__meta">
                                ${dateMap[notif.notificationId]}
                            </div>
                        </div>

                        <!-- Actions -->
                        <div class="notif-item__actions">
                            <c:if test="${!notif.isRead}">
                                <form action="${pageContext.request.contextPath}/notifications"
                                      method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="markRead">
                                    <input type="hidden" name="id" value="${notif.notificationId}">
                                    <button type="submit" class="notif-btn-read" title="Đánh dấu đã đọc">✓</button>
                                </form>
                            </c:if>
                            <form action="${pageContext.request.contextPath}/notifications"
                                  method="post" style="display:inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${notif.notificationId}">
                                <button type="submit" class="notif-btn-delete"
                                        title="Xóa thông báo"
                                        onclick="return confirm('Xóa thông báo này?')">✕</button>
                            </form>
                        </div>

                    </div>
                </c:forEach>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="notif-pagination">
                    <c:if test="${currentPage > 0}">
                        <a href="${pageContext.request.contextPath}/notifications?page=${currentPage - 1}"
                           class="notif-page-btn">← Trước</a>
                    </c:if>
                    <c:forEach begin="0" end="${totalPages - 1}" var="p">
                        <a href="${pageContext.request.contextPath}/notifications?page=${p}"
                           class="notif-page-btn ${p == currentPage ? 'active' : ''}">${p + 1}</a>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages - 1}">
                        <a href="${pageContext.request.contextPath}/notifications?page=${currentPage + 1}"
                           class="notif-page-btn">Tiếp →</a>
                    </c:if>
                </div>
            </c:if>

        </c:otherwise>
    </c:choose>
</main>

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

<script>
(function() {
    var trigger = document.querySelector('.user-dropdown__trigger');
    var menu    = document.querySelector('.user-dropdown__menu');
    if (!trigger || !menu) return;
    trigger.addEventListener('click', function(e) {
        e.stopPropagation();
        menu.classList.toggle('open');
    });
    document.addEventListener('click', function() { menu.classList.remove('open'); });
    menu.addEventListener('click', function(e) { e.stopPropagation(); });
})();
</script>

</body>
</html>
