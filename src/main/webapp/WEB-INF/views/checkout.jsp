<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Thanh toán – BookStore</title>

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
<div id="loadingOverlay">
  <div class="loader"></div>
  <span>Đang xử lý đơn hàng...</span>
</div>


<!-- Steps -->
<div class="checkout-steps">
  <div class="step-item done">
    <div class="step-num"><i class="bi bi-check"></i></div>
    <span>GIỎ HÀNG</span>
  </div>
  <div class="step-divider done"></div>
  <div class="step-item active">
    <div class="step-num">2</div>
    <span>THANH TOÁN</span>
  </div>
  <div class="step-divider"></div>
  <div class="step-item">
    <div class="step-num">3</div>
    <span>XÁC NHẬN</span>
  </div>
</div>

<div class="checkout-wrapper">

  <!-- ── LEFT: form ── -->
  <div>

    <c:if test="${not empty errorMessage}">
      <div class="alert-error">
        <i class="bi bi-exclamation-circle-fill"></i>
        <span>${errorMessage}</span>
      </div>
    </c:if>

    <form id="checkoutForm" method="post"
          action="${pageContext.request.contextPath}/checkout"
          onsubmit="handleSubmit(event)">
        <input type="hidden" 
           name="${_csrf.parameterName}" 
           value="${_csrf.token}"/>
      <!-- 1. Địa chỉ giao hàng -->
      <div class="co-card">
        <div class="co-card-header">
          <i class="bi bi-geo-alt-fill co-icon"></i>
          <h2>Địa chỉ giao hàng</h2>
        </div>
        <div class="co-card-body">
          <c:choose>
            <c:when test="${empty addresses}">
              <div class="no-address">
                <i class="bi bi-house-slash"></i>
                Bạn chưa có địa chỉ giao hàng nào.<br/>
                <a href="${pageContext.request.contextPath}/profile/addresses"
                   class="btn-add-addr" style="margin-top:12px;">
                  <i class="bi bi-plus"></i> Thêm địa chỉ
                </a>
              </div>
            </c:when>
            <c:otherwise>
              <div class="address-grid">
                <c:forEach var="addr" items="${addresses}" varStatus="st">
                  <label class="address-option ${st.first ? 'selected' : ''}"
                         onclick="selectCard(this, 'address-option')">
                    <input type="radio" name="addressId"
                           value="${addr.addressId}"
                           ${st.first ? 'checked' : ''}/>
                    <c:if test="${addr.isDefault}">
                      <span class="addr-default">Mặc định</span><br/>
                    </c:if>
                    <div class="addr-name">${addr.fullName}</div>
                    <div class="addr-text">
                      ${addr.phone}<br/>
                      ${addr.addressLine}, ${addr.ward},<br/>
                      ${addr.district}, ${addr.city}
                    </div>
                  </label>
                </c:forEach>
              </div>
              <a href="${pageContext.request.contextPath}/profile/addresses"
                 class="btn-add-addr">
                <i class="bi bi-plus"></i> Thêm địa chỉ mới
              </a>
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <!-- 2. Phương thức thanh toán -->
      <div class="co-card">
        <div class="co-card-header">
          <i class="bi bi-credit-card-fill co-icon"></i>
          <h2>Phương thức thanh toán</h2>
        </div>
        <div class="co-card-body">
          <div class="payment-list">

            <label class="payment-option selected"
                   onclick="selectCard(this, 'payment-option')">
              <input type="radio" name="paymentMethod" value="cod" checked/>
              <div class="pay-icon-wrap cod"><i class="bi bi-cash-coin"></i></div>
              <div>
                <div class="pay-name">Tiền mặt khi nhận hàng (COD)</div>
                <div class="pay-desc">Thanh toán trực tiếp khi shipper giao hàng</div>
              </div>
            </label>

            <label class="payment-option"
                   onclick="selectCard(this, 'payment-option')">
              <input type="radio" name="paymentMethod" value="banking"/>
              <div class="pay-icon-wrap online"><i class="bi bi-wallet2"></i></div>
              <div>
                <div class="pay-name">Thanh toán trực tuyến (Mô phỏng)</div>
                <div class="pay-desc">Xác nhận ngay sau khi đặt hàng</div>
              </div>
            </label>

          </div>
        </div>
      </div>

      <!-- 3. Mã giảm giá -->
      <div class="co-card">
        <div class="co-card-header">
          <i class="bi bi-tag-fill co-icon"></i>
          <h2>Mã giảm giá</h2>
        </div>
        <div class="co-card-body">
          <div class="discount-row">
            <input type="text" name="discountCode" id="discountInput"
                   placeholder="Nhập mã (VD: SALE10)"
                   oninput="this.value = this.value.toUpperCase()"/>
            <button type="button" class="btn-apply-discount"
                    onclick="applyDiscount()">Áp dụng</button>
          </div>
          <div id="discountMsg"></div>
        </div>
      </div>

    </form>
  </div>

  <!-- ── RIGHT: order summary ── -->
  <div class="summary-sticky">
    <div class="co-card">
      <div class="co-card-header">
        <i class="bi bi-bag-check-fill co-icon"></i>
        <h2>Tóm tắt đơn hàng</h2>
      </div>
      <div class="co-card-body">

        <ul class="summary-items">
          <c:forEach var="item" items="${cartItems}">
            <li class="summary-item">
              <img src="${not empty item.book.coverImageUrl 
                            ? item.book.coverImageUrl 
                            : pageContext.request.contextPath.concat('/images/default-book.jpg')}"
                   alt="${item.book.title}"
                   onerror="this.src='${pageContext.request.contextPath}/images/default-book.jpg'"/>
              <div class="si-info">
                <div class="si-title">${item.book.title}</div>
                <div class="si-author">${item.book.author}</div>
                <div class="si-bottom">
                  <span class="si-qty">×${item.quantity}</span>
                  <span class="si-subtotal">
                    <fmt:formatNumber
                        value="${item.book.price.multiply(item.quantity)}"
                        type="number" maxFractionDigits="0"/>đ
                  </span>
                </div>
              </div>
            </li>
          </c:forEach>
        </ul>

        <table class="totals-table">
          <tr>
            <td class="label-cell">Tạm tính</td>
            <td id="subtotalDisplay">
              <fmt:formatNumber value="${totalAmount}"
                                type="number" maxFractionDigits="0"/>đ
            </td>
          </tr>
          <tr id="discountRow" style="display:none;">
            <td class="label-cell">Giảm giá</td>
            <td class="discount-cell" id="discountDisplay"></td>
          </tr>
          <tr>
            <td class="label-cell">Vận chuyển</td>
            <td style="color:#1a7a3c;font-weight:700;">Miễn phí</td>
          </tr>
          <tr class="grand-row">
            <td>Tổng cộng</td>
            <td id="totalDisplay">
              <fmt:formatNumber value="${totalAmount}"
                                type="number" maxFractionDigits="0"/>đ
            </td>
          </tr>
        </table>

        <button type="submit" form="checkoutForm"
                class="btn-checkout" id="placeBtn"
                ${empty addresses ? 'disabled' : ''}>
          <i class="bi bi-shield-check"></i>
          ĐẶT HÀNG NGAY
        </button>

        <div class="secure-note">
          <i class="bi bi-lock-fill"></i> Thông tin của bạn được bảo mật
        </div>

      </div>
    </div>
  </div>

