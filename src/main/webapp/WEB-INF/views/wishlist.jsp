<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Yêu thích – BookStore</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
</head>
<body>

<!-- ===== NAVBAR ===== -->
<nav class="navbar">
  <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"></h1>
  <ul class="navbar__nav">
    <li><a href="${pageContext.request.contextPath}/">Trang chủ</a></li>
    <li><a href="${pageContext.request.contextPath}/books">Tất cả sách</a></li>
    <li><a href="${pageContext.request.contextPath}/">Giới thiệu</a></li>
    <li><a href="${pageContext.request.contextPath}/">Liên hệ</a></li>
    <li class="navbar__search-item">
      <form action="${pageContext.request.contextPath}/books" method="get">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="action" value="search">
        <input type="text" name="keyword" placeholder="Tìm sách..." autocomplete="off">
        <button type="submit">
          <img src="${pageContext.request.contextPath}/images/magnifying-glass.png" width="30" height="30" alt="search"/>
        </button>
      </form>
    </li>
    <c:if test="${sessionScope.currentUser.role == 'admin'}">
      <li><a href="${pageContext.request.contextPath}/admin/dashboard">Quản trị</a></li>
    </c:if>
  </ul>
  <div class="navbar__action">
    <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
      <img src="${pageContext.request.contextPath}/images/online-shopping.png" width="30" height="30" alt="cart"/>
      <c:if test="${sessionScope.cartCount > 0}">
        <span class="cart-count">${sessionScope.cartCount}</span>
      </c:if>
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
            <a href="${pageContext.request.contextPath}/wishlist" class="user-dropdown__item">
              <i class="bi bi-heart"></i> Yêu thích
            </a>
            <div class="user-dropdown__divider"></div>
            <a href="${pageContext.request.contextPath}/user?action=logout"
               class="user-dropdown__item user-dropdown__item--logout">
              <i class="bi bi-box-arrow-right"></i> Đăng xuất
            </a>
          </div>
        </div>
      </c:when>
      <c:otherwise>
        <a href="${pageContext.request.contextPath}/" class="btn-link">Tài khoản</a>
      </c:otherwise>
    </c:choose>
  </div>
</nav>

<!-- ===== CONTENT ===== -->
<div class="wishlist-container">

  <!-- Breadcrumb -->
  <nav class="breadcrumb" style="margin-bottom:20px;">
    <a href="${pageContext.request.contextPath}/">Trang chủ</a>
    <span>›</span>
    <span>Yêu thích</span>
  </nav>

  <div class="wishlist-topbar">
    <div class="section-header" style="margin:0; border:none; padding:0;">
      <h2 class="section-title">
        <i class="bi bi-heart-fill" style="color:#e53935; margin-right:8px;"></i>
        Danh sách yêu thích
        <c:if test="${wishlist.totalItems > 0}">
          <span style="font-size:0.85rem; color:#888; font-weight:400; margin-left:8px;">
            (${wishlist.totalItems} sản phẩm)
          </span>
        </c:if>
      </h2>
    </div>
    <c:if test="${wishlist.totalItems > 0}">
      <button class="btn-add-all" onclick="addAllToCart()">
        <i class="bi bi-cart-plus"></i> Thêm tất cả vào giỏ
      </button>
    </c:if>
  </div>

  <c:choose>
    <c:when test="${empty wishlist.items}">
      <div class="wishlist-empty">
        <i class="bi bi-heart"></i>
        <h3>Danh sách yêu thích trống</h3>
        <p>Hãy thêm những cuốn sách bạn yêu thích vào đây nhé!</p>
        <a href="${pageContext.request.contextPath}/books">Khám phá sách</a>
      </div>
    </c:when>
    <c:otherwise>
      <div class="wishlist-grid" id="wishlistGrid">
        <c:forEach var="item" items="${wishlist.items}">
          <div class="wl-card" id="wlCard_${item.wishlistItemId}">

            <a href="${pageContext.request.contextPath}/books?action=detail&id=${item.bookId}">
              <img class="wl-card__img"
                   src="${not empty item.coverImageUrl
                           ? item.coverImageUrl
                           : pageContext.request.contextPath.concat('/images/default-book.jpg')}"
                   alt="${item.bookTitle}"
                   onerror="this.src='${pageContext.request.contextPath}/images/default-book.jpg'"/>
            </a>

            <div class="wl-card__body">
              <a href="${pageContext.request.contextPath}/books?action=detail&id=${item.bookId}"
                 style="text-decoration:none;">
                <div class="wl-card__title">${item.bookTitle}</div>
              </a>
              <div class="wl-card__author">${item.bookAuthor}</div>
              <div class="wl-card__price">
                <fmt:formatNumber value="${item.price}" type="number" maxFractionDigits="0"/>đ
              </div>
              <span class="wl-card__stock ${item.stockQuantity > 0 ? 'in' : 'out'}">
                ${item.stockQuantity > 0 ? 'Còn hàng' : 'Hết hàng'}
              </span>

              <div class="wl-card__actions">
                <button class="btn-wl-cart"
                        ${item.stockQuantity <= 0 ? 'disabled' : ''}
                        onclick="moveToCart(${item.wishlistItemId}, this)">
                  <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                </button>
                <button class="btn-wl-remove"
                        title="Xóa khỏi yêu thích"
                        onclick="removeFromWishlist(${item.wishlistItemId})">
                  <i class="bi bi-trash"></i>
                </button>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>

