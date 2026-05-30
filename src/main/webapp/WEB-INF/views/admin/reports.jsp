<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Báo cáo thống kê - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
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
                    <div class="chart-header"><h3>🥧 Doanh thu theo phương thức thanh toán</h3></div>
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
                        <c:if test="${empty topSellingBooks}">
                            <tr><td colspan="5" style="text-align:center;">Chưa có dữ liệu bán hàng</td></tr>
                        </c:if>
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
            
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <script>
        // Chuyển đổi dữ liệu từ JSP sang JavaScript an toàn
        const dateLabels = ${dateLabelsJson};
        const revenueData = ${revenueDataJson};
        const paymentLabels = ${paymentLabelsJson};
        const paymentData = ${paymentDataJson};

        console.log('Date Labels:', dateLabels);
        console.log('Revenue Data:', revenueData);
        console.log('Payment Labels:', paymentLabels);
        console.log('Payment Data:', paymentData);

        // Revenue Chart
        if (document.getElementById('revenueChart')) {
            const revenueCtx = document.getElementById('revenueChart').getContext('2d');
            new Chart(revenueCtx, {
                type: 'line',
                data: {
                    labels: dateLabels,
                    datasets: [{
                        label: 'Doanh thu (VNĐ)',
                        data: revenueData,
                        borderColor: '#405a28',
                        backgroundColor: 'rgba(64,90,40,0.1)',
                        borderWidth: 2,
                        tension: 0.4,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: { position: 'top' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    let value = context.raw;
                                    return 'Doanh thu: ' + value.toLocaleString('vi-VN') + ' ₫';
                                }
                            }
                        }
                    },
                    scales: {
                        y: {
                            ticks: {
                                callback: function(value) {
                                    return value.toLocaleString('vi-VN') + ' ₫';
                                }
                            }
                        }
                    }
                }
            });
        }

        // Payment Method Chart
        if (document.getElementById('paymentChart')) {
            const paymentCtx = document.getElementById('paymentChart').getContext('2d');
            new Chart(paymentCtx, {
                type: 'doughnut',
                data: {
                    labels: paymentLabels,
                    datasets: [{
                        data: paymentData,
                        backgroundColor: ['#405a28', '#2e7d32', '#ed6c02', '#4361ee', '#9c27b0', '#dc3545']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    let label = context.label || '';
                                    let value = context.raw;
                                    let total = context.dataset.data.reduce((a, b) => a + b, 0);
                                    let percentage = ((value / total) * 100).toFixed(1);
                                    return `${label}: ${value.toLocaleString('vi-VN')} ₫ (${percentage}%)`;
                                }
                            }
                        }
                    }
                }
            });
        }
    </script>
</body>
</html>