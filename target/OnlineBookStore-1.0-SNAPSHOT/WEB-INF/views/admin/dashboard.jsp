<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Nhà Sách Online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
    <div class="admin-container">
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="dashboard"/>
        </jsp:include>

        <main class="main-content">
            <header class="main-header">
                <div class="header-title">
                    <h1>Tổng quan</h1>
                    <p>Chào mừng, <strong>${sessionScope.currentUser.username}</strong> | <span id="currentDateTime"></span></p>
                </div>
                <div class="notification-bell">
                    <a href="${pageContext.request.contextPath}/admin/notifications" class="btn-icon">
                        🔔 <c:if test="${unreadCount > 0}"><span class="notification-badge">${unreadCount}</span></c:if>
                    </a>
                </div>
            </header>

            <!-- Stat Cards -->
            <div class="stat-grid">
                <div class="stat-card">
                    <div class="stat-icon green">💰</div>
                    <div class="stat-info">
                        <h3>Tổng doanh thu</h3>
                        <p class="stat-value"><fmt:formatNumber value="${totalRevenue}" type="number" groupingUsed="true"/> ₫</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon blue">📦</div>
                    <div class="stat-info">
                        <h3>Tổng đơn hàng</h3>
                        <p class="stat-value">${totalOrders}</p>
                        <c:if test="${pendingOrders > 0}"><span class="stat-trend">${pendingOrders} đơn chờ xử lý</span></c:if>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon orange">👥</div>
                    <div class="stat-info">
                        <h3>Người dùng</h3>
                        <p class="stat-value">${totalUsers}</p>
                        <c:if test="${newUsersThisMonth > 0}"><span class="stat-trend up">+${newUsersThisMonth} mới trong tháng</span></c:if>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon purple">📚</div>
                    <div class="stat-info">
                        <h3>Sách trong kho</h3>
                        <p class="stat-value">${totalBooks}</p>
                        <c:if test="${lowStockBooks > 0}"><span class="stat-trend down">${lowStockBooks} sách sắp hết</span></c:if>
                    </div>
                </div>
            </div>

            <!-- Recent Orders -->
            <div class="data-table-container">
                <div class="table-header">
                    <h3>📦 Đơn hàng gần đây</h3>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="btn-secondary">Xem tất cả →</a>
                </div>
                <table class="data-table">
                    <thead>
                        <tr><th>Mã đơn</th><th>Khách hàng</th><th>Ngày đặt</th><th>Tổng tiền</th><th>Trạng thái</th><th>Thao tác</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${recentOrders}">
                            <tr>
                                <td>#${order.orderId}</td>
                                <td>${order.user.username}</td>
                                <td>${order.orderDate.toLocalDate()}</td>
                                <td><fmt:formatNumber value="${order.finalAmount}" type="number" groupingUsed="true"/> ₫</td>
                                <td><span class="status-badge ${order.status}">
                                    <c:choose>
                                        <c:when test="${order.status == 'pending'}">Chờ xác nhận</c:when>
                                        <c:when test="${order.status == 'confirmed'}">Đã xác nhận</c:when>
                                        <c:when test="${order.status == 'shipping'}">Đang giao</c:when>
                                        <c:when test="${order.status == 'delivered'}">Đã giao</c:when>
                                        <c:otherwise>${order.status}</c:otherwise>
                                    </c:choose>
                                </span></td>
                                <td><a href="${pageContext.request.contextPath}/admin/orders?action=detail&id=${order.orderId}" class="btn-icon">👁️ Chi tiết</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recentOrders}">
                            <tr><td colspan="6" style="text-align:center;">Chưa có đơn hàng nào</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Low Stock Alert -->
            <c:if test="${not empty lowStockBooksList}">
                <div class="alert-card warning">
                    <div class="alert-icon">⚠️</div>
                    <div class="alert-content">
                        <h4>Cảnh báo tồn kho thấp</h4>
                        <ul>
                            <c:forEach var="book" items="${lowStockBooksList}">
                                <li>📖 ${book.title} - Còn ${book.stockQuantity} bản</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </c:if>
        </main>
    </div>

    <script>
        function updateDateTime() {
            const now = new Date();
            document.getElementById('currentDateTime').textContent = now.toLocaleString('vi-VN');
        }
        updateDateTime();
        setInterval(updateDateTime, 1000);
    </script>
</body>
</html>