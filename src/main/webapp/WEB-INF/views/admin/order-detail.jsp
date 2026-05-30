<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chi tiết đơn hàng #${order.orderId} - Admin</title>
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
                    <h1>Chi tiết đơn hàng #${order.orderId}</h1>
                    <p>Xem và cập nhật trạng thái đơn hàng</p>
                </div>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn-secondary">← Quay lại</a>
            </header>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
                <!-- Customer Info -->
                <div class="data-table-container">
                    <div class="table-header"><h3>👤 Thông tin khách hàng</h3></div>
                    <table class="data-table">
                        <tr><th style="width:140px">Họ tên</th><td>${order.address.fullName}</td></tr>
                        <tr><th>Email</th><td>${order.user.email}</td></tr>
                        <tr><th>Số điện thoại</th><td>${order.address.phone}</td></tr>
                        <tr><th>Ngày đặt</th><td>${order.orderDate}</td></tr>
                    </table>
                </div>

                <!-- Shipping Address -->
                <div class="data-table-container">
                    <div class="table-header"><h3>📦 Địa chỉ giao hàng</h3></div>
                    <table class="data-table">
                        <tr><th style="width:140px">Người nhận</th><td>${order.address.fullName}</td></tr>
                        <tr><th>Địa chỉ</th><td>${order.address.addressLine}, ${order.address.ward}, ${order.address.district}, ${order.address.city}</td></tr>
                        <tr><th>Số điện thoại</th><td>${order.address.phone}</td></tr>
                    </table>
                </div>
            </div>

            <!-- Order Items -->
            <div class="data-table-container" style="margin-top: 24px;">
                <div class="table-header"><h3>📚 Sản phẩm đã đặt</h3></div>
                <table class="data-table">
                    <thead><tr><th>Sản phẩm</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th></tr></thead>
                    <tbody>
                        <c:forEach var="item" items="${orderItems}">
                            <tr>
                                <td><strong>${item.book.title}</strong><br><small>${item.book.author}</small></td>
                                <td><fmt:formatNumber value="${item.priceAtTime}" type="number" groupingUsed="true"/> ₫</td>
                                <td>${item.quantity}</td>
                                <td><fmt:formatNumber value="${item.priceAtTime * item.quantity}" type="number" groupingUsed="true"/> ₫</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Order Summary & Status Update -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-top: 24px;">
                <div class="data-table-container">
                    <div class="table-header"><h3>💰 Tổng kết đơn hàng</h3></div>
                    <table class="data-table">
                        <tr><th style="width:160px">Tạm tính</th><td><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/> ₫</td></tr>
                        <tr><th>Giảm giá</th><td>- <fmt:formatNumber value="${order.discountAmount}" type="number" groupingUsed="true"/> ₫</td></tr>
                        <tr><th>Thành tiền</th><td><strong style="color:#405a28; font-size:1.1rem;"><fmt:formatNumber value="${order.finalAmount}" type="number" groupingUsed="true"/> ₫</strong></td></tr>
                        <tr><th>Phương thức TT</th><td>
                            <c:choose>
                                <c:when test="${payment.paymentMethod == 'cod'}">💰 Thanh toán khi nhận hàng (COD)</c:when>
                                <c:when test="${payment.paymentMethod == 'banking'}">🏦 Chuyển khoản ngân hàng</c:when>
                                <c:when test="${payment.paymentMethod == 'momo'}">📱 Ví Momo</c:when>
                                <c:otherwise>${payment.paymentMethod}</c:otherwise>
                            </c:choose>
                        </td></tr>
                        <tr><th>Trạng thái TT</th><td><span class="payment-badge ${order.paymentStatus}">${order.paymentStatus}</span></td></tr>
                    </table>
                </div>

                <!-- Update Status -->
                <div class="data-table-container">
                    <div class="table-header"><h3>🔄 Cập nhật trạng thái</h3></div>
                    <div style="padding: 20px;">
                        <form action="${pageContext.request.contextPath}/admin/orders" method="post">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <div class="form-group">
                                <label>Trạng thái đơn hàng</label>
                                <select name="status" class="form-control">
                                    <option value="pending" ${order.status == 'pending' ? 'selected' : ''}>⏳ Chờ xác nhận</option>
                                    <option value="confirmed" ${order.status == 'confirmed' ? 'selected' : ''}>✅ Đã xác nhận</option>
                                    <option value="shipping" ${order.status == 'shipping' ? 'selected' : ''}>🚚 Đang giao hàng</option>
                                    <option value="delivered" ${order.status == 'delivered' ? 'selected' : ''}>📦 Đã giao thành công</option>
                                    <option value="cancelled" ${order.status == 'cancelled' ? 'selected' : ''}>❌ Đã hủy</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Trạng thái thanh toán</label>
                                <select name="paymentStatus" class="form-control">
                                    <option value="unpaid" ${order.paymentStatus == 'unpaid' ? 'selected' : ''}>Chưa thanh toán</option>
                                    <option value="paid" ${order.paymentStatus == 'paid' ? 'selected' : ''}>Đã thanh toán</option>
                                    <option value="refunded" ${order.paymentStatus == 'refunded' ? 'selected' : ''}>Đã hoàn tiền</option>
                                </select>
                            </div>
                            <div class="form-actions" style="margin-top: 20px; padding-top: 0; border-top: none;">
                                <button type="submit" class="btn-primary">💾 Cập nhật</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>