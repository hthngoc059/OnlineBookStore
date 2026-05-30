<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Liên hệ – BookStore</title>
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
    <li><a href="${pageContext.request.contextPath}/about">Giới thiệu</a></li>
    <li><a href="${pageContext.request.contextPath}/contact" class="nav-active">Liên hệ</a></li>
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

<!-- ===== PAGE HEADER ===== -->
<div class="contact-header">
  <div class="contact-header__inner">
    <span class="section-badge section-badge--light">Hỗ trợ khách hàng</span>
    <h1 class="contact-header__title">Liên hệ với chúng tôi</h1>
    <p class="contact-header__sub">
      Chúng tôi luôn sẵn sàng lắng nghe và hỗ trợ bạn. Hãy để lại tin nhắn — chúng tôi sẽ phản hồi trong vòng 24 giờ.
    </p>
  </div>
</div>

<!-- ===== MAIN CONTENT ===== -->
<div class="contact-main">

  <!-- ── Info cards ── -->
  <div class="contact-info">
    <div class="contact-info-card">
      <div class="contact-info-card__icon">
        <i class="bi bi-geo-alt-fill"></i>
      </div>
      <div class="contact-info-card__body">
        <h3>Địa chỉ</h3>
        <p>123 Đường Sách, Quận 1<br>TP. Hồ Chí Minh, Việt Nam</p>
      </div>
    </div>

    <div class="contact-info-card">
      <div class="contact-info-card__icon">
        <i class="bi bi-telephone-fill"></i>
      </div>
      <div class="contact-info-card__body">
        <h3>Điện thoại</h3>
        <p><a href="tel:+84901234567">+84 90 123 4567</a></p>
        <p style="font-size:0.78rem; color:#aaa; margin-top:4px;">Thứ Hai – Thứ Bảy, 8:00–17:00</p>
      </div>
    </div>

    <div class="contact-info-card">
      <div class="contact-info-card__icon">
        <i class="bi bi-envelope-fill"></i>
      </div>
      <div class="contact-info-card__body">
        <h3>Email</h3>
        <p><a href="mailto:hello@bookstore.vn">hello@bookstore.vn</a></p>
        <p style="font-size:0.78rem; color:#aaa; margin-top:4px;">Phản hồi trong vòng 24 giờ</p>
      </div>
    </div>

    <div class="contact-info-card">
      <div class="contact-info-card__icon">
        <i class="bi bi-clock-fill"></i>
      </div>
      <div class="contact-info-card__body">
        <h3>Giờ làm việc</h3>
        <p>T2 – T6: 8:00 – 17:00<br>T7: 8:00 – 12:00</p>
      </div>
    </div>

    <!-- Social links -->
    <div class="contact-social">
      <span class="contact-social__label">Theo dõi chúng tôi</span>
      <div class="contact-social__links">
        <a href="#" class="contact-social__btn" title="Facebook">
          <i class="bi bi-facebook"></i>
        </a>
        <a href="#" class="contact-social__btn" title="Instagram">
          <i class="bi bi-instagram"></i>
        </a>
        <a href="#" class="contact-social__btn" title="Zalo">
          <i class="bi bi-chat-dots-fill"></i>
        </a>
        <a href="#" class="contact-social__btn" title="YouTube">
          <i class="bi bi-youtube"></i>
        </a>
      </div>
    </div>
  </div>

  <!-- ── Contact form ── -->
  <div class="contact-form-wrap">
    <div class="contact-form-card">
      <h2 class="contact-form-card__title">
        <i class="bi bi-send-fill" style="color:#405a28;"></i>
        Gửi tin nhắn
      </h2>

      <!-- Alert (hiện khi submit) -->
      <div id="contactAlert" class="contact-alert" style="display:none;"></div>

      <form id="contactForm" novalidate>
        <div class="contact-form-row">
          <div class="contact-fg">
            <label for="cf_name">Họ và tên <span class="required">*</span></label>
            <input type="text" id="cf_name" placeholder="Nguyễn Văn A" autocomplete="name"/>
            <span class="cf-error" id="err_name"></span>
          </div>
          <div class="contact-fg">
            <label for="cf_email">Email <span class="required">*</span></label>
            <input type="email" id="cf_email" placeholder="example@email.com" autocomplete="email"/>
            <span class="cf-error" id="err_email"></span>
          </div>
        </div>

        <div class="contact-fg">
          <label for="cf_phone">Số điện thoại</label>
          <input type="tel" id="cf_phone" placeholder="0901 234 567" autocomplete="tel"/>
        </div>

        <div class="contact-fg">
          <label for="cf_subject">Chủ đề <span class="required">*</span></label>
          <div class="contact-select-wrap">
            <select id="cf_subject">
              <option value="">-- Chọn chủ đề --</option>
              <option value="order">Vấn đề về đơn hàng</option>
              <option value="product">Hỏi về sản phẩm</option>
              <option value="return">Đổi trả & hoàn tiền</option>
              <option value="partner">Hợp tác xuất bản</option>
              <option value="other">Khác</option>
            </select>
            <i class="bi bi-chevron-down contact-select__arrow"></i>
          </div>
          <span class="cf-error" id="err_subject"></span>
        </div>

        <div class="contact-fg">
          <label for="cf_message">Nội dung <span class="required">*</span></label>
          <textarea id="cf_message" rows="5" placeholder="Hãy mô tả vấn đề hoặc câu hỏi của bạn..."></textarea>
          <span class="cf-error" id="err_message"></span>
        </div>

        <button type="submit" class="contact-submit-btn" id="contactSubmitBtn">
          <i class="bi bi-send"></i>
          <span>Gửi tin nhắn</span>
        </button>
      </form>
    </div>
  </div>

