<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${not empty keyword}">Tìm kiếm: ${keyword}</c:when>
            <c:when test="${not empty selectedGenre}">Thể loại: ${selectedGenre}</c:when>
            <c:otherwise>Tất cả sách</c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
</head>
<body>

<!-- ═══════════ NAVBAR (đồng bộ với home.jsp) ═══════════ -->
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
<div class="search-page-header">
    <div class="search-page-header__inner">
        <c:choose>
            <c:when test="${not empty keyword}">
                <div class="search-breadcrumb">
                    <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
                    <span>›</span>
                    <span>Tìm kiếm</span>
                </div>
                <h2 class="search-page-title">
                    Kết quả tìm kiếm:
                    <em>"<c:out value="${keyword}"/>"</em>
                </h2>
                <p class="search-page-count">
                    <c:choose>
                        <c:when test="${empty books}">Không tìm thấy sách phù hợp</c:when>
                        <c:otherwise>Tìm thấy ${books.size()} kết quả</c:otherwise>
                    </c:choose>
                </p>
            </c:when>
            <c:when test="${not empty selectedGenre}">
                <div class="search-breadcrumb">
                    <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
                    <span>›</span>
                    <span>Thể loại</span>
                </div>
                <h2 class="search-page-title">Thể loại: <em>${selectedGenre}</em></h2>
            </c:when>
            <c:otherwise>
                <div class="search-breadcrumb">
                    <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
                    <span>›</span>
                    <span>Tất cả sách</span>
                </div>
                <h2 class="search-page-title">Tất cả sách</h2>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- ═══════════ MAIN CONTENT ═══════════ -->
<main class="books-container">
    <c:choose>
        <c:when test="${empty books}">
            <div class="search-empty">
                <div class="search-empty__icon">📭</div>
                <h3>Không tìm thấy sách phù hợp</h3>
                <p>Hãy thử tìm với từ khóa khác hoặc duyệt toàn bộ danh mục.</p>
                <a href="${pageContext.request.contextPath}/books" class="btn-view-more">
                    Xem tất cả sách
                </a>
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
                                 alt="${book.title}"
                                 onerror="this.src='${pageContext.request.contextPath}/images/default-book.jpg'">
                        </a>
                        <div class="book-info">
                            <h3 class="book-title">${book.title}</h3>
                            <p class="book-author">${book.author}</p>
                            <p class="book-price">${book.priceFormatted} đ</p>

                            <c:if test="${book.stockQuantity == 0}">
                                <span class="book-out-of-stock">Hết hàng</span>
                            </c:if>

                            <div class="book-actions">
                                <a href="${pageContext.request.contextPath}/books?action=detail&id=${book.bookId}"
                                   class="btn-detail">Chi tiết</a>

                                <c:choose>
                                    <c:when test="${book.stockQuantity > 0}">
                                        <a href="${pageContext.request.contextPath}/cart?action=add&id=${book.bookId}"
                                           class="btn-add-cart">Thêm giỏ</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="btn-add-cart btn-add-cart--disabled">Hết hàng</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
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

