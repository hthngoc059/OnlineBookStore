<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>${editMode ? 'Cập nhật' : 'Thêm'} địa chỉ – Nhà Sách Online</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
</head>
<body>

<%-- ── NAVBAR ── --%>
<nav class="navbar">
  <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"/></h1>
  <ul class="navbar__nav">
    <li><a href="${pageContext.request.contextPath}/">Trang chủ</a></li>
    <li><a href="${pageContext.request.contextPath}/">Tất cả sách</a></li>
    <li><a href="${pageContext.request.contextPath}/">Giới thiệu</a></li>
    <li><a href="${pageContext.request.contextPath}/">Liên hệ</a></li>
    <li class="navbar__search-item">
      <form action="${pageContext.request.contextPath}/books" method="get">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="action" value="search"/>
        <input type="text" name="keyword" placeholder="Tìm sách..." autocomplete="off"/>
        <button type="submit">
          <img src="${pageContext.request.contextPath}/images/magnifying-glass.png"
               width="30" height="30" alt="search"/>
        </button>
      </form>
    </li>
    <c:if test="${sessionScope.currentUser.role == 'admin'}">
      <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dành cho quản trị viên</a></li>
    </c:if>
  </ul>
  <div class="navbar__action">
    <a href="${pageContext.request.contextPath}/cart" class="btn-cart">
      <img src="${pageContext.request.contextPath}/images/online-shopping.png"
           width="30" height="30" alt="cart"/>
      <c:if test="${sessionScope.cartCount > 0}">
        <span class="cart-count">${sessionScope.cartCount}</span>
      </c:if>
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
              <i class="bi bi-person-circle"></i> Tài khoản của tôi
            </a>
            <a href="${pageContext.request.contextPath}/orders" class="user-dropdown__item">
              <i class="bi bi-bag-check"></i> Đơn mua
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
        <a href="javascript:void(0)" onclick="openModal('login')" class="btn-link">Tài khoản</a>
      </c:otherwise>
    </c:choose>
  </div>
</nav>

<%-- ── LAYOUT ── --%>
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
           class="profile-nav-item">Hồ sơ</a>
        <a href="${pageContext.request.contextPath}/profile?action=password"
           class="profile-nav-item">Đổi mật khẩu</a>
        <a href="${pageContext.request.contextPath}/profile/addresses"
           class="profile-nav-item active">Địa chỉ</a>
      </div>
      <a href="${pageContext.request.contextPath}/orders" class="profile-nav-group-link">
        <i class="bi bi-bag-check"></i> Đơn mua
      </a>
    </nav>
  </div>

  <%-- MAIN CONTENT --%>
  <div class="profile-content">

    <%-- Alerts từ session --%>
    <c:if test="${not empty sessionScope.successMsg}">
      <div class="profile-alert profile-alert--ok">✅ ${sessionScope.successMsg}</div>
      <c:remove var="successMsg" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMsg}">
      <div class="profile-alert profile-alert--err">❌ ${sessionScope.errorMsg}</div>
      <c:remove var="errorMsg" scope="session"/>
    </c:if>

    <div class="addr-form-wrap">

      <div class="addr-form-title">
        <c:choose>
          <c:when test="${editMode}">Cập nhật địa chỉ</c:when>
          <c:otherwise>Thêm địa chỉ mới</c:otherwise>
        </c:choose>
      </div>
      <div class="addr-form-sub">
        Vui lòng điền đầy đủ thông tin để đảm bảo giao hàng chính xác.
      </div>

      <%-- Form --%>
      <form method="post" action="${pageContext.request.contextPath}/address"
            id="addressForm" onsubmit="return validateForm()">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <%-- Nếu sửa: truyền action=edit và addressId --%>
        <c:if test="${editMode}">
          <input type="hidden" name="action" value="edit"/>
          <input type="hidden" name="addressId" value="${address.addressId}"/>
        </c:if>

        <%-- Họ và tên + Số điện thoại --%>
        <div class="addr-row-2col">
          <div class="addr-row">
            <label for="fullName">Họ và tên <span style="color:#cc0000;">*</span></label>
            <input type="text" id="fullName" name="fullName"
                   placeholder="Nguyễn Văn A"
                   value="${editMode ? address.fullName : ''}"
                   maxlength="100"/>
            <span class="field-error" id="err-fullName"></span>
          </div>
          <div class="addr-row">
            <label for="phone">Số điện thoại <span style="color:#cc0000;">*</span></label>
            <input type="tel" id="phone" name="phone"
                   placeholder="09xxxxxxxx"
                   value="${editMode ? address.phone : ''}"
                   maxlength="15"/>
            <span class="field-error" id="err-phone"></span>
          </div>
        </div>

        <%-- Địa chỉ cụ thể --%>
        <div class="addr-row">
          <label for="addressLine">Số nhà, tên đường <span style="color:#cc0000;">*</span></label>
          <input type="text" id="addressLine" name="addressLine"
                 placeholder="VD: 123 Đường Lê Lợi"
                 value="${editMode ? address.addressLine : ''}"
                 maxlength="255"/>
          <span class="field-error" id="err-addressLine"></span>
        </div>

        <%-- Phường / xã + Quận / huyện --%>
        <div class="addr-row-2col">
          <div class="addr-row">
            <label for="ward">Phường / Xã <span style="color:#cc0000;">*</span></label>
            <input type="text" id="ward" name="ward"
                   placeholder="VD: Phường Bến Nghé"
                   value="${editMode ? address.ward : ''}"
                   maxlength="100"/>
            <span class="field-error" id="err-ward"></span>
          </div>
          <div class="addr-row">
            <label for="district">Quận / Huyện <span style="color:#cc0000;">*</span></label>
            <input type="text" id="district" name="district"
                   placeholder="VD: Quận 1"
                   value="${editMode ? address.district : ''}"
                   maxlength="100"/>
            <span class="field-error" id="err-district"></span>
          </div>
        </div>

        <%-- Tỉnh / Thành phố --%>
        <div class="addr-row">
          <label for="city">Tỉnh / Thành phố <span style="color:#cc0000;">*</span></label>
          <input type="text" id="city" name="city"
                 placeholder="VD: TP. Hồ Chí Minh"
                 value="${editMode ? address.city : ''}"
                 maxlength="100"/>
          <span class="field-error" id="err-city"></span>
        </div>

        <%-- Thiết lập mặc định --%>
        <div class="addr-default-row">
          <input type="checkbox" id="isDefault" name="isDefault"
                 ${editMode && address.isDefault == true ? 'checked' : ''}/>
          <label for="isDefault">
            Đặt làm địa chỉ mặc định
          </label>
        </div>

        <%-- Actions --%>
        <div class="addr-form-actions">
          <a href="${pageContext.request.contextPath}/profile/addresses"
             class="btn-addr-cancel">
            <i class="bi bi-arrow-left" style="margin-right:5px;"></i>Trở về
          </a>
          <button type="submit" class="btn-addr-save">
            <i class="bi bi-check-lg" style="margin-right:5px;"></i>
            <c:choose>
              <c:when test="${editMode}">Cập nhật địa chỉ</c:when>
              <c:otherwise>Lưu địa chỉ</c:otherwise>
            </c:choose>
          </button>
        </div>

      </form>
    </div>
  </div>