</div>

<!-- ===== MAP ===== -->
<div class="contact-map-section">
  <div class="contact-map-inner">
    <h2 class="contact-map-title">
      <i class="bi bi-map"></i> Tìm chúng tôi trên bản đồ
    </h2>
    <div class="contact-map-frame">
      <iframe
        src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.4672840552076!2d106.69516781480057!3d10.776888792319977!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f3a9d8d1bb3%3A0xd2a8f5e4c6b5e3d!2zxJAuIFPDoWNoLCBRdeG6rW4gMSwgVGjDoG5oIHBo4buRIEjhu5MgQ2jDrSBNaW5o!5e0!3m2!1svi!2svn!4v1622000000000!5m2!1svi!2svn"
        width="100%" height="100%"
        style="border:0;"
        allowfullscreen=""
        loading="lazy"
        referrerpolicy="no-referrer-when-downgrade"
        title="Bản đồ BookStore">
      </iframe>
    </div>
  </div>
</div>

<!-- ===== FAQ ===== -->
<div class="contact-faq">
  <div class="contact-faq__inner">
    <div class="section-badge">FAQ</div>
    <h2 class="contact-faq__title">Câu hỏi thường gặp</h2>
    <div class="faq-list">

      <div class="faq-item">
        <button class="faq-question" onclick="toggleFaq(this)">
          <span>Thời gian giao hàng là bao lâu?</span>
          <i class="bi bi-plus-lg faq-icon"></i>
        </button>
        <div class="faq-answer">
          <p>Thông thường 2–5 ngày làm việc tùy khu vực. Nội thành TP.HCM và Hà Nội có thể giao trong ngày hoặc hôm sau.</p>
        </div>
      </div>

      <div class="faq-item">
        <button class="faq-question" onclick="toggleFaq(this)">
          <span>Tôi có thể đổi trả sách không?</span>
          <i class="bi bi-plus-lg faq-icon"></i>
        </button>
        <div class="faq-answer">
          <p>Có. Chúng tôi chấp nhận đổi trả trong vòng 7 ngày kể từ ngày nhận hàng nếu sách bị lỗi in, rách bìa hoặc không đúng sản phẩm. Vui lòng giữ nguyên bao bì.</p>
        </div>
      </div>

      <div class="faq-item">
        <button class="faq-question" onclick="toggleFaq(this)">
          <span>Có những phương thức thanh toán nào?</span>
          <i class="bi bi-plus-lg faq-icon"></i>
        </button>
        <div class="faq-answer">
          <p>Chúng tôi hỗ trợ: Thanh toán khi nhận hàng (COD), chuyển khoản ngân hàng, ví điện tử (MoMo, ZaloPay, VNPay) và thẻ tín dụng/ghi nợ quốc tế.</p>
        </div>
      </div>

      <div class="faq-item">
        <button class="faq-question" onclick="toggleFaq(this)">
          <span>Sách có đảm bảo chính hãng không?</span>
          <i class="bi bi-plus-lg faq-icon"></i>
        </button>
        <div class="faq-answer">
          <p>Tất cả sách tại BookStore đều có nguồn gốc rõ ràng từ các NXB uy tín tại Việt Nam. Chúng tôi cam kết 100% sách chính hãng, không bán hàng nhái hay photocopy.</p>
        </div>
      </div>

      <div class="faq-item">
        <button class="faq-question" onclick="toggleFaq(this)">
          <span>Làm thế nào để trở thành đối tác/cộng tác viên?</span>
          <i class="bi bi-plus-lg faq-icon"></i>
        </button>
        <div class="faq-answer">
          <p>Bạn có thể gửi email đến <a href="mailto:partner@bookstore.vn">partner@bookstore.vn</a> hoặc điền vào form liên hệ phía trên với chủ đề "Hợp tác xuất bản". Đội ngũ của chúng tôi sẽ phản hồi trong 2–3 ngày làm việc.</p>
        </div>
      </div>

    </div>
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

    </div>
  </div>
