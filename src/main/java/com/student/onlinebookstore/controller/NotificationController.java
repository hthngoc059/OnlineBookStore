package com.student.onlinebookstore.controller;

import com.student.onlinebookstore.dao.NotificationDAO;
import com.student.onlinebookstore.model.Notification;
import com.student.onlinebookstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet("/notifications")
public class NotificationController extends HttpServlet {

    private NotificationDAO notificationDAO;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        notificationDAO = new NotificationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request, response);
        if (currentUser == null) return;

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
            default:
                listNotifications(request, response, currentUser);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request, response);
        if (currentUser == null) return;

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "markRead":    markRead(request, response, currentUser);    break;
            case "markAllRead": markAllRead(request, response, currentUser); break;
            case "delete":      deleteOne(request, response, currentUser);   break;
            case "deleteAll":   deleteAll(request, response, currentUser);   break;
            default:
                response.sendRedirect(request.getContextPath() + "/notifications");
        }
    }

    // ─── List ────────────────────────────────────────────────────────────────

    private void listNotifications(HttpServletRequest request,
                                   HttpServletResponse response,
                                   User currentUser)
            throws ServletException, IOException {

        int userId = currentUser.getUserId();
        int page   = parseIntParam(request, "page", 0);
        int size   = 15;

        List<Notification> notifications =
                notificationDAO.getNotificationsByUserId(userId, page, size);

        // ── Build helper maps keyed by notificationId ──────────────────────
        Map<Integer, String> dateMap = new LinkedHashMap<>();
        Map<Integer, String> iconMap = new LinkedHashMap<>();

        for (Notification n : notifications) {

            // 1) Formatted date string (avoids fmt:formatDate LocalDateTime bug)
            String formatted = (n.getCreatedAt() != null)
                    ? n.getCreatedAt().format(DISPLAY_FMT)
                    : "-";
            dateMap.put(n.getNotificationId(), formatted);

            // 2) Icon resolved in Java – no JSTL Unicode issues
            iconMap.put(n.getNotificationId(), resolveIcon(n.getTitle()));
        }

        int unreadCount = notificationDAO.countUnreadByUserId(userId);
        int totalCount  = notificationDAO.countByUserId(userId);
        int totalPages  = (totalCount == 0) ? 1
                        : (int) Math.ceil((double) totalCount / size);

        request.setAttribute("notifications", notifications);
        request.setAttribute("dateMap",       dateMap);
        request.setAttribute("iconMap",       iconMap);
        request.setAttribute("unreadCount",   unreadCount);
        request.setAttribute("totalCount",    totalCount);
        request.setAttribute("currentPage",   page);
        request.setAttribute("totalPages",    totalPages);

        request.getRequestDispatcher("/WEB-INF/views/notification.jsp")
               .forward(request, response);
    }

    /**
     * Chọn emoji icon dựa trên title – xử lý trong Java để tránh lỗi
     * JSTL fn:containsIgnoreCase với ký tự Unicode tiếng Việt.
     */
    private String resolveIcon(String title) {
        if (title == null) return "🔔";
        String t = title.toLowerCase(Locale.ROOT);

        if (t.contains("order")   || t.contains("giao")      ||
            t.contains("ship")    || t.contains("deliver")    ||
            t.contains("confirm") || t.contains("cancel")     ||
            t.contains("pending") || t.contains("update")     ||
            t.contains("don hang")) {
            return "📦";
        }
        if (t.contains("stock")   || t.contains("low")        ||
            t.contains("ton kho") || t.contains("alert")) {
            return "⚠️";
        }
        if (t.contains("payment") || t.contains("paid")       ||
            t.contains("refund")  || t.contains("thanh toan")) {
            return "💳";
        }
        if (t.contains("available") || t.contains("book")     ||
            t.contains("restock")   || t.contains("sach")) {
            return "📚";
        }
        return "🔔";
    }

    // ─── Actions ─────────────────────────────────────────────────────────────

    private void markRead(HttpServletRequest request,
                          HttpServletResponse response,
                          User currentUser) throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try { notificationDAO.markAsRead(Integer.parseInt(idStr)); }
            catch (NumberFormatException ignored) {}
        }
        response.sendRedirect(request.getContextPath() + "/notifications");
    }

    private void markAllRead(HttpServletRequest request,
                             HttpServletResponse response,
                             User currentUser) throws IOException {
        notificationDAO.markAllAsReadByUserId(currentUser.getUserId());
        response.sendRedirect(request.getContextPath() + "/notifications");
    }

    private void deleteOne(HttpServletRequest request,
                           HttpServletResponse response,
                           User currentUser) throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try { notificationDAO.deleteNotification(Integer.parseInt(idStr)); }
            catch (NumberFormatException ignored) {}
        }
        response.sendRedirect(request.getContextPath() + "/notifications");
    }

    private void deleteAll(HttpServletRequest request,
                           HttpServletResponse response,
                           User currentUser) throws IOException {
        notificationDAO.deleteAllByUserId(currentUser.getUserId());
        response.sendRedirect(request.getContextPath() + "/notifications");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private User getCurrentUser(HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/home?showLogin=true");
            return null;
        }
        return user;
    }

    private int parseIntParam(HttpServletRequest request, String name, int defaultVal) {
        String val = request.getParameter(name);
        if (val == null) return defaultVal;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultVal; }
    }
}