</div>

<footer>
  <p>&copy; 2024 Nhà Sách Online. All rights reserved.</p>
</footer>

<script>
/* ── Dropdown ── */
(function () {
  const trigger = document.querySelector('.user-dropdown__trigger');
  const menu    = document.querySelector('.user-dropdown__menu');
  if (!trigger || !menu) return;
  trigger.addEventListener('click', function (e) { e.stopPropagation(); menu.classList.toggle('open'); });
  document.addEventListener('click', function () { menu.classList.remove('open'); });
  menu.addEventListener('click', function (e) { e.stopPropagation(); });
})();

/* ── Form validation ── */
function validateForm() {
  const fields = ['fullName', 'phone', 'addressLine', 'ward', 'district', 'city'];
  const labels = {
    fullName: 'Họ và tên',
    phone: 'Số điện thoại',
    addressLine: 'Địa chỉ cụ thể',
    ward: 'Phường / Xã',
    district: 'Quận / Huyện',
    city: 'Tỉnh / Thành phố'
  };
  let valid = true;

  // Reset errors
  fields.forEach(id => {
    document.getElementById(id).classList.remove('error');
    document.getElementById('err-' + id).textContent = '';
  });

  fields.forEach(id => {
    const input = document.getElementById(id);
    if (!input.value.trim()) {
      input.classList.add('error');
      document.getElementById('err-' + id).textContent = labels[id] + ' không được để trống.';
      valid = false;
    }
  });

  // Validate phone format (VN: 10 digits, starts with 0)
  const phone = document.getElementById('phone').value.trim();
  if (phone && !/^(0[3|5|7|8|9])+([0-9]{8})$/.test(phone)) {
    document.getElementById('phone').classList.add('error');
    document.getElementById('err-phone').textContent = 'Số điện thoại không hợp lệ (VD: 0912345678).';
    valid = false;
  }

  return valid;
}

/* Remove error highlight on input */
document.querySelectorAll('.addr-row input').forEach(function (input) {
  input.addEventListener('input', function () {
    this.classList.remove('error');
    const errEl = document.getElementById('err-' + this.id);
    if (errEl) errEl.textContent = '';
  });
});
</script>
</body>
</html>