</div>

<!-- Toast -->
<div id="wlToast"></div>

<footer>
  <p>© 2024 BookStore. All rights reserved.</p>
</footer>

<script>
  const CTX              = "${pageContext.request.contextPath}";
  const CSRF_NAME        = "${_csrf.parameterName}";
  const CSRF_VALUE       = "${_csrf.token}";

  function buildParams(extra) {
    const p = new URLSearchParams();
    p.append(CSRF_NAME, CSRF_VALUE);
    Object.entries(extra).forEach(([k, v]) => p.append(k, v));
    return p;
  }

  async function post(action, body) {
    const resp = await fetch(CTX + '/wishlist?action=' + action, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: buildParams(body).toString()
    });
    return resp.json();
  }

  /* ── Xóa 1 sản phẩm ── */
  async function removeFromWishlist(itemId) {
    const card = document.getElementById('wlCard_' + itemId);
    if (card) card.style.opacity = '0.4';

    const data = await post('remove', { itemId });

    if (data.success) {
      if (card) card.remove();
      showToast('Đã xóa khỏi danh sách yêu thích', 'success');
      checkEmpty();
    } else {
      if (card) card.style.opacity = '1';
      showToast(data.message || 'Có lỗi xảy ra', 'error');
    }
  }

  /* ── Chuyển 1 sản phẩm vào giỏ ── */
  async function moveToCart(itemId, btn) {
    btn.disabled = true;
    btn.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang xử lý...';

    const data = await post('moveToCart', { itemId });

    if (data.success) {
      const card = btn.closest('.wl-card');
      if (card) card.remove();
      showToast('Đã thêm vào giỏ hàng!', 'success');
      checkEmpty();
    } else {
      btn.disabled = false;
      btn.innerHTML = '<i class="bi bi-cart-plus"></i> Thêm vào giỏ';
      showToast(data.message || 'Có lỗi xảy ra', 'error');
    }
  }

  /* ── Thêm tất cả vào giỏ ── */
  async function addAllToCart() {

    const btn = document.querySelector('.btn-add-all');

    if (btn) {
        btn.disabled = true;
        btn.textContent = 'Đang xử lý...';
    }

    const resp = await fetch(CTX + '/wishlist/addAllToCart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: buildParams({}).toString()
    });

    const data = await resp.json();

    if (data.success) {

        showToast('Đã thêm tất cả vào giỏ hàng!', 'success');

        setTimeout(() => {
            location.href = CTX + '/cart';
        }, 900);

    } else {

        if (btn) {
            btn.disabled = false;
            btn.innerHTML =
                '<i class="bi bi-cart-plus"></i> Thêm tất cả vào giỏ';
        }

        showToast(data.message || 'Có lỗi xảy ra', 'error');
    }
}

  /* ── Kiểm tra danh sách có rỗng không ── */
  function checkEmpty() {
    const grid = document.getElementById('wishlistGrid');
    if (grid && grid.children.length === 0) {
      grid.outerHTML = `
        <div class="wishlist-empty">
          <i class="bi bi-heart"></i>
          <h3>Danh sách yêu thích trống</h3>
          <p>Hãy thêm những cuốn sách bạn yêu thích vào đây nhé!</p>
          <a href="${pageContext.request.contextPath}/books">Khám phá sách</a>
        </div>`;
      const topBtn = document.querySelector('.btn-add-all');
      if (topBtn) topBtn.remove();
    }
  }

  /* ── Toast ── */
  let toastTimer;
  function showToast(msg, type = 'success') {
    const el = document.getElementById('wlToast');
    el.textContent = msg;
    el.className = 'show ' + type;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => el.className = '', 2800);
  }

  /* ── User dropdown ── */
  (function() {
    const trigger = document.querySelector('.user-dropdown__trigger');
    const menu    = document.querySelector('.user-dropdown__menu');
    if (!trigger || !menu) return;
    trigger.addEventListener('click', e => { e.stopPropagation(); menu.classList.toggle('open'); });
    document.addEventListener('click', () => menu.classList.remove('open'));
    menu.addEventListener('click', e => e.stopPropagation());
  })();
</script>
</body>
</html>