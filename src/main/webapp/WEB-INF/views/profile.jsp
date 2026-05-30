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
                <a href="${pageContext.request.contextPath}/profile?action=info"
                   class="profile-nav-item ${param.action == null || param.action == 'info' ? 'active' : ''}">
                    Hồ sơ
                </a>
                <a href="${pageContext.request.contextPath}/profile?action=password"
                   class="profile-nav-item ${param.action == 'password' ? 'active' : ''}">
                    Đổi mật khẩu
                </a>
                <a href="${pageContext.request.contextPath}/profile?action=addresses"
                    class="profile-nav-item ${param.action == 'addresses' ? 'active' : ''}">
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

        <c:choose>
            <%-- ── TAB: ĐỔI MẬT KHẨU ── --%>
            <c:when test="${param.action == 'password'}">
                <h2 class="profile-title">Đổi Mật Khẩu</h2>
                <p style="color:#888; font-size:0.85rem; margin-bottom:28px;
                          border-bottom:1px solid #f0f0f0; padding-bottom:16px;">
                    Để bảo mật tài khoản, vui lòng không chia sẻ mật khẩu
                </p>

                <c:if test="${not empty successMsg}">
                    <div class="profile-alert profile-alert--ok">✅ ${successMsg}</div>
                </c:if>
                <c:if test="${not empty errorMsg}">
                    <div class="profile-alert profile-alert--err">❌ ${errorMsg}</div>
                </c:if>

                <form method="post"
                      action="${pageContext.request.contextPath}/profile?action=password">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <%-- Mật khẩu hiện tại --%>
                    <div class="profile-form-row">
                        <label>Mật khẩu hiện tại</label>
                        <div class="profile-pw-wrap">
                            <input type="password" name="currentPassword"
                                   id="currentPassword"
                                   placeholder="Nhập mật khẩu hiện tại"
                                   class="profile-input" required/>
                            <span class="pw-toggle" onclick="togglePw('currentPassword', this)">
                                <i class="bi bi-eye-slash"></i>
                            </span>
                        </div>
                    </div>

                    <%-- Mật khẩu mới --%>
                    <div class="profile-form-row">
                        <label>Mật khẩu mới</label>
                        <div class="profile-pw-wrap">
                            <input type="password" name="newPassword"
                                   id="newPassword"
                                   placeholder="Tối thiểu 6 ký tự"
                                   class="profile-input" required/>
                            <span class="pw-toggle" onclick="togglePw('newPassword', this)">
                                <i class="bi bi-eye-slash"></i>
                            </span>
                        </div>
                    </div>

                    <%-- Xác nhận mật khẩu --%>
                    <div class="profile-form-row">
                        <label>Xác nhận mật khẩu</label>
                        <div class="profile-pw-wrap">
                            <input type="password" name="confirmPassword"
                                   id="confirmPassword"
                                   placeholder="Nhập lại mật khẩu mới"
                                   class="profile-input" required/>
                            <span class="pw-toggle" onclick="togglePw('confirmPassword', this)">
                                <i class="bi bi-eye-slash"></i>
                            </span>
                        </div>
                    </div>

                    <div class="profile-form-row" style="border:none; padding-top:8px;">
                        <label></label>
                        <button type="submit" class="btn-profile-save">Xác Nhận</button>
                    </div>
                </form>

                <script>
                function togglePw(id, btn) {
                    const input = document.getElementById(id);
                    const icon  = btn.querySelector('i');
                    if (input.type === 'password') {
                        input.type = 'text';
                        icon.className = 'bi bi-eye';
                    } else {
                        input.type = 'password';
                        icon.className = 'bi bi-eye-slash';
                    }
                }
                </script>
            </c:when>
            <%-- ── TAB: ĐỊA CHỈ ── --%>
