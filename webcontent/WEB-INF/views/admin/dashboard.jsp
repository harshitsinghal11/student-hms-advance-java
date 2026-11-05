<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Admin Dashboard" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item active">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/students" class="nav-item">Students</a>
            <a href="${pageContext.request.contextPath}/admin/rooms" class="nav-item">Rooms</a>
            <a href="${pageContext.request.contextPath}/admin/allocations" class="nav-item">Allocations</a>
            <a href="${pageContext.request.contextPath}/admin/fees" class="nav-item">Fees</a>
            <a href="${pageContext.request.contextPath}/admin/complaints" class="nav-item">Complaints</a>
            <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item">Reports</a>
            <a href="${pageContext.request.contextPath}/auth/change-password" class="nav-item">Change Password</a>
        </nav>
    </aside>

    <main class="main-content">
        <div class="page-body">
            <div class="page-header-row">
                <div>
                    <h1>Admin Control Panel</h1>
                    <p class="text-muted">Overview of hostel accommodations, resident students, and pending tasks.</p>
                </div>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>

            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Total Students</div>
                    <div class="stat-value">${totalStudents}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Total Rooms</div>
                    <div class="stat-value">${totalRooms}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Available Rooms</div>
                    <div class="stat-value" style="color: var(--color-success);">${availableRooms}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Occupied Rooms</div>
                    <div class="stat-value">${occupiedRooms}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Active Allotments</div>
                    <div class="stat-value">${activeAllocations}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Pending Invoices</div>
                    <div class="stat-value" style="color: var(--color-warning);">${pendingFeesCount}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Open Grievances</div>
                    <div class="stat-value" style="color: var(--color-danger);">${pendingComplaints}</div>
                </div>
            </div>

            <div class="page-header-row" style="margin-top: var(--space-6);">
                <h2>Quick Management Actions</h2>
            </div>

            <div class="filter-bar">
                <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-secondary">Manage Students</a>
                <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-secondary">Configure Rooms</a>
                <a href="${pageContext.request.contextPath}/admin/allocations" class="btn btn-primary">Room Allocations</a>
                <a href="${pageContext.request.contextPath}/admin/fees" class="btn btn-secondary">Fee Invoices</a>
                <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-secondary">Grievance Inbox</a>
                <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">Analytical Reports</a>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
