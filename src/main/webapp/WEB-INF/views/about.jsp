<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Giới thiệu – BookStore</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
</head>
<body>

<!-- ===== NAVBAR ===== -->
<nav class="navbar">
  <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"></h1>
  <ul class="navbar__nav">
    <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
    <li><a href="${pageContext.request.contextPath}/books">Tất cả sách</a></li>
    <li><a href="${pageContext.request.contextPath}/about" class="nav-active">Giới thiệu</a></li>
    <li><a href="${pageContext.request.contextPath}/contact">Liên hệ</a></li>
    <li class="navbar__search-item">
      <form action="${pageContext.request.contextPath}/books" method="get">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="action" value="search">
        <input type="text" name="keyword" placeholder="Tìm sách..." autocomplete="off">
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
      <img src="${pageContext.request.contextPath}/images/bell.png" width="30" height="30" alt="bell"/>
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

<div class="search-page-header">
    <div class="search-page-header__inner">
                <div class="search-breadcrumb">
                    <a href="/OnlineBookStore/home">Trang chủ</a>
                    <span>›</span>
                    <span>Giới thiệu</span>
                </div>
                <h2 class="search-page-title">Về chúng tôi</h2>

    </div>
</div>

<!-- ===== STORY ===== -->
<section class="about-story">
  <div class="about-story__inner">
    <div class="about-story__text">
      <div class="section-badge">Lịch sử hình thành</div>
      <h2 class="about-story__title">Từ một góc nhỏ yêu sách</h2>
      <p>
        storyshop được thành lập năm 2026 bởi một nhóm bạn trẻ với chung niềm đam mê sách.
        Khởi đầu chỉ là một cửa hàng sách nhỏ tại TP.HCM, chúng tôi dần mở rộng sang nền tảng
        trực tuyến để phục vụ độc giả trên khắp Việt Nam.
      </p>
      <p>
        Chúng tôi tin rằng sách là cầu nối giữa con người với tri thức, với cảm xúc và với nhau.
        Mỗi cuốn sách tại BookStore được chọn lọc kỹ càng, đảm bảo chất lượng nội dung và
        hình thức in ấn tốt nhất.
      </p>
      <ul class="about-story__list">
        <li><i class="bi bi-check-circle-fill"></i> Sách chính hãng, có hóa đơn rõ ràng</li>
        <li><i class="bi bi-check-circle-fill"></i> Giao hàng toàn quốc trong 2–5 ngày</li>
        <li><i class="bi bi-check-circle-fill"></i> Đổi trả miễn phí trong 7 ngày</li>
        <li><i class="bi bi-check-circle-fill"></i> Hỗ trợ khách hàng 7 ngày/tuần</li>
      </ul>
    </div>
    <div class="about-story__image-wrap">
      <div class="about-story__img-card about-story__img-card--main">
        <i class="bi bi-book-half"></i>
        <span>Kho sách đa dạng</span>
      </div>
      <div class="about-story__img-card about-story__img-card--accent">
        <i class="bi bi-heart-fill"></i>
        <span>Đam mê từ trái tim</span>
      </div>
      <div class="about-story__floater">
        <i class="bi bi-star-fill"></i>
        <strong>4.9/5</strong>
        <span>Đánh giá</span>
      </div>
    </div>
  </div>
</section>

<!-- ===== VALUES ===== -->
<section class="about-values">
  <div class="about-values__inner">
    <div class="section-badge">Giá trị cốt lõi</div>
    <h2 class="about-values__title">Những điều chúng tôi tin tưởng</h2>
    <div class="values-grid">
      <div class="value-card">
        <div class="value-card__icon"><i class="bi bi-shield-check"></i></div>
        <h3>Chất lượng</h3>
        <p>Mỗi cuốn sách đều được kiểm tra kỹ càng trước khi đến tay bạn đọc.</p>
      </div>
      <div class="value-card">
        <div class="value-card__icon"><i class="bi bi-people"></i></div>
        <h3>Cộng đồng</h3>
        <p>Xây dựng cộng đồng yêu sách, nơi mọi người cùng chia sẻ và khám phá.</p>
      </div>
      <div class="value-card">
        <div class="value-card__icon"><i class="bi bi-lightning"></i></div>
        <h3>Tiện lợi</h3>
        <p>Mua sắm dễ dàng, thanh toán linh hoạt và giao hàng nhanh chóng.</p>
      </div>
      <div class="value-card">
        <div class="value-card__icon"><i class="bi bi-leaf"></i></div>
        <h3>Bền vững</h3>
        <p>Cam kết sử dụng vật liệu thân thiện môi trường trong đóng gói và vận chuyển.</p>
      </div>
    </div>
  </div>
</section>

<!-- ===== CTA ===== -->
<section class="about-cta">
  <div class="about-cta__inner">
    <h2>Bắt đầu hành trình đọc sách của bạn</h2>
    <p>Hàng nghìn đầu sách đang chờ đón bạn. Đăng ký ngay để nhận ưu đãi độc quyền.</p>
    <div class="about-cta__btns">
      <a href="${pageContext.request.contextPath}/books" class="about-cta__btn--primary">
        <i class="bi bi-book"></i> Khám phá ngay
      </a>
      <a href="${pageContext.request.contextPath}/contact" class="about-cta__btn--outline">
        <i class="bi bi-chat-dots"></i> Liên hệ chúng tôi
      </a>
    </div>
  </div>