<c:when test="${param.action == 'addresses'}">
    <h2 class="profile-title">Địa chỉ của tôi</h2>
    <p style="color:#888; font-size:0.85rem; margin-bottom:0;
              border-bottom:1px solid #f0f0f0; padding-bottom:16px; margin-bottom:0;">
        Quản lý địa chỉ giao hàng của bạn
    </p>

    <c:if test="${not empty successMsg}">
        <div class="profile-alert profile-alert--ok" style="margin-top:20px;">✅ ${successMsg}</div>
    </c:if>
    <c:if test="${not empty errorMsg}">
        <div class="profile-alert profile-alert--err" style="margin-top:20px;">❌ ${errorMsg}</div>
    </c:if>

    <%-- Header: tiêu đề + nút thêm --%>
    <div class="addr-mgmt-header" style="margin-top:20px;">
        <h2 style="font-size:0.9rem; color:#888; font-weight:600; margin:0;">
            ${empty addresses ? '0' : addresses.size()} địa chỉ đã lưu
        </h2>
        <a href="${pageContext.request.contextPath}/address" class="btn-add-address">
            <i class="bi bi-plus-lg"></i> Thêm địa chỉ mới
        </a>
    </div>

    <%-- Danh sách địa chỉ --%>
    <c:choose>
        <c:when test="${empty addresses}">
            <div class="addr-empty">
                <i class="bi bi-geo-alt"></i>
                <p>Bạn chưa có địa chỉ nào.</p>
            </div>
        </c:when>
        <c:otherwise>
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
                            <c:if test="${addr.isDefault}">
                                <div class="addr-tags">
                                    <span class="addr-tag">
                                        <i class="bi bi-star-fill" style="font-size:0.65rem;"></i>
                                        Mặc định
                                    </span>
                                    <span class="addr-tag addr-tag--pickup">Địa chỉ lấy hàng</span>
                                </div>
                            </c:if>
                        </div>

                        <div class="addr-actions">
                            <div class="addr-action-links">
                                <a href="${pageContext.request.contextPath}/address?action=edit&id=${addr.addressId}"
                                   class="btn-addr-edit">
                                    <i class="bi bi-pencil"></i> Sửa
                                </a>
                                <c:if test="${!addr.isDefault}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/profile?action=deleteAddress"
                                          style="display:inline;"
                                          onsubmit="return confirm('Bạn có chắc muốn xóa địa chỉ này?');">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <input type="hidden" name="action" value="deleteAddress"/>
                                        <input type="hidden" name="addressId" value="${addr.addressId}"/>
                                        <button type="submit" class="btn-addr-delete">
                                            <i class="bi bi-trash"></i> Xóa
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                            <c:if test="${!addr.isDefault}">
                                <form method="post"
                                      action="${pageContext.request.contextPath}/profile?action=setDefault">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <input type="hidden" name="action" value="setDefault"/>
                                    <input type="hidden" name="addressId" value="${addr.addressId}"/>
                                    <button type="submit" class="btn-set-default">
                                        <i class="bi bi-star"></i> Đặt mặc định
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</c:when>
            <%-- ── TAB: HỒ SƠ (mặc định) ── --%>
            <c:otherwise>
                <h2 class="profile-title">Hồ Sơ Của Tôi</h2>
                <p style="color:#888; font-size:0.85rem; margin-bottom:28px;
                          border-bottom:1px solid #f0f0f0; padding-bottom:16px;">
                    Quản lý thông tin hồ sơ để bảo mật tài khoản
                </p>

                <c:if test="${not empty successMsg}">
                    <div class="profile-alert profile-alert--ok">✅ ${successMsg}</div>
                </c:if>
                <c:if test="${not empty errorMsg}">
                    <div class="profile-alert profile-alert--err">❌ ${errorMsg}</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/profile">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="profile-form-row">
                        <label>Tên đăng nhập</label>
                        <div>
                            <input type="text" value="${sessionScope.currentUser.username}"
                                   class="profile-input profile-input--readonly" readonly/>
                            <div class="profile-hint">Tên đăng nhập không thể thay đổi.</div>
                        </div>
                    </div>

                    <div class="profile-form-row">
                        <label>Email</label>
                        <input type="text" value="${sessionScope.currentUser.email}"
                               class="profile-input profile-input--readonly" readonly/>
                    </div>

                    <div class="profile-form-row">
                        <label>Số điện thoại</label>
                        <input type="tel" name="phoneNumber"
                               value="${sessionScope.currentUser.phoneNumber}"
                               placeholder="Nhập số điện thoại"
                               class="profile-input"/>
                    </div>

                    <div class="profile-form-row" style="border:none; padding-top:8px;">
                        <label></label>
                        <button type="submit" class="btn-profile-save">Lưu</button>
                    </div>
                </form>
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