</div>

<footer>
  <p>© 2024 BookStore. All rights reserved.</p>
</footer>

<script>
  const RAW_TOTAL = ${totalAmount};
  const CSRF_TOKEN_NAME = "${_csrf.parameterName}";
  const CSRF_TOKEN_VALUE = "${_csrf.token}";
  const CTX = "${pageContext.request.contextPath}";

  let appliedDiscount = 0;
  let appliedDiscountId = null;

  function selectCard(label, cls) {
    document.querySelectorAll('.' + cls).forEach(el => el.classList.remove('selected'));
    label.classList.add('selected');
  }

  async function applyDiscount() {
    const code = document.getElementById('discountInput').value.trim().toUpperCase();
    const msg  = document.getElementById('discountMsg');
    const btn  = document.querySelector('.btn-apply-discount');

    if (!code) {
      showMsg(msg, 'err', 'Vui lòng nhập mã giảm giá');
      return;
    }

    // Loading state
    btn.disabled = true;
    btn.textContent = 'Đang kiểm tra...';
    msg.className = '';
    msg.textContent = '';

    try {
      const params = new URLSearchParams();
      params.append(CSRF_TOKEN_NAME, CSRF_TOKEN_VALUE);
      params.append('code', code);
      params.append('totalAmount', RAW_TOTAL);

      const res = await fetch(CTX + '/checkout/validate-discount', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
      });

      const data = await res.json();

      if (data.success) {
        appliedDiscount   = Number(data.discountAmount);
        appliedDiscountId = data.discountId;

        // Thêm hidden input discountId vào form để submit
        let hiddenInput = document.getElementById('hiddenDiscountId');
        if (!hiddenInput) {
          hiddenInput = document.createElement('input');
          hiddenInput.type = 'hidden';
          hiddenInput.id   = 'hiddenDiscountId';
          hiddenInput.name = 'discountId';
          document.getElementById('checkoutForm').appendChild(hiddenInput);
        }
        hiddenInput.value = appliedDiscountId;

        // Hiện modal thành công
        showSuccessModal(data.description || code, appliedDiscount);
        updateTotals();

      } else {
        appliedDiscount   = 0;
        appliedDiscountId = null;
        // Xoá hidden input nếu có
        const old = document.getElementById('hiddenDiscountId');
        if (old) old.remove();

        showMsg(msg, 'err', '❌ ' + (data.message || 'Mã không hợp lệ hoặc đã hết hạn'));
        updateTotals();
      }

    } catch (err) {
      showMsg(msg, 'err', '❌ Lỗi kết nối, vui lòng thử lại');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Áp dụng';
    }
  }

  function showMsg(el, cls, text) {
    el.className = cls;
    el.textContent = text;
  }

  /* ── Modal thông báo áp dụng voucher thành công ── */
  function showSuccessModal(description, amount) {
    // Tạo modal nếu chưa có
    let modal = document.getElementById('voucherModal');
    if (!modal) {
      modal = document.createElement('div');
      modal.id = 'voucherModal';
      modal.innerHTML = `
        <div class="voucher-modal-overlay" onclick="closeVoucherModal()"></div>
        <div class="voucher-modal-box">
          <div class="voucher-modal-icon"><i class="bi bi-patch-check-fill"></i></div>
          <h3>Áp dụng mã thành công!</h3>
          <p id="voucherModalDesc"></p>
          <p class="voucher-modal-amount">Tiết kiệm: <strong id="voucherModalAmt"></strong></p>
          <button class="btn-voucher-close" onclick="closeVoucherModal()">Tuyệt vời!</button>
        </div>`;
      document.body.appendChild(modal);
    }
    document.getElementById('voucherModalDesc').textContent = description;
    document.getElementById('voucherModalAmt').textContent  = fmtNum(amount) + 'đ';
    modal.classList.add('show');
  }

  function closeVoucherModal() {
    const modal = document.getElementById('voucherModal');
    if (modal) modal.classList.remove('show');
  }

  function updateTotals() {
    const final_ = Math.max(0, RAW_TOTAL - appliedDiscount);
    document.getElementById('totalDisplay').textContent = fmtNum(final_) + 'đ';
    const row = document.getElementById('discountRow');
    if (appliedDiscount > 0) {
      row.style.display = '';
      document.getElementById('discountDisplay').textContent = '-' + fmtNum(appliedDiscount) + 'đ';
    } else {
      row.style.display = 'none';
    }
  }

  function fmtNum(n) {
    return Math.round(n).toLocaleString('vi-VN');
  }

  function handleSubmit(e) {
    if (!document.querySelector('input[name="addressId"]:checked')) {
      e.preventDefault();
      alert('Vui lòng chọn địa chỉ giao hàng');
      return;
    }
    document.getElementById('loadingOverlay').classList.add('show');
    document.getElementById('placeBtn').disabled = true;
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
