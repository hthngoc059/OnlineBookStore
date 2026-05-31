<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <title>Địa chỉ của tôi – Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
</head>
<body>

<%-- ── NAVBAR ── --%>
<nav class="navbar">
    <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"></h1>
    <ul class="navbar__nav">
        <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
        <li><a href="${pageContext.request.contextPath}/books">Tất cả sách</a></li>
        <li><a href="${pageContext.request.contextPath}/about">Giới thiệu</a></li>
        <li><a href="${pageContext.request.contextPath}/contact">Liên hệ</a></li>
        <li class="navbar__search-item">
            <form action="${pageContext.request.contextPath}/books" method="get">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" name="action" value="search">
                <input type="text" name="keyword" placeholder="Tìm sách..." value="${param.keyword}" autocomplete="off">
                <button type="submit"><img src="${pageContext.request.contextPath}/images/magnifying-glass.png" width="30" height="30" alt="search"/></button>
            </form>
        </li>
        <c:if test="${sessionScope.currentUser.role=='admin'}">
            <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dành cho quản trị viên</a></li>
        </c:if>
    </ul>
    <div class="navbar__action">
        <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
            <img src="${pageContext.request.contextPath}/images/online-shopping.png" width="30" height="30" alt="cart"/>
            <c:if test="${sessionScope.cartCount > 0}">
                <span class="cart-count">${sessionScope.cartCount}</span>
            </c:if>
        </a>
        <a href="${pageContext.request.contextPath}/notifications" class="btn-cart">
            <img src="${pageContext.request.contextPath}/images/bell.png" width="30" height="30" alt="notifications"/>
        </a>
        <a href="${pageContext.request.contextPath}/wishlist" class="btn-cart">
            <img src="${pageContext.request.contextPath}/images/e-commerce.png" width="30" height="30" alt="wishlist"/>
        </a>
        <c:choose>
            <c:when test="${sessionScope.currentUser != null}">
                <div class="user-dropdown">
                    <div class="user-dropdown__trigger">
                        <img src="${pageContext.request.contextPath}/images/user.png" width="26" height="26" alt="user"/>
                        <span>Xin chào, <strong>${sessionScope.currentUser.username}</strong></span>
                        <i class="bi bi-chevron-down" style="font-size:0.7rem;"></i>
                    </div>
                    <div class="user-dropdown__menu">
                        <div class="user-dropdown__arrow"></div>
                        <a href="${pageContext.request.contextPath}/profile" class="user-dropdown__item">
                            <i class="bi bi-person-circle"></i> Tài khoản của tôi
                        </a>
                        <a href="${pageContext.request.contextPath}/orders" class="user-dropdown__item">
                            <i class="bi bi-bag-check"></i> Đơn mua
                        </a>
                        <div class="user-dropdown__divider"></div>
                        <a href="${pageContext.request.contextPath}/user?action=logout" class="user-dropdown__item user-dropdown__item--logout">
                            <i class="bi bi-box-arrow-right"></i> Đăng xuất
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <a href="javascript:void(0)" onclick="openModal('login')" class="btn-link">Tài khoản</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>

<%-- ── LAYOUT ── --%>
<div class="profile-container">

    <%-- SIDEBAR --%>
    <div class="profile-sidebar">
        <div class="profile-avatar">
            <i class="bi bi-person-circle"></i>
            <div>
                <div class="profile-name">${sessionScope.currentUser.username}</div>
            </div>
        </div>
        <nav class="profile-nav">
            <div class="profile-nav-group">
                <div class="profile-nav-label">
                    <i class="bi bi-person"></i> Tài khoản của tôi
                </div>
                <a href="${pageContext.request.contextPath}/profile?action=info" class="profile-nav-item">Hồ sơ</a>
                <a href="${pageContext.request.contextPath}/profile?action=password" class="profile-nav-item">Đổi mật khẩu</a>
                <a href="${pageContext.request.contextPath}/profile/addresses" class="profile-nav-item active">Địa chỉ</a>
            </div>
            <a href="${pageContext.request.contextPath}/orders" class="profile-nav-group-link">
                <i class="bi bi-bag-check"></i> Đơn mua
            </a>
        </nav>
    </div>

    <%-- MAIN CONTENT --%>
    <div class="profile-content">

        <%-- Alert messages --%>
        <c:if test="${not empty successMsg}">
            <div class="profile-alert profile-alert--ok">✅ ${successMsg}</div>
        </c:if>
        <c:if test="${not empty errorMsg}">
            <div class="profile-alert profile-alert--err">❌ ${errorMsg}</div>
        </c:if>

        <%-- Header --%>
        <div class="addr-mgmt-header">
            <h2>Địa chỉ của tôi</h2>
            <a href="${pageContext.request.contextPath}/address" class="btn-add-address">
                <i class="bi bi-plus-lg"></i> Thêm địa chỉ
            </a>
        </div>

        <%-- Address list --%>
        <c:choose>
            <c:when test="${empty addresses}">
                <div class="addr-empty">
                    <i class="bi bi-geo-alt"></i>
                    <p>Bạn chưa có địa chỉ nào.</p>
                    <a href="${pageContext.request.contextPath}/address" class="btn-add-address">
                        <i class="bi bi-plus-lg"></i> Thêm địa chỉ mới
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <p class="addr-section-title">Địa chỉ</p>
                <div class="addr-list">
                    <c:forEach var="addr" items="${addresses}">
                        <div class="addr-item">
                            <div class="addr-info">
                                <div class="addr-name-phone">
                                    <span class="addr-name">${addr.fullName}</span>
                                    <span class="addr-divider">|</span>
                                    <span class="addr-phone">(+84) ${addr.phone}</span>
                                </div>
                                <div class="addr-line">
                                    ${addr.addressLine}<br/>
                                    ${addr.ward}, ${addr.district}, ${addr.city}
                                </div>
                                <c:if test="${addr.isDefault == true}">
                                    <div class="addr-tags">
                                        <span class="addr-tag">Mặc định</span>
                                        <span class="addr-tag addr-tag--pickup">Địa chỉ lấy hàng</span>
                                    </div>
                                </c:if>
                            </div>

                            <div class="addr-actions">
                                <div class="addr-action-links">
                                    <a href="${pageContext.request.contextPath}/address?action=edit&id=${addr.addressId}"
                                       class="btn-addr-edit">Cập nhật</a>
                                    <c:if test="${addr.isDefault != true}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/profile/addresses"
                                              style="display:inline;"
                                              onsubmit="return confirm('Bạn có chắc muốn xóa địa chỉ này?');">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="addressId" value="${addr.addressId}"/>
                                            <button type="submit" class="btn-addr-delete">Xóa</button>
                                        </form>
                                    </c:if>
                                </div>
                                <c:if test="${addr.isDefault != true}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/profile/addresses">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <input type="hidden" name="action" value="setDefault"/>
                                        <input type="hidden" name="addressId" value="${addr.addressId}"/>
                                        <button type="submit" class="btn-set-default">Thiết lập mặc định</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</div>

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
    const trigger = document.querySelector('.user-dropdown__trigger');
    const menu    = document.querySelector('.user-dropdown__menu');
    if (!trigger || !menu) return;
    trigger.addEventListener('click', function(e) { e.stopPropagation(); menu.classList.toggle('open'); });
    document.addEventListener('click', function() { menu.classList.remove('open'); });
    menu.addEventListener('click', function(e) { e.stopPropagation(); });
})();
</script>
</body>
</html>