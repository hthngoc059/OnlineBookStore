<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Home Page</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>
        <nav class="navbar">
            <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"></h1>
            <!--NAV LINKS -->
            <ul class="navbar__nav">
                <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/books">Tất cả sách</a></li>
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
        <main class="books-container">
            <div class="section-header">
                <h2 class="section-title">Sách  Nổi Bật</h2>
            </div>
            <c:choose>
                <c:when test="${empty featuredBooks}">
                    <div class="no-books">
                        <p>Hiện chưa có sách nào.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="books-grid">
                        <c:forEach var="book" items="${featuredBooks}">
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
                                    <p class="book-price">${book.priceFormatted}</p>

                                    <!-- Hiện badge nếu hết hàng -->
                                    <c:if test="${book.stockQuantity == 0}">
                                        <span style="color:red; font-size:0.8rem;">Hết hàng</span>
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
                                                <span class="btn-add-cart"
                                                      style="opacity:0.4; cursor:not-allowed;">Hết hàng</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                            </div>
                        </c:forEach>
                    </div>

                    <!-- Xem thêm button -->
                    <div class="view-more-wrapper">
                        <a href="${pageContext.request.contextPath}/books" class="btn-view-more">Xem thêm</a>
                    </div>

                </c:otherwise>
            </c:choose>
        </main>
        <main class="books-container">
            <div class="section-header">
                <h2 class="section-title">Sách  Giảm Giá</h2>
            </div>
            <c:choose>
                <c:when test="${empty bestSellers}">
                    <div class="no-books">
                        <p>Hiện chưa có sách nào.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="books-grid">
                        <c:forEach var="book" items="${bestSellers}">
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
                                    <p class="book-price">${book.priceFormatted}</>

                                    <!-- Hiện badge nếu hết hàng -->
                                    <c:if test="${book.stockQuantity == 0}">
                                        <span style="color:red; font-size:0.8rem;">Hết hàng</span>
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
                                                <span class="btn-add-cart"
                                                      style="opacity:0.4; cursor:not-allowed;">Hết hàng</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                            </div>
                        </c:forEach>
                    </div>

                    <!-- Xem thêm button -->
                    <div class="view-more-wrapper">
                        <a href="${pageContext.request.contextPath}/books" class="btn-view-more">Xem thêm</a>
                    </div>

                </c:otherwise>
            </c:choose>
        </main>                        
        <footer>
            <p>&copy; 2024 Nhà Sách Online. All rights reserved.</p>
        </footer>
        <div class="modal-overlay" id="authModal" onclick="handleOverlayClick(event)">
            <div class="modal-box">

                <!-- LEFT: illustration panel -->
                <div class="modal-left">
                    <div class="modal-left-icon">📚</div>
                    <h3>Nhà Sách Online</h3>
                    <p>Khám phá hàng nghìn đầu sách hay. Đặt hàng nhanh, giao tận nơi.</p>
                    <span class="promo-badge">🎁 Giảm 10% đơn đầu tiên</span>
                </div>

                <!-- RIGHT: form panel -->
                <div class="modal-right">
                    <button class="modal-close" onclick="closeModal()">✕</button>

                    <!-- Tabs -->
                    <div class="modal-tabs">
                        <button class="modal-tab active" id="tab-login" onclick="switchTab('login')">ĐĂNG NHẬP</button>
                        <button class="modal-tab" id="tab-register" onclick="switchTab('register')">ĐĂNG KÝ</button>
                    </div>

                    <!-- ── LOGIN FORM ── -->
                    <div class="tab-content active" id="content-login">
                        <c:if test="${not empty loginError}">
                            <div class="modal-alert modal-alert-error">${loginError}</div>
                        </c:if>
                        <c:if test="${not empty successMessage}">
                            <div class="modal-alert modal-alert-success">${successMessage}</div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/user" method="post">
                            <input type="hidden" 
           name="${_csrf.parameterName}" 
           value="${_csrf.token}"/>
                            <input type="hidden" name="action" value="login">

                            <div class="modal-form-group">
                                <label for="loginUsername">Tên đăng nhập</label>
                                <input type="text" id="loginUsername" name="username"
                                       placeholder="Nhập tên đăng nhập" required>
                            </div>
                            <div class="modal-form-group">
                                <label for="loginPassword">Mật khẩu</label>
                                <input type="password" id="loginPassword" name="password"
                                       placeholder="Nhập mật khẩu" required>
                            </div>

                            <button type="submit" class="btn-modal-submit">Đăng nhập</button>
                        </form>

                        <p class="modal-switch-text">
                            Chưa có tài khoản?
                            <a onclick="switchTab('register')">Đăng ký ngay</a>
                        </p>
                    </div>

                    <!-- ── REGISTER FORM ── -->
                    <div class="tab-content" id="content-register">
                        <c:if test="${not empty registerError}">
                            <div class="modal-alert modal-alert-error">${registerError}</div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/user" method="post">
                            <input type="hidden" 
           name="${_csrf.parameterName}" 
           value="${_csrf.token}"/>
                            <input type="hidden" name="action" value="register">

                            <div class="modal-form-group">
                                <label for="regUsername">Tên đăng nhập *</label>
                                <input type="text" id="regUsername" name="username"
                                       placeholder="Tối thiểu 3 ký tự" required
                                       class="${not empty usernameError ? 'input-error' : ''}">
                                <c:if test="${not empty usernameError}">
                                    <span class="field-error">${usernameError}</span>
                                </c:if>
                            </div>

                            <div class="modal-form-group">
                                <label for="regEmail">Email *</label>
                                <input type="email" id="regEmail" name="email"
                                       placeholder="example@email.com" required
                                       class="${not empty emailError ? 'input-error' : ''}">
                                <c:if test="${not empty emailError}">
                                    <span class="field-error">${emailError}</span>
                                </c:if>
                            </div>

                            <div class="modal-form-group">
                                <label for="regPhone">Số điện thoại</label>
                                <input type="tel" id="regPhone" name="phoneNumber"
                                       placeholder="0901234567">
                            </div>

                            <div class="modal-form-group">
                                <label for="regPassword">Mật khẩu *</label>
                                <input type="password" id="regPassword" name="password"
                                       placeholder="Tối thiểu 6 ký tự" required
                                       class="${not empty passwordError ? 'input-error' : ''}">
                                <c:if test="${not empty passwordError}">
                                    <span class="field-error">${passwordError}</span>
                                </c:if>
                            </div>

                            <div class="modal-form-group">
                                <label for="regConfirm">Xác nhận mật khẩu *</label>
                                <input type="password" id="regConfirm" name="confirmPassword"
                                       placeholder="Nhập lại mật khẩu" required
                                       class="${not empty confirmPasswordError ? 'input-error' : ''}">
                                <c:if test="${not empty confirmPasswordError}">
                                    <span class="field-error">${confirmPasswordError}</span>
                                </c:if>
                            </div>

                            <button type="submit" class="btn-modal-submit">Đăng ký</button>
                        </form>

                        <p class="modal-switch-text">
                            Đã có tài khoản?
                            <a onclick="switchTab('login')">Đăng nhập</a>
                        </p>
                    </div>

                </div><!-- /modal-right -->
            </div><!-- /modal-box -->
        </div><!-- /modal-overlay -->

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
