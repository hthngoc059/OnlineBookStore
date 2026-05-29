<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thêm mã giảm giá - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
    <div class="admin-container">
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="discounts"/>
        </jsp:include>

        <main class="main-content">
            <header class="main-header">
                <div class="header-title">
                    <h1>Thêm mã giảm giá</h1>
                    <p>Tạo mã giảm giá mới cho khách hàng</p>
                </div>
                <a href="${pageContext.request.contextPath}/admin/discounts" class="btn-secondary">← Quay lại</a>
            </header>

            <div class="form-container">
                <form action="${pageContext.request.contextPath}/admin/discounts" method="post">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Mã code <span class="required">*</span></label>
                            <input type="text" name="code" placeholder="SUMMER2024" required>
                            <small style="color:#888;">Chỉ gồm chữ hoa, số, không dấu cách, ví dụ: SALE20</small>
                        </div>
                        <div class="form-group">
                            <label>Mô tả</label>
                            <input type="text" name="description" placeholder="Giảm giá mùa hè...">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Loại giảm giá <span class="required">*</span></label>
                            <select name="discountType" required>
                                <option value="percent">🎯 Phần trăm (%)</option>
                                <option value="fixed">💰 Cố định (VNĐ)</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Giá trị giảm <span class="required">*</span></label>
                            <input type="number" name="discountValue" step="1000" required>
                            <small>Nếu là %, nhập số (ví dụ: 10). Nếu là tiền, nhập số VNĐ</small>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Ngày bắt đầu <span class="required">*</span></label>
                            <input type="datetime-local" name="startDate" required>
                        </div>
                        <div class="form-group">
                            <label>Ngày kết thúc <span class="required">*</span></label>
                            <input type="datetime-local" name="endDate" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Số lần sử dụng tối đa</label>
                        <input type="number" name="maxUsage" placeholder="Để trống nếu không giới hạn" min="1">
                        <small>Để trống nếu muốn mã giảm giá có thể dùng không giới hạn số lần</small>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/admin/discounts" class="btn-secondary">Hủy</a>
                        <button type="submit" class="btn-primary">🏷️ Thêm mã giảm giá</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</body>
</html>