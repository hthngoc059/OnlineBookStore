<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý mã giảm giá - Admin</title>
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
                    <h1>Quản lý mã giảm giá</h1>
                    <p>Thêm, sửa, xóa mã giảm giá cho khách hàng</p>
                </div>
                <a href="${pageContext.request.contextPath}/admin/discounts?action=add" class="btn-primary">+ Thêm mã giảm giá</a>
            </header>

            <!-- Hiển thị thông báo -->
            <c:if test="${not empty successMsg}">
                <div class="alert-card" style="border-left-color: #2e7d32; background: #e8f5e9; margin-bottom: 20px;">
                    <div class="alert-icon">✅</div>
                    <div class="alert-content"><h4 style="color:#2e7d32;">${successMsg}</h4></div>
                </div>
                <% session.removeAttribute("successMsg"); %>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="alert-card warning" style="margin-bottom: 20px;">
                    <div class="alert-icon">❌</div>
                    <div class="alert-content"><h4>${errorMsg}</h4></div>
                </div>
                <% session.removeAttribute("errorMsg"); %>
            </c:if>

            <!-- Discounts Table -->
            <div class="data-table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Mã code</th>
                            <th>Mô tả</th>
                            <th>Loại</th>
                            <th>Giá trị</th>
                            <th>Ngày bắt đầu</th>
                            <th>Ngày kết thúc</th>
                            <th>Đã dùng</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="discount" items="${discounts}">
                            <tr>
                                <td>${discount.discountId}</td>
                                <td><strong>${discount.code}</strong></td>
                                <td>${discount.description}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${discount.discountType == 'percent'}">🎯 Phần trăm</c:when>
                                        <c:otherwise>💰 Cố định</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${discount.discountType == 'percent'}">
                                            ${discount.discountValue}%
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${discount.discountValue}" type="number" groupingUsed="true"/> ₫
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${discount.startDate.toLocalDate()} <br><small>${discount.startDate.toLocalTime().toString().substring(0,5)}</small></td>
                                <td>${discount.endDate.toLocalDate()} <br><small>${discount.endDate.toLocalTime().toString().substring(0,5)}</small></td>
                                <td>${discount.usedCount} / ${discount.maxUsage != null ? discount.maxUsage : '∞'}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${discount.isActive and discount.endDate > now}">
                                            <span class="stock-badge ok">🟢 Hoạt động</span>
                                        </c:when>
                                        <c:when test="${!discount.isActive}">
                                            <span class="stock-badge out">⚫ Vô hiệu</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="stock-badge out">🔴 Hết hạn</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/discounts?action=edit&id=${discount.discountId}" class="btn-icon">✏️ Sửa</a>
                                    <a href="${pageContext.request.contextPath}/admin/discounts?action=toggle&id=${discount.discountId}" class="btn-icon ${discount.isActive ? 'btn-danger' : 'btn-primary'}">
                                        ${discount.isActive ? '🔘 Tắt' : '⚡ Bật'}
                                    </a>
                                    <a href="javascript:void(0)" onclick="confirmDelete(${discount.discountId})" class="btn-icon btn-danger">🗑️ Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty discounts}">
                            <tr><td colspan="10" style="text-align:center;">Không có mã giảm giá nào</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 0}">
                        <a href="?page=${currentPage-1}">← Trước</a>
                    </c:if>
                    <span>Trang ${currentPage+1} / ${totalPages}</span>
                    <c:if test="${currentPage+1 < totalPages}">
                        <a href="?page=${currentPage+1}">Sau →</a>
                    </c:if>
                </div>
            </c:if>
        </main>
    </div>

    <script>
        function confirmDelete(id) {
            if(confirm('Bạn có chắc muốn xóa mã giảm giá này?')) {
                window.location.href = '${pageContext.request.contextPath}/admin/discounts?action=delete&id=' + id;
            }
        }
    </script>
</body>
</html>