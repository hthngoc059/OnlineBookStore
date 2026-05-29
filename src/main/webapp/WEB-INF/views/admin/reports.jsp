<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Báo cáo thống kê - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <div class="admin-container">
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="reports"/>
        </jsp:include>

        <main class="main-content">
            <header class="main-header">
                <div class="header-title">
                    <h1>Báo cáo thống kê</h1>
                    <p>Phân tích doanh thu và hiệu suất bán hàng</p>
                </div>
            </header>

            <!-- Date Range Filter -->
            <div class="filter-bar">
                <form action="${pageContext.request.contextPath}/admin/reports" method="get" class="search-form">
                    <input type="date" name="fromDate" value="${fromDate}">
                    <input type="date" name="toDate" value="${toDate}">
                    <button type="submit" class="btn-secondary">📊 Lọc báo cáo</button>
                </form>
            </div>

            <!-- Revenue Chart -->
            <div class="chart-row">
                <div class="chart-card">
                    <div class="chart-header"><h3>📈 Doanh thu theo ngày</h3></div>
                    <canvas id="revenueChart" width="400" height="200"></canvas>
                </div>
                <div class="chart-card">
                    <div class="chart-header"><h3>🥧 Doanh thu theo phương thức TT</h3></div>
                    <canvas id="paymentChart" width="400" height="200"></canvas>
                </div>
            </div>

            <!-- Top Selling Books -->
            <div class="data-table-container">
                <div class="table-header"><h3>🏆 Top sách bán chạy nhất</h3></div>
                <table class="data-table">
                    <thead><tr><th>#</th><th>Sách</th><th>Tác giả</th><th>Đã bán</th><th>Doanh thu</th></thead>
                    <tbody>
                        <c:forEach var="book" items="${topSellingBooks}" varStatus="st">
                            <tr>
                                <td>${st.index + 1}</td>
                                <td><strong>${book.title}</strong></td>
                                <td>${book.author}</td>
                                <td>${book.totalSold} cuốn</td>
                                <td><fmt:formatNumber value="${book.revenue}" type="number" groupingUsed="true"/> ₫</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Revenue Summary -->
            <div class="stat-grid" style="margin-top: 24px;">
                <div class="stat-card"><div class="stat-icon green">💰</div><div class="stat-info"><h3>Tổng doanh thu</h3><p class="stat-value"><fmt:formatNumber value="${totalRevenue}" type="number" groupingUsed="true"/> ₫</p></div></div>
                <div class="stat-card"><div class="stat-icon blue">📦</div><div class="stat-info"><h3>Tổng đơn hàng</h3><p class="stat-value">${totalOrders}</p></div></div>
                <div class="stat-card"><div class="stat-icon orange">⭐</div><div class="stat-info"><h3>Đánh giá trung bình</h3><p class="stat-value">${avgRating} / 5</p></div></div>
            </div>
        </main>
    </div>

    <script>
        // Revenue Chart
        const revenueCtx = document.getElementById('revenueChart').getContext('2d');
        new Chart(revenueCtx, {
            type: 'line',
            data: { labels: ${dateLabels}, datasets: [{ label: 'Doanh thu (VNĐ)', data: ${revenueData}, borderColor: '#405a28', backgroundColor: 'rgba(64,90,40,0.1)', tension: 0.4, fill: true }] },
            options: { responsive: true, plugins: { legend: { position: 'top' } } }
        });

        // Payment Method Chart
        const paymentCtx = document.getElementById('paymentChart').getContext('2d');
        new Chart(paymentCtx, {
            type: 'doughnut',
            data: { labels: ${paymentLabels}, datasets: [{ data: ${paymentData}, backgroundColor: ['#405a28', '#2e7d32', '#ed6c02', '#4361ee', '#9c27b0'] }] },
            options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
        });
    </script>
</body>
</html>