<!-- ═══════════ AUTH MODAL (đồng bộ với home.jsp) ═══════════ -->
<div class="modal-overlay" id="authModal" onclick="handleOverlayClick(event)">
    <div class="modal-box">
        <div class="modal-left">
            <h3>Nhà Sách Online</h3>
            <p>Khám phá hàng nghìn đầu sách hay.</p>
            <span class="promo-badge">Giảm 10% đơn đầu tiên</span>
        </div>
        <div class="modal-right">
            <button class="modal-close" onclick="closeModal()">✕</button>
            <div class="modal-tabs">
                <button class="modal-tab active" id="tab-login" onclick="switchTab('login')">ĐĂNG NHẬP</button>
                <button class="modal-tab" id="tab-register" onclick="switchTab('register')">ĐĂNG KÝ</button>
            </div>
            <div class="tab-content active" id="content-login">
                <form action="${pageContext.request.contextPath}/user" method="post">
                    <input type="hidden" name="action" value="login">
                    <div class="modal-form-group">
                        <label>Tên đăng nhập</label>
                        <input type="text" name="username" placeholder="Nhập tên đăng nhập" required>
                    </div>
                    <div class="modal-form-group">
                        <label>Mật khẩu</label>
                        <input type="password" name="password" placeholder="Nhập mật khẩu" required>
                    </div>
                    <button type="submit" class="btn-modal-submit">Đăng nhập</button>
                </form>
                <p class="modal-switch-text">Chưa có tài khoản?
                    <a onclick="switchTab('register')">Đăng ký ngay</a></p>
                <p class="modal-switch-text" style="margin-top:6px;">
                    <a onclick="switchTab('forgot')">Quên mật khẩu?</a>
                </p>
            </div>
            <div class="tab-content" id="content-register">
                <form action="${pageContext.request.contextPath}/user" method="post">
                    <input type="hidden" name="action" value="register">
                    <div class="modal-form-group">
                        <label>Tên đăng nhập *</label>
                        <input type="text" name="username" placeholder="Tối thiểu 3 ký tự" required>
                    </div>
                    <div class="modal-form-group">
                        <label>Email *</label>
                        <input type="email" name="email" placeholder="example@email.com" required>
                    </div>
                    <div class="modal-form-group">
                        <label>Mật khẩu *</label>
                        <input type="password" name="password" placeholder="Tối thiểu 6 ký tự" required>
                    </div>
                    <div class="modal-form-group">
                        <label>Xác nhận mật khẩu *</label>
                        <input type="password" name="confirmPassword" placeholder="Nhập lại mật khẩu" required>
                    </div>
                    <button type="submit" class="btn-modal-submit">Đăng ký</button>
                </form>
                <p class="modal-switch-text">Đã có tài khoản?
                    <a onclick="switchTab('login')">Đăng nhập</a></p>
            </div>
                    <!-- ── FORGOT PASSWORD FORM ── -->
                    <div class="tab-content" id="content-forgot">
                        <h3 style="margin-bottom:12px; font-size:1rem; font-weight:600;">Lấy lại mật khẩu</h3>
                        <p style="font-size:0.85rem; color:#666; margin-bottom:14px;">
                            Nhập đúng tên đăng nhập và email. Mật khẩu sẽ được reset về <strong>123456</strong>.
                        </p>
                        <div id="forgot-result"></div>
                        <div class="modal-form-group">
                            <label for="forgotUsername">Tên đăng nhập</label>
                            <input type="text" id="forgotUsername" placeholder="Nhập tên đăng nhập">
                        </div>
                        <div class="modal-form-group">
                            <label for="forgotEmail">Email đã đăng ký</label>
                            <input type="email" id="forgotEmail" placeholder="example@email.com">
                        </div>
                        <button type="button" class="btn-modal-submit" onclick="handleForgotPassword()">
                            Xác nhận
                        </button>
                        <p class="modal-switch-text" style="margin-top:12px;">
                            <a onclick="switchTab('login')">← Quay lại đăng nhập</a>
                        </p>
                    </div>
        </div>
    </div>
</div>

<style>
/* ═══ SEARCH PAGE SPECIFIC STYLES ═══ */

/* Page Header */
.search-page-header {
    background: linear-gradient(135deg, #f9fbf6 0%, #eef4e8 100%);
    border-bottom: 2px solid #e0ecd6;
    padding: 32px 0 28px;
}

.search-page-header__inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
}

.search-breadcrumb {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 0.8rem;
    color: #888;
    margin-bottom: 10px;
}

.search-breadcrumb a {
    color: #405a28;
    font-weight: 600;
}

.search-breadcrumb a:hover { text-decoration: underline; }

