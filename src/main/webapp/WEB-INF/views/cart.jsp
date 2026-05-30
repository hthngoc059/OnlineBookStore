<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ hàng - Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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

    <!-- ===== CART CONTENT ===== -->
    <div class="notif-page-header">
  <div class="notif-page-header__inner">
    <div class="search-breadcrumb">
      <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
      <span>›</span>
      <span>Giỏ hàng</span>
    </div>
    <div class="notif-page-title-row">
      <h2 class="notif-page-title">
        Giỏ hàng của bạn
        <c:if test="${not empty cartItems}">
          <span class="notif-unread-chip">${fn:length(cartItems)} sản phẩm</span>
        </c:if>
      </h2>
      <c:if test="${not empty cartItems}">
        <a href="${pageContext.request.contextPath}/cart?action=clear"
           class="btn-notif-action btn-notif-action--danger"
           onclick="return confirm('Xóa toàn bộ giỏ hàng?')">
          <i class="bi bi-trash"></i> Xóa tất cả
        </a>
      </c:if>
    </div>
  </div>
</div>
      <div class="cart-container">
        <c:choose>
            <c:when test="${sessionScope.currentUser == null}">
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
                    </div>
                    <div class="cart-summary-right">
                        <p class="cart-total-label">Tổng cộng</p>
                        <p class="cart-total-price">
                            <fmt:formatNumber value="${cartTotal}" type="number" groupingUsed="true"/> ₫
                        </p>
                        <a href="${pageContext.request.contextPath}/checkout" class="btn-checkout">
                            Thanh toán
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
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