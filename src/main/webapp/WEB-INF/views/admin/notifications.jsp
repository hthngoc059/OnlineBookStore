<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý thông báo - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
    <div class="admin-container">
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="notifications"/>
        </jsp:include>

        <main class="main-content">
            <header class="main-header">
                <div class="header-title"> WEB-INF
                    <h1>Quản lý thông báo</h1>
                    <p>Gửi thông báo đến người dùng</p>
                </div>
            </header>

            <!-- Hiển thị thông báo kết quả -->
            <c:if test="${not empty notifySuccess}">
                <div class="alert-card" style="border-left-color: #2e7d32; background: #e8f5e9; margin-bottom: 20px;">
                    <div class="alert-icon">✅</div>
                    <div class="alert-content"><h4 style="color:#2e7d32;">${notifySuccess}</h4></div>
                </div>
                <% session.removeAttribute("notifySuccess"); %>
            </c:if>
            <c:if test="${not empty notifyError}">
                <div class="alert-card warning" style="margin-bottom: 20px;">
                    <div class="alert-icon">❌</div>
                    <div class="alert-content"><h4>${notifyError}</h4></div>
                </div>
                <% session.removeAttribute("notifyError"); %>
            </c:if>

            <!-- Send Notification Form -->
            <div class="form-container" style="margin-bottom: 32px;">
                <form action="${pageContext.request.contextPath}/admin/notifications" method="post">
                    <input type="hidden" name="action" value="send">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="form-group">
                        <label>Gửi đến</label>
                        <select name="target">
                            <option value="all">📢 Tất cả người dùng</option>
                            <option value="users">👤 Chỉ người dùng (User)</option>
                            <option value="admins">👑 Chỉ quản trị viên (Admin)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Tiêu đề thông báo</label>
                        <input type="text" name="title" placeholder="Ví dụ: Flash sale cuối tuần!" required>
                    </div>

                    <div class="form-group">
                        <label>Nội dung</label>
                        <textarea name="message" rows="4" placeholder="Nội dung thông báo..." required></textarea>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-primary">📢 Gửi thông báo</button>
                    </div>
                </form>
            </div>

            <!-- Notification History -->
            <div class="data-table-container">
                <div class="table-header"><h3>📜 Lịch sử thông báo</h3></div>
                <table class="data-table">
                    <thead>
                        <tr><th>ID</th><th>Người nhận</th><th>Tiêu đề</th><th>Nội dung</th><th>Ngày gửi</th><th>Trạng thái</th><th>Thao tác</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="notif" items="${notifications}">
                            <tr>
                                <td>${notif.notificationId}</td>
                                <td>
                                    <strong>${notif.user.username}</strong><br>
                                    <small>${notif.user.email}</small>
                                </td>
                                <td><strong>${notif.title}</strong></td>
                                <td>${notif.message}</td>
                                <td><fmt:formatDate value="${notif.createdAt}" pattern="dd/MM/yyyy HH:mm"/>}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${notif.isRead}">
                                            <span class="stock-badge ok">✅ Đã đọc</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="stock-badge low">⏳ Chưa đọc</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="javascript:void(0)" onclick="confirmDelete(${notif.notificationId})" class="btn-icon btn-danger">🗑️ Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty notifications}">
                            <tr><td colspan="7" style="text-align:center;">Chưa có thông báo nào</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </main>
    </div>

    <script>
        function confirmDelete(id) {
            if(confirm('Bạn có chắc muốn xóa thông báo này?')) {
                window.location.href = '${pageContext.request.contextPath}/admin/notifications?action=delete&id=' + id;
            }
        }
    </script>
</body>
</html>