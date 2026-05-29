<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm địa chỉ - Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <nav class="navbar">
        <h1><img src="${pageContext.request.contextPath}/images/Logo.png" width="125" height="125"></h1>
        <ul class="navbar__nav">
            <li><a href="${pageContext.request.contextPath}/">Trang chủ</a></li>
            <li><a href="${pageContext.request.contextPath}/books">Tất cả sách</a></li>
            <li><a href="${pageContext.request.contextPath}/checkout" class="btn-link">← Quay lại thanh toán</a></li>
        </ul>
        <div class="navbar__action">
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
                    <a href="${pageContext.request.contextPath}/books" class="btn-link">Tài khoản</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>

    <div class="address-form-container">
        <div class="address-form-card">
            <h2 class="address-form-title">📌 Thêm địa chỉ mới</h2>
            
            <form method="post" action="${pageContext.request.contextPath}/profile/addresses" class="address-form">
                <input type="hidden" 
           name="${_csrf.parameterName}" 
           value="${_csrf.token}"/>
                <input type="hidden" name="action" value="add">
                
                <div class="address-form-group">
                    <label for="fullName">Họ tên <span class="required">*</span></label>
                    <input type="text" id="fullName" name="fullName" placeholder="Nguyễn Văn A" required>
                </div>
                
                <div class="address-form-group">
                    <label for="phone">Số điện thoại <span class="required">*</span></label>
                    <input type="tel" id="phone" name="phone" placeholder="0901234567" required>
                </div>
                
                <div class="address-form-group">
                    <label for="addressLine">Địa chỉ <span class="required">*</span></label>
                    <input type="text" id="addressLine" name="addressLine" placeholder="Số nhà, tên đường" required>
                </div>
                
                <div class="address-form-row">
                    <div class="address-form-group">
                        <label for="ward">Phường/Xã</label>
                        <input type="text" id="ward" name="ward" placeholder="Phường/Xã">
                    </div>
                    <div class="address-form-group">
                        <label for="district">Quận/Huyện</label>
                        <input type="text" id="district" name="district" placeholder="Quận/Huyện">
                    </div>
                </div>
                
                <div class="address-form-group">
                    <label for="city">Tỉnh/Thành phố <span class="required">*</span></label>
                    <input type="text" id="city" name="city" placeholder="TP. Hồ Chí Minh" required>
                </div>
                
                <div class="address-form-checkbox">
                    <input type="checkbox" id="isDefault" name="isDefault" value="true">
                    <label for="isDefault">Đặt làm địa chỉ mặc định</label>
                </div>
                
                <div class="address-form-actions">
                    <a href="${pageContext.request.contextPath}/checkout" class="btn-cancel">Hủy</a>
                    <button type="submit" class="btn-save-address">💾 Lưu địa chỉ</button>
                </div>
            </form>
        </div>
    </div>

    <footer>
        <p>&copy; 2024 Nhà Sách Online. All rights reserved.</p>
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