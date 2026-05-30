<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sửa mã giảm giá - Admin</title>
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
                    <h1>Sửa mã giảm giá</h1>
                    <p>Cập nhật thông tin mã giảm giá</p>
                </div>
                <a href="${pageContext.request.contextPath}/admin/discounts" class="btn-secondary">← Quay lại</a>
            </header>

            <div class="form-container">
                <form action="${pageContext.request.contextPath}/admin/discounts" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="discountId" value="${discount.discountId}">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Mã code</label>
                            <input type="text" value="${discount.code}" readonly disabled style="background:#f5f5f5;">
                            <small>Không thể sửa mã code</small>
                        </div>
                        <div class="form-group">
                            <label>Mô tả</label>
                            <input type="text" name="description" value="${discount.description}">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Loại giảm giá <span class="required">*</span></label>
                            <select name="discountType" required>
                                <option value="percent" ${discount.discountType == 'percent' ? 'selected' : ''}>🎯 Phần trăm (%)</option>
                                <option value="fixed" ${discount.discountType == 'fixed' ? 'selected' : ''}>💰 Cố định (VNĐ)</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Giá trị giảm <span class="required">*</span></label>
                            <input type="number" name="discountValue" step="1" value="${discount.discountValueFormatted}" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Ngày bắt đầu <span class="required">*</span></label>
                            <input type="datetime-local" name="startDate" value="${discount.startDate.toString().replace('T', 'T').substring(0,16)}" required>
                        </div>
                        <div class="form-group">
                            <label>Ngày kết thúc <span class="required">*</span></label>
                            <input type="datetime-local" name="endDate" value="${discount.endDate.toString().replace('T', 'T').substring(0,16)}" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Số lần sử dụng tối đa</label>
                        <input type="number" name="maxUsage" value="${discount.maxUsage}" placeholder="Để trống nếu không giới hạn" min="1">
                    </div>

                    <div class="form-group">
                        <label>Trạng thái</label>
                        <select name="isActive">
                            <option value="true" ${discount.isActive ? 'selected' : ''}>🟢 Hoạt động</option>
                            <option value="false" ${!discount.isActive ? 'selected' : ''}>⚫ Vô hiệu</option>
                        </select>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/admin/discounts" class="btn-secondary">Hủy</a>
                        <button type="submit" class="btn-primary">💾 Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</body>
</html>