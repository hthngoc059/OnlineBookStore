<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <title>Tài khoản – Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
</head>
<body>

<nav class="navbar">
            <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"></h1>
            <!--NAV LINKS -->
            <ul class="navbar__nav">
                <li><a href="${pageContext.request.contextPath}/">Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/">Tất cả sách</a></li>
                <li><a href="${pageContext.request.contextPath}/">Giới thiệu</a></li>
                <li><a href="${pageContext.request.contextPath}/">Liên hệ</a></li>
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
                        <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
                            <img src="${pageContext.request.contextPath}/images/bell.png" width="30" height="30" alt="cart"/>
                          
                        </a>
                        <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
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

<div class="profile-container">

    <%-- SIDEBAR --%>
    <div class="profile-sidebar">
        <div class="profile-avatar">
            <i class="bi bi-person-circle"></i>
            <div>
                <div class="profile-name">${sessionScope.currentUser.username}</div>
                <a href="#" class="profile-edit-link">
                    <i class="bi bi-pencil"></i> Sửa hồ sơ
                </a>
            </div>
        </div>

        <nav class="profile-nav">
            <div class="profile-nav-group">
                <div class="profile-nav-label">
                    <i class="bi bi-person"></i> Tài khoản của tôi
                </div>
                <a href="${pageContext.request.contextPath}/profile?action=info"
                   class="profile-nav-item ${param.action == null || param.action == 'info' ? 'active' : ''}">
                    Hồ sơ
                </a>
                <a href="${pageContext.request.contextPath}/profile?action=password"
                   class="profile-nav-item ${param.action == 'password' ? 'active' : ''}">
                    Đổi mật khẩu
                </a>
                <a href="${pageContext.request.contextPath}/profile/addresses"
                   class="profile-nav-item">
                    Địa chỉ
                </a>
            </div>
            <a href="${pageContext.request.contextPath}/orders" class="profile-nav-group-link">
                <i class="bi bi-bag-check"></i> Đơn mua
            </a>
        </nav>
    </div>

    <%-- NỘI DUNG CHÍNH --%>
    <div class="profile-content">
        <h2 class="profile-title">Hồ sơ của tôi</h2>
        <p style="color:#888; font-size:0.85rem; margin-bottom:28px;">
            Quản lý thông tin tài khoản
        </p>

        <div class="profile-form-row">
            <label>Tên đăng nhập</label>
            <span>${sessionScope.currentUser.username}</span>
        </div>
        <div class="profile-form-row">
            <label>Email</label>
            <span>${sessionScope.currentUser.email}</span>
        </div>
        <div class="profile-form-row">
            <label>Số điện thoại</label>
            <span>${not empty sessionScope.currentUser.phone 
                      ? sessionScope.currentUser.phone 
                      : 'Chưa cập nhật'}</span>
        </div>
    </div>

</div>

<footer>
    <p>&copy; 2024 Nhà Sách Online. All rights reserved.</p>
</footer>
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