.search-breadcrumb span { color: #bbb; }

.search-page-title {
    font-size: 1.6rem;
    font-weight: 700;
    color: #222;
    margin: 0 0 6px;
    line-height: 1.3;
}

.search-page-title em {
    font-style: normal;
    color: #405a28;
    border-bottom: 3px solid #405a28;
    padding-bottom: 1px;
}

.search-page-count {
    font-size: 0.85rem;
    color: #888;
    margin: 0;
}

/* Empty state */
.search-empty {
    text-align: center;
    padding: 80px 20px;
    color: #aaa;
}

.search-empty__icon {
    font-size: 4rem;
    margin-bottom: 16px;
    display: block;
}

.search-empty h3 {
    font-size: 1.3rem;
    font-weight: 700;
    color: #555;
    margin-bottom: 8px;
}

.search-empty p {
    font-size: 0.9rem;
    color: #999;
    margin-bottom: 24px;
}

/* Out of stock badge */
.book-out-of-stock {
    display: inline-block;
    color: #cc0000;
    font-size: 0.75rem;
    font-weight: 700;
    background: #fff0f0;
    border: 1px solid #ffcccc;
    padding: 2px 8px;
    border-radius: 4px;
    margin-bottom: 8px;
}

/* Disabled cart button */
.btn-add-cart--disabled {
    opacity: 0.4;
    cursor: not-allowed;
    pointer-events: none;
}

/* Active nav link */
.navbar__nav .active-nav {
    color: #405a28 !important;
    font-weight: 700;
    border-bottom: 2px solid #405a28;
    padding-bottom: 2px;
}

/* User dropdown text color fix for white navbar */
.user-dropdown__trigger span {
    color: #333 !important;
}
</style>

<script>
    const contextPath = '${pageContext.request.contextPath}';
function openModal(tab) {
    var modal = document.getElementById('authModal');
    if (modal) {
        modal.style.display = 'flex';
        modal.classList.add('active');
        switchTab(tab || 'login');
        document.body.style.overflow = 'hidden';
    }
}

function closeModal() {
    var modal = document.getElementById('authModal');
    if (modal) {
        modal.style.display = 'none';
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }
}

function handleOverlayClick(e) {
    if (e.target === document.getElementById('authModal')) closeModal();
}
async function handleForgotPassword() {
                var username  = document.getElementById('forgotUsername').value.trim();
                var email     = document.getElementById('forgotEmail').value.trim();
                var resultDiv = document.getElementById('forgot-result');

                if (!username || !email) {
                    resultDiv.innerHTML = '<div class="modal-alert modal-alert-error">Vui lòng nhập đầy đủ thông tin.</div>';
                    return;
                }

                resultDiv.innerHTML = '<div class="modal-alert">Đang xử lý...</div>';

                try {
                    var resp = await fetch(contextPath + '/user?action=forgotPassword'
                        + '&username=' + encodeURIComponent(username)
                        + '&email='    + encodeURIComponent(email));
                    var data = await resp.json();
                    if (data.success) {
                        resultDiv.innerHTML =
                            '<div class="modal-alert modal-alert-success">' +
                            'Mật khẩu đã được reset về <strong>123456</strong>. ' +
                            'Vui lòng đăng nhập và đổi mật khẩu ngay.' +
                            '</div>';
                        document.getElementById('forgotUsername').value = '';
                        document.getElementById('forgotEmail').value = '';
                    } else {
                        resultDiv.innerHTML = '<div class="modal-alert modal-alert-error">' + data.message + '</div>';
                    }
                } catch (e) {
                    resultDiv.innerHTML = '<div class="modal-alert modal-alert-error">Có lỗi xảy ra, vui lòng thử lại.</div>';
                }
            }
function switchTab(tab) {
    ['login', 'register', 'forgot'].forEach(function(t) {
        var content = document.getElementById('content-' + t);
        if (content) content.classList.remove('active');
    });
    ['login', 'register'].forEach(function(t) {
        var tabBtn = document.getElementById('tab-' + t);
        if (tabBtn) tabBtn.classList.remove('active');
    });

    if (tab === 'forgot') {
        var forgotContent = document.getElementById('content-forgot');
        if (forgotContent) forgotContent.classList.add('active');
        document.getElementById('forgotUsername').value = '';
        document.getElementById('forgotEmail').value = '';
        document.getElementById('forgot-result').innerHTML = '';
    } else {
        var tabBtn = document.getElementById('tab-' + tab);
        if (tabBtn) tabBtn.classList.add('active');
        var content = document.getElementById('content-' + tab);
        if (content) content.classList.add('active');
    }
}

document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeModal();
});

// User dropdown
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
