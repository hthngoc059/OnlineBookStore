<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý đơn hàng - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
    <div class="admin-container">
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="orders"/>
        </jsp:include>

        <main class="main-content">
            <header class="main-header">
                <div class="header-title">
                    <h1>Quản lý đơn hàng</h1>
                    <p>Xem và cập nhật trạng thái đơn hàng</p>
                </div>
            </header>

            <!-- Filter Bar - THÊM METHOD GET ĐỂ SEARCH HOẠT ĐỘNG -->
            <div class="filter-bar">
                <form action="${pageContext.request.contextPath}/admin/orders" method="get" class="search-form" ">
                    <input type="text" name="keyword" placeholder="Tìm theo mã đơn hoặc tên khách hàng..." 
                           value="${param.keyword}">
                    <select name="status">
                        <option value="">Tất cả trạng thái</option>
                        <option value="pending" ${param.status == 'pending' ? 'selected' : ''}>Chờ xác nhận</option>
                        <option value="confirmed" ${param.status == 'confirmed' ? 'selected' : ''}>Đã xác nhận</option>
                        <option value="shipping" ${param.status == 'shipping' ? 'selected' : ''}>Đang giao</option>
                        <option value="delivered" ${param.status == 'delivered' ? 'selected' : ''}>Đã giao</option>
                        <option value="cancelled" ${param.status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                    </select>
                    <button type="submit" class="btn-secondary">🔍 Tìm kiếm</button>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="btn-secondary">🔄 Xóa lọc</a>
                </form>
            </div>

            <!-- Orders Table -->
            <div class="data-table-container">
                <table class="data-table">
                    <thead>
                        <tr><th>Mã đơn</th><th>Khách hàng</th><th>Ngày đặt</th><th>Tổng tiền</th><th>Trạng thái</th><th>Thanh toán</th><th>Thao tác</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orders}">
                            <tr>
                                <td>#${order.orderId}</td>
                                <td>
                                    ${not empty order.user ? order.user.username : 'N/A'} 
                                    <br><small>${not empty order.user ? order.user.email : 'N/A'}</small>
                                </td>
                                <td>
                                    ${order.formattedOrderDate}
                                </td>
                                <td>${order.finalAmountFormatted} đ</td>
                                <td><span class="status-badge ${order.status}">
                                    <c:choose>
                                        <c:when test="${order.status == 'pending'}">Chờ xác nhận</c:when>
                                        <c:when test="${order.status == 'confirmed'}">Đã xác nhận</c:when>
                                        <c:when test="${order.status == 'shipping'}">Đang giao</c:when>
                                        <c:when test="${order.status == 'delivered'}">Đã giao</c:when>
                                        <c:when test="${order.status == 'cancelled'}">Đã hủy</c:when>
                                    </c:choose>
                                </span></td>
                                <td><span class="payment-badge ${order.paymentStatus}">
                                    <c:choose>
                                        <c:when test="${order.paymentStatus == 'unpaid'}">Chưa TT</c:when>
                                        <c:when test="${order.paymentStatus == 'paid'}">Đã TT</c:when>
                                        <c:when test="${order.paymentStatus == 'refunded'}">Hoàn tiền</c:when>
                                    </c:choose>
                                </span></td>
                                <td><a href="${pageContext.request.contextPath}/admin/orders?action=detail&id=${order.orderId}" class="btn-icon">Chi tiết</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty orders}">
                            <tr><td colspan="7" style="text-align:center;">Không có đơn hàng nào</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination - THÊM GIỮ LẠI PARAMS -->
            <c:if test="${totalPages > 1}">
                <div class="pagination" style="display: flex; justify-content: center; gap: 10px; margin-top: 20px;">
                    <c:if test="${currentPage > 0}">
                        <a href="?page=${currentPage-1}&status=${param.status}&keyword=${param.keyword}" class="btn-secondary">← Trước</a>
                    </c:if>
                    <span>Trang ${currentPage+1} / ${totalPages}</span>
                    <c:if test="${currentPage+1 < totalPages}">
                        <a href="?page=${currentPage+1}&status=${param.status}&keyword=${param.keyword}" class="btn-secondary">Sau →</a>
                    </c:if>
                </div>
            </c:if>
        </main>
    </div>
</body>
</html>