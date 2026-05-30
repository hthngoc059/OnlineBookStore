<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${book.title} - Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            </a>
            <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
                <img src="${pageContext.request.contextPath}/images/bell.png" width="30" height="30" alt="bell"/>
            </a>
            <a href="${pageContext.request.contextPath}/wishlist" class="btn-cart">
                <img src="${pageContext.request.contextPath}/images/e-commerce.png" width="30" height="30" alt="orders"/>
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
                    <a href="javascript:void(0)" onclick="openModal('login')" class="btn-link">Tài khoản</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>

    <div class="detail-container">

        <nav class="breadcrumb">
            <a href="${pageContext.request.contextPath}/">Trang chủ</a>
            <span>›</span>
            <a href="${pageContext.request.contextPath}/books">Tất cả sách</a>
            <span>›</span>
            <c:forEach var="genre" items="${book.genres}" varStatus="st">
                <a href="${pageContext.request.contextPath}/books?genre=${genre.genreId}">${genre.genreName}</a>
                <c:if test="${!st.last}"><span>,</span></c:if>
            </c:forEach>
            <c:if test="${empty book.genres}"><span>Sách</span></c:if>
            <span>›</span>
            <span>${book.title}</span>
        </nav>

        <div class="detail-main">

            <div class="detail-cover-wrap">
                <img class="detail-cover"
                     src="${not empty book.coverImageUrl
                            ? book.coverImageUrl
                            : pageContext.request.contextPath.concat('/images/default-book.jpg')}"
                     alt="${book.title}"
                     onerror="this.src='${pageContext.request.contextPath}/images/default-book.jpg'">

                                <div class="detail-cover-actions">
                                    <c:choose>
                                        <c:when test="${sessionScope.currentUser != null}">
                                            <button class="btn-wishlist ${inWishlist ? 'active' : ''}" 
                                                    onclick="toggleWishlist(${book.bookId}, this)"
                                                    data-item-id="${wishlistItemId}">
                                                <span>${inWishlist ? '♥' : '♡'}</span>
                                                ${inWishlist ? 'Đã yêu thích' : 'Yêu thích'}
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn-wishlist" onclick="openModal('login')" style="flex:1;">
                                                ♡ Yêu thích
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
            </div>
            <div class="detail-info">

                <div class="detail-genres">
                    <c:forEach var="genre" items="${book.genres}">
                        <a href="${pageContext.request.contextPath}/books?genre=${genre.genreId}" class="genre-tag">
                            ${genre.genreName}
                        </a>
                    </c:forEach>
                    <c:if test="${empty book.genres}">
                        <span class="genre-tag">Sách</span>
                    </c:if>
                </div>

                <h1 class="detail-title">${book.title}</h1>
                <p class="detail-author">Tác giả: <strong>${book.author}</strong></p>

                <!-- Star rating -->
                <div class="detail-rating-row">
                    <div class="stars-display" id="starsDisplay">
                        <!-- Rendered by JS below -->
                    </div>
                    <span class="rating-score" id="ratingScore"></span>
                    <span class="rating-count">(${reviewCount} đánh giá)</span>
                </div>

                <!-- Price -->
                <div class="detail-price-section">
                    <span class="detail-price">
                        <fmt:formatNumber value="${book.price}" type="number" groupingUsed="true"/> ₫
                    </span>
                    <%-- If you add discounted price in the future, show it here --%>
                </div>

                <!-- Stock & Format chips -->
                <div class="detail-meta-row">
                    <c:choose>
                        <c:when test="${book.stockQuantity > 0}">
                            <span class="meta-chip in-stock">✔ Còn hàng (${book.stockQuantity})</span>
                        </c:when>
                        <c:otherwise>
                            <span class="meta-chip out-stock">✖ Hết hàng</span>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${not empty book.format}">
                        <span class="meta-chip">
                            <c:choose>
                                <c:when test="${book.format == 'paperback'}">📖 Bìa mềm</c:when>
                                <c:when test="${book.format == 'hardcover'}">📕 Bìa cứng</c:when>
                                <c:when test="${book.format == 'ebook'}">💻 Ebook</c:when>
                                <c:when test="${book.format == 'audiobook'}">🎧 Audiobook</c:when>
                                <c:otherwise>${book.format}</c:otherwise>
                            </c:choose>
                        </span>
                    </c:if>

                    <c:if test="${not empty book.language}">
                        <span class="meta-chip">🌐 ${book.language}</span>
                    </c:if>
                </div>

                <!-- Quantity selector -->
                <c:if test="${book.stockQuantity > 0}">
                    <div class="quantity-section">
                        <p class="quantity-label">SỐ LƯỢNG</p>
                        <div class="quantity-control">
                            <button class="qty-btn" id="btnMinus" onclick="changeQty(-1)">−</button>
                            <input class="qty-input" type="number" id="qtyInput"
                                   value="1" min="1" max="${book.stockQuantity}"
                                   onchange="validateQty()">
                            <button class="qty-btn" id="btnPlus" onclick="changeQty(1)">+</button>
                        </div>
                    </div>
                </c:if>

                <!-- Action buttons -->
                <div class="detail-action-btns">
                    <c:choose>
                        <c:when test="${book.stockQuantity > 0}">
                            <button class="btn-add-to-cart-lg" onclick="addToCart()">
                                🛒 Thêm vào giỏ
                            </button>
                            <button class="btn-buy-now" onclick="buyNow()">
                                ⚡ Mua ngay
                            </button>
                        </c:when>
                        <c:otherwise>
                            <button class="btn-add-to-cart-lg" disabled>🛒 Thêm vào giỏ</button>
                            <button class="btn-buy-now" disabled>⚡ Mua ngay</button>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Book specs -->
                <div class="detail-specs">
                    <p class="specs-title">Thông tin sách</p>
                    <table class="specs-table">
                        <c:if test="${not empty book.isbn}">
                            <tr><td>ISBN</td><td>${book.isbn}</td></tr>
                        </c:if>
                        <c:if test="${not empty book.publisher}">
                            <tr><td>Nhà xuất bản</td><td>${book.publisher}</td></tr>
                        </c:if>
                        <c:if test="${not empty book.publishedYear}">
                            <tr><td>Năm xuất bản</td><td>${book.publishedYear}</td></tr>
                        </c:if>
                        <c:if test="${not empty book.language}">
                            <tr><td>Ngôn ngữ</td><td>${book.language}</td></tr>
                        </c:if>
                        <c:if test="${not empty book.format}">
                            <tr>
                                <td>Định dạng</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${book.format == 'paperback'}">Bìa mềm</c:when>
                                        <c:when test="${book.format == 'hardcover'}">Bìa cứng</c:when>
                                        <c:when test="${book.format == 'ebook'}">Ebook</c:when>
                                        <c:when test="${book.format == 'audiobook'}">Audiobook</c:when>
                                        <c:otherwise>${book.format}</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:if>
                        <tr><td>Trạng thái</td>
                            <td style="color:${book.stockQuantity > 0 ? '#405a28' : '#ff3333'}; font-weight:700;">
                                ${book.stockQuantity > 0 ? 'Còn hàng' : 'Hết hàng'}
                            </td>
                        </tr>
                    </table>
                </div>

            </div><!-- /detail-info -->
        </div><!-- /detail-main -->
    </div><!-- /detail-container -->

    <!-- ===== DESCRIPTION ===== -->
    <c:if test="${not empty book.description}">
        <div class="detail-description">
            <div class="section-divider">
                <h2>Mô tả sách</h2>
            </div>
            <p class="desc-text">${book.description}</p>
        </div>
    </c:if>

    <!-- ===== RELATED BOOKS ===== -->
    <c:if test="${not empty relatedBooks}">
        <div class="related-section">
            <div class="section-divider">
                <h2>Sách cùng thể loại</h2>
            </div>
            <div class="books-grid">
                <c:forEach var="rb" items="${relatedBooks}">
                    <div class="book-card">
                        <a href="${pageContext.request.contextPath}/books?action=detail&id=${rb.bookId}">
                            <img class="book-image"
                                 src="${not empty rb.coverImageUrl
                                        ? rb.coverImageUrl
                                        : pageContext.request.contextPath.concat('/images/default-book.jpg')}"
                                 alt="${rb.title}"
                                 onerror="this.src='${pageContext.request.contextPath}/images/default-book.jpg'">
                        </a>
                        <div class="book-info">
                            <h3 class="book-title">${rb.title}</h3>
                            <p class="book-author">${rb.author}</p>
                            <p class="book-price">
                                <fmt:formatNumber value="${rb.price}" type="number" groupingUsed="true"/> ₫
                            </p>
                            <div class="book-actions">
                                <a href="${pageContext.request.contextPath}/books?action=detail&id=${rb.bookId}"
                                   class="btn-detail">Chi tiết</a>
                                <c:choose>
                                    <c:when test="${rb.stockQuantity > 0}">
                                        <a href="${pageContext.request.contextPath}/cart?action=add&id=${rb.bookId}"
                                           class="btn-add-cart">Thêm giỏ</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="btn-add-cart" style="opacity:0.4;cursor:not-allowed;">Hết hàng</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <!-- ===== REVIEWS ===== -->
    <div class="reviews-section">
        <div class="section-divider">
            <h2>Đánh giá khách hàng</h2>
        </div>

        <!-- Summary bar -->
        <div class="review-summary">
            <div class="review-score-big">
                <div class="score" id="avgScoreBig">0</div>
                <div class="stars-display" id="avgStarsBig"></div>
                <div class="total">${reviewCount} đánh giá</div>
            </div>
            <div class="review-bars" id="ratingBars">
                <!-- Rendered by JS -->
            </div>
        </div>

        <!-- Review list -->
        <div class="review-list">
            <c:choose>
                <c:when test="${empty book.reviews}">
                    <p style="color:#aaa; font-style:italic;">Chưa có đánh giá nào. Hãy là người đầu tiên!</p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="review" items="${book.reviews}">
                        <div class="review-card">
                            <div class="review-card-header">
                                <div class="reviewer-avatar">
                                    ${fn:substring(review.user.username, 0, 1).toUpperCase()}
                                </div>
                                <div class="reviewer-info">
                                    <div class="reviewer-name">${review.user.username}</div>
                                    <div class="reviewer-date">
                                        ${review.createdAt.toLocalDate()} ${review.createdAt.toLocalTime().toString().substring(0,5)}
                                    </div>
                                </div>
                                <div class="review-stars">
                                    <c:forEach begin="1" end="5" var="i">
                                        <span class="star ${i <= review.rating ? 'filled' : ''}">★</span>
                                    </c:forEach>
                                </div>
                            </div>
                            <c:if test="${not empty review.comment}">
                                <p class="review-comment">${review.comment}</p>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Write review -->
        <div class="write-review-box">
            <p class="write-review-title">Viết đánh giá của bạn</p>
            <c:choose>
                <c:when test="${sessionScope.currentUser != null}">
                    <div id="reviewAlert" style="display:none;"></div>

                    <input type="hidden" id="reviewBookId"    value="${book.bookId}"/>
                    <input type="hidden" id="reviewCsrfName"  value="${_csrf.parameterName}"/>
                    <input type="hidden" id="reviewCsrfToken" value="${_csrf.token}"/>
                    <input type="hidden" id="ratingInput"     value="0"/>

                    <div class="star-picker" id="starPicker">
                        <span class="star-pick" data-value="1">★</span>
                        <span class="star-pick" data-value="2">★</span>
                        <span class="star-pick" data-value="3">★</span>
                        <span class="star-pick" data-value="4">★</span>
                        <span class="star-pick" data-value="5">★</span>
                    </div>

                    <textarea class="review-textarea" id="reviewComment"
                              placeholder="Chia sẻ cảm nhận của bạn về cuốn sách này..."></textarea>

                    <button type="button" class="btn-submit-review" onclick="submitReview()">Gửi đánh giá</button>
                </c:when>
                <c:otherwise>
                    <p class="login-to-review">
                        Vui lòng <a href="javascript:void(0)" onclick="openModal('login')">đăng nhập</a>
                        để viết đánh giá.
                    </p>
                </c:otherwise>
            </c:choose>
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

    <!-- ===== AUTH MODAL (same as home.jsp) ===== -->
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
                <div class="tab-content active" id="content-login">
                    <c:if test="${not empty loginError}">
                        <div class="modal-alert modal-alert-error">${loginError}</div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/user" method="post">
                        <input type="hidden" 
           name="${_csrf.parameterName}" 
           value="${_csrf.token}"/>
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
                    <p class="modal-switch-text">Chưa có tài khoản? <a onclick="switchTab('register')">Đăng ký ngay</a></p>
                </div>
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
                    <p class="modal-switch-text">Đã có tài khoản? <a onclick="switchTab('login')">Đăng nhập</a></p>
                </div>
            </div>
        </div>
    </div>

    <!-- ===== SCRIPTS ===== -->
    <script>
        // ---- Data from server ----
        const MAX_QTY    = ${book.stockQuantity};
        const BOOK_ID    = ${book.bookId};
        const AVG_RATING = ${not empty avgRating ? avgRating : 0};
        const CTX        = '${pageContext.request.contextPath}';

        // ---- Render stars helper ----
        function renderStars(container, rating, size) {
            container.innerHTML = '';
            for (let i = 1; i <= 5; i++) {
                const s = document.createElement('span');
                s.className = 'star';
                if (i <= Math.floor(rating)) s.classList.add('filled');
                else if (i - rating < 1 && i - rating > 0) s.classList.add('half');
                s.style.fontSize = size || '1.3rem';
                s.textContent = '★';
                container.appendChild(s);
            }
        }

        // ---- Init rating displays ----
        document.addEventListener('DOMContentLoaded', function () {
            const avg = parseFloat(AVG_RATING) || 0;

            // Header rating
            const starsEl = document.getElementById('starsDisplay');
            const scoreEl = document.getElementById('ratingScore');
            if (starsEl) renderStars(starsEl, avg);
            if (scoreEl) scoreEl.textContent = avg > 0 ? avg.toFixed(1) + '/5' : 'Chưa có';

            // Big score in review summary
            const bigScore = document.getElementById('avgScoreBig');
            const bigStars = document.getElementById('avgStarsBig');
            if (bigScore) bigScore.textContent = avg > 0 ? avg.toFixed(1) : '—';
            if (bigStars) renderStars(bigStars, avg, '1.1rem');

            // Rating bars (counts from server)
            const barsContainer = document.getElementById('ratingBars');
            if (barsContainer) {
                const counts = {
                    5: parseInt('${ratingCounts["5"]}') || 0,
                    4: parseInt('${ratingCounts["4"]}') || 0,
                    3: parseInt('${ratingCounts["3"]}') || 0,
                    2: parseInt('${ratingCounts["2"]}') || 0,
                    1: parseInt('${ratingCounts["1"]}') || 0
                };
                const total = Object.values(counts).reduce((a, b) => a + b, 0);
                barsContainer.innerHTML = '';
                [5, 4, 3, 2, 1].forEach(star => {
                    const cnt = counts[star] || 0;
                    const pct = total > 0 ? (cnt / total * 100) : 0;
                    barsContainer.innerHTML += '<div class="rating-bar-row">'
                        + '<span class="rating-bar-label">' + star + ' ★</span>'
                        + '<div class="rating-bar-track">'
                        + '<div class="rating-bar-fill" style="width:' + pct + '%"></div>'
                        + '</div>'
                        + '<span class="rating-bar-count">' + cnt + '</span>'
                        + '</div>';
                });
            }

            // Star picker for write review
            const picks = document.querySelectorAll('.star-pick');
            picks.forEach(pick => {
                pick.addEventListener('mouseover', function () {
                    const val = parseInt(this.dataset.value);
                    picks.forEach((p, i) => {
                        p.style.color = i < val ? '#f5a623' : '#ddd';
                    });
                });
                pick.addEventListener('mouseleave', function () {
                    const selected = document.getElementById('ratingInput').value;
                    picks.forEach((p, i) => {
                        p.style.color = i < parseInt(selected) ? '#f5a623' : '#ddd';
                    });
                });
                pick.addEventListener('click', function () {
                    const val = parseInt(this.dataset.value);
                    document.getElementById('ratingInput').value = val;
                    picks.forEach((p, i) => {
                        p.classList.toggle('selected', i < val);
                        p.style.color = i < val ? '#f5a623' : '#ddd';
                    });
                });
            });

            // Qty button initial state
            updateQtyButtons();
        });

        // ---- Quantity control ----
        function changeQty(delta) {
            const input = document.getElementById('qtyInput');
            if (!input) return;
            let val = parseInt(input.value) + delta;
            val = Math.max(1, Math.min(MAX_QTY, val));
            input.value = val;
            updateQtyButtons();
        }

        function validateQty() {
            const input = document.getElementById('qtyInput');
            if (!input) return;
            let val = parseInt(input.value);
            if (isNaN(val) || val < 1) val = 1;
            if (val > MAX_QTY) val = MAX_QTY;
            input.value = val;
            updateQtyButtons();
        }

        function updateQtyButtons() {
            const input = document.getElementById('qtyInput');
            const btnMinus = document.getElementById('btnMinus');
            const btnPlus  = document.getElementById('btnPlus');
            if (!input) return;
            const val = parseInt(input.value);
            if (btnMinus) btnMinus.disabled = val <= 1;
            if (btnPlus)  btnPlus.disabled  = val >= MAX_QTY;
        }

        // ---- Add to cart ----
        function addToCart() {
            const qty = document.getElementById('qtyInput') ? document.getElementById('qtyInput').value : 1;
            window.location.href = CTX + '/cart?action=add&id=' + BOOK_ID + '&qty=' + qty;
        }

        // ---- Buy now ----
        function buyNow() {
            const qty = document.getElementById('qtyInput') ? document.getElementById('qtyInput').value : 1;
            // Add to cart then redirect to checkout
            fetch(CTX + '/cart?action=add&id=' + BOOK_ID + '&qty=' + qty, { method: 'GET' })
                .then(() => { window.location.href = CTX + '/checkout'; })
                .catch(() => { window.location.href = CTX + '/cart?action=add&id=' + BOOK_ID + '&qty=' + qty; });
        }

        // ---- Modal ----
        function openModal(tab) {
            var modal = document.getElementById('authModal');
            if (modal) { modal.style.display = 'flex'; modal.classList.add('active'); switchTab(tab || 'login'); document.body.style.overflow = 'hidden'; }
        }
        function closeModal() {
            var modal = document.getElementById('authModal');
            if (modal) { modal.style.display = 'none'; modal.classList.remove('active'); document.body.style.overflow = ''; }
        }
        function handleOverlayClick(e) {
            if (e.target === document.getElementById('authModal')) closeModal();
        }
        function switchTab(tab) {
            ['login','register'].forEach(t => {
                document.getElementById('tab-' + t).classList.toggle('active', t === tab);
                document.getElementById('content-' + t).classList.toggle('active', t === tab);
            });
        }
        document.addEventListener('keydown', e => { if (e.key === 'Escape') closeModal(); });
        window.addEventListener('DOMContentLoaded', () => {
            var modal = document.getElementById('authModal');
            if (modal) modal.style.display = 'none';
        });
        async function toggleWishlist(bookId, btn) {
        const inWishlist = btn.classList.contains('active');
        const action = inWishlist ? 'remove' : 'add';

        const params = new URLSearchParams();
        params.append('${_csrf.parameterName}', '${_csrf.token}');

        if (action === 'add') {
          params.append('bookId', bookId);
        } else {
          // Cần wishlistItemId — lấy từ data attribute
          params.append('itemId', btn.dataset.itemId);
        }

        const resp = await fetch(CTX + '/wishlist?action=' + action, {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: params.toString()
        });
        const data = await resp.json();

        if (data.success) {
          btn.classList.toggle('active');
          btn.innerHTML = btn.classList.contains('active')
            ? '<i class="bi bi-heart-fill"></i> Đã yêu thích'
            : '<i class="bi bi-heart"></i> Yêu thích';
        }
      }
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