</section>

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
<!-- ===== AUTH MODAL ===== -->
<div class="modal-overlay" id="authModal" onclick="handleOverlayClick(event)">
  <div class="modal-box">
    <div class="modal-left">
      <img src="${pageContext.request.contextPath}/images/login.png" width="100" height="100">
      <p>Khám phá hàng nghìn đầu sách hay. Đặt hàng nhanh, giao tận nơi.</p>
      <span class="promo-badge">Giảm 10% đơn đầu tiên</span>
    </div>
    <div class="modal-right">
      <button class="modal-close" onclick="closeModal()">✕</button>
      <div class="modal-tabs">
        <button class="modal-tab active" id="tab-login" onclick="switchTab('login')">ĐĂNG NHẬP</button>
        <button class="modal-tab" id="tab-register" onclick="switchTab('register')">ĐĂNG KÝ</button>
      </div>

      <!-- LOGIN -->
      <div class="tab-content active" id="content-login">
        <form action="${pageContext.request.contextPath}/user" method="post">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden" name="action" value="login">
          <div class="modal-form-group">
            <label for="loginUsername">Tên đăng nhập</label>
            <input type="text" id="loginUsername" name="username" placeholder="Nhập tên đăng nhập" required>
          </div>
          <div class="modal-form-group">
            <label for="loginPassword">Mật khẩu</label>
            <input type="password" id="loginPassword" name="password" placeholder="Nhập mật khẩu" required>
          </div>
          <button type="submit" class="btn-modal-submit">Đăng nhập</button>
        </form>
        <p class="modal-switch-text">
          Chưa có tài khoản? <a onclick="switchTab('register')">Đăng ký ngay</a>
        </p>
        <p class="modal-switch-text" style="margin-top:6px;">
            <a onclick="switchTab('forgot')">Quên mật khẩu?</a>
        </p>
      </div>

      <!-- REGISTER -->
      <div class="tab-content" id="content-register">
        <form action="${pageContext.request.contextPath}/user" method="post">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden" name="action" value="register">
          <div class="modal-form-group">
            <label for="regUsername">Tên đăng nhập *</label>
            <input type="text" id="regUsername" name="username" placeholder="Tối thiểu 3 ký tự" required>
          </div>
          <div class="modal-form-group">
            <label for="regEmail">Email *</label>
            <input type="email" id="regEmail" name="email" placeholder="example@email.com" required>
          </div>
          <div class="modal-form-group">
            <label for="regPhone">Số điện thoại</label>
            <input type="tel" id="regPhone" name="phoneNumber" placeholder="0901234567">
          </div>
          <div class="modal-form-group">
            <label for="regPassword">Mật khẩu *</label>
            <input type="password" id="regPassword" name="password" placeholder="Tối thiểu 6 ký tự" required>
          </div>
          <div class="modal-form-group">
            <label for="regConfirm">Xác nhận mật khẩu *</label>
            <input type="password" id="regConfirm" name="confirmPassword" placeholder="Nhập lại mật khẩu" required>
          </div>
          <button type="submit" class="btn-modal-submit">Đăng ký</button>
        </form>
        <p class="modal-switch-text">
          Đã có tài khoản? <a onclick="switchTab('login')">Đăng nhập</a>
        </p>
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
<script>
    const contextPath = '${pageContext.request.contextPath}';
            function openModal(tab) {
                var modal = document.getElementById('authModal');
                if (modal) {
                    modal.style.display = 'flex';
                    modal.classList.add('active');
                    switchTab(tab || 'login');
                    document.body.style.overflow = 'hidden';
                } else {
                    console.error('Modal not found');
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
                if (e.target === document.getElementById('authModal')) {
                    closeModal();
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
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') {
                    closeModal();
                }
            });

            // Kiểm tra modal có tồn tại không
            window.addEventListener('DOMContentLoaded', function() {
                console.log('DOM loaded, checking modal...');
                var modal = document.getElementById('authModal');
                if (modal) {
                    console.log('Modal found');
                    modal.style.display = 'none';
                } else {
                    console.error('Modal NOT found!');
                }

                // Xử lý lỗi từ server
                var hasLoginError = ${not empty loginError ? 'true' : 'false'};
                var hasRegisterError = ${not empty registerError or not empty usernameError or not empty emailError or not empty passwordError or not empty confirmPasswordError ? 'true' : 'false'};
                var hasSuccess = ${not empty successMessage ? 'true' : 'false'};

                if (hasLoginError || hasSuccess) {
                    openModal('login');
                } else if (hasRegisterError) {
                    openModal('register');
                }
                var showLogin = ${not empty param.showLogin ? 'true' : 'false'};
                if (hasLoginError || hasSuccess || showLogin) {
                    openModal('login');
                } else if (hasRegisterError) {
                    openModal('register');
                }
            });
        </script>
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
