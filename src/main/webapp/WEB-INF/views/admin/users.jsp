<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý người dùng - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
    <div class="admin-container">
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="users"/>
        </jsp:include>

        <main class="main-content">
            <header class="main-header">
                <div class="header-title">
                    <h1>Quản lý người dùng</h1>
                    <p>Xem và quản lý thông tin người dùng hệ thống</p>
                </div>
            </header>

            <!-- Filter Bar -->
            <div class="filter-bar">
                <form action="${pageContext.request.contextPath}/admin/users" method="get" class="search-form">
                    <input type="text" name="keyword" placeholder="Tìm theo tên, email..." value="${param.keyword}">
                    <select name="role">
                        <option value="">Tất cả vai trò</option>
                        <option value="user" ${param.role == 'user' ? 'selected' : ''}>Người dùng</option>
                        <option value="admin" ${param.role == 'admin' ? 'selected' : ''}>Quản trị viên</option>
                    </select>
                    <button type="submit" class="btn-secondary">🔍 Tìm kiếm</button>
                </form>
            </div>

            <!-- Users Table -->
            <div class="data-table-container">
                <table class="data-table">
                    <thead>
                        <tr><th>ID</th><th>Tên đăng nhập</th><th>Email</th><th>Số điện thoại</th><th>Vai trò</th><th>Ngày đăng ký</th><th>Thao tác</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>${user.userId}</td>
                                <td>${user.username}</td>
                                <td>${user.email}</td>
                                <td>${user.phoneNumber}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${user.role == 'admin'}"><span class="status-badge confirmed">Admin</span></c:when>
                                        <c:otherwise><span class="status-badge pending">User</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td><fmt:formatDate value="${user.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm" /></td>
                                <td>
                                    <a href="javascript:void(0)" onclick="confirmDelete(${user.userId})" class="btn-icon btn-danger">🗑️ Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty users}">
                            <tr><td colspan="7" style="text-align:center;">Không có người dùng nào</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 0}"><a href="?page=${currentPage-1}&keyword=${param.keyword}&role=${param.role}">← Trước</a></c:if>
                    <span>Trang ${currentPage+1} / ${totalPages}</span>
                    <c:if test="${currentPage+1 < totalPages}"><a href="?page=${currentPage+1}&keyword=${param.keyword}&role=${param.role}">Sau →</a></c:if>
                </div>
            </c:if>
        </main>
    </div>

    <script>
        function confirmDelete(userId) {
            if(confirm('Bạn có chắc muốn xóa người dùng này? Hành động này không thể hoàn tác.')) {
                window.location.href = '${pageContext.request.contextPath}/admin/users?action=delete&id=' + userId;
            }
        }
    </script>
</body>
</html>