</div>
<script>
  /* ── FAQ toggle ── */
  function toggleFaq(btn) {
    const item = btn.closest('.faq-item');
    const isOpen = item.classList.contains('open');
    document.querySelectorAll('.faq-item.open').forEach(i => i.classList.remove('open'));
    if (!isOpen) item.classList.add('open');
  }

  /* ── Form validation & submit ── */
  document.getElementById('contactForm').addEventListener('submit', function (e) {
    e.preventDefault();
    let valid = true;

    const name    = document.getElementById('cf_name');
    const email   = document.getElementById('cf_email');
    const subject = document.getElementById('cf_subject');
    const message = document.getElementById('cf_message');

    // Reset
    ['err_name','err_email','err_subject','err_message'].forEach(id => {
      document.getElementById(id).textContent = '';
    });
    [name, email, subject, message].forEach(el => el.classList.remove('input-error'));

    if (!name.value.trim()) {
      document.getElementById('err_name').textContent = 'Vui lòng nhập họ và tên.';
      name.classList.add('input-error'); valid = false;
    }

    const emailRx = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email.value.trim() || !emailRx.test(email.value)) {
      document.getElementById('err_email').textContent = 'Vui lòng nhập email hợp lệ.';
      email.classList.add('input-error'); valid = false;
    }

    if (!subject.value) {
      document.getElementById('err_subject').textContent = 'Vui lòng chọn chủ đề.';
      subject.classList.add('input-error'); valid = false;
    }

    if (message.value.trim().length < 10) {
      document.getElementById('err_message').textContent = 'Nội dung cần ít nhất 10 ký tự.';
      message.classList.add('input-error'); valid = false;
    }

    if (!valid) return;

    /* Simulate submit */
    const btn = document.getElementById('contactSubmitBtn');
    btn.disabled = true;
    btn.innerHTML = '<i class="bi bi-hourglass-split"></i> <span>Đang gửi...</span>';

    setTimeout(() => {
      const alert = document.getElementById('contactAlert');
      alert.className = 'contact-alert contact-alert--success';
      alert.innerHTML = '<i class="bi bi-check-circle-fill"></i> Cảm ơn bạn! Chúng tôi đã nhận được tin nhắn và sẽ phản hồi sớm nhất có thể.';
      alert.style.display = 'flex';
      alert.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
      document.getElementById('contactForm').reset();
      btn.disabled = false;
      btn.innerHTML = '<i class="bi bi-send"></i> <span>Gửi tin nhắn</span>';
    }, 1200);
  });

  /* ── User dropdown ── */
  (function () {
    const trigger = document.querySelector('.user-dropdown__trigger');
    const menu = document.querySelector('.user-dropdown__menu');
    if (!trigger || !menu) return;
    trigger.addEventListener('click', e => { e.stopPropagation(); menu.classList.toggle('open'); });
    document.addEventListener('click', () => menu.classList.remove('open'));
    menu.addEventListener('click', e => e.stopPropagation());
  })();
</script>
<script>
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
                var tabLogin = document.getElementById('tab-login');
                var tabRegister = document.getElementById('tab-register');
                var contentLogin = document.getElementById('content-login');
                var contentRegister = document.getElementById('content-register');

                if (tabLogin && tabRegister && contentLogin && contentRegister) {
                    if (tab === 'login') {
                        tabLogin.classList.add('active');
                        tabRegister.classList.remove('active');
                        contentLogin.classList.add('active');
                        contentRegister.classList.remove('active');
                    } else {
                        tabLogin.classList.remove('active');
                        tabRegister.classList.add('active');
                        contentLogin.classList.remove('active');
                        contentRegister.classList.add('active');
                    }
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
