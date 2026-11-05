<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Student Dashboard" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="nav-item active">Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/profile" class="nav-item">My Profile</a>
            <a href="${pageContext.request.contextPath}/student/room" class="nav-item">Allocated Room</a>
            <a href="${pageContext.request.contextPath}/student/fees" class="nav-item">Fees & Receipts</a>
            <a href="${pageContext.request.contextPath}/student/complaints" class="nav-item">My Complaints</a>
            <a href="${pageContext.request.contextPath}/auth/change-password" class="nav-item">Change Password</a>
        </nav>
    </aside>

    <main class="main-content">
        <div class="page-body">
            <div class="page-header-row">
                <div>
                    <h1>Welcome, ${empty student ? sessionScope.currentUser.fullName : student.fullName}!</h1>
                    <p class="text-muted">Hostel Resident Self-Service Portal</p>
                </div>
            </div>

            <c:if test="${empty student}">
                <div class="alert alert-danger">
                    Your student resident profile has not been fully configured yet. Please contact the Hostel Warden office.
                </div>
            </c:if>

            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Allocated Room</div>
                    <div class="stat-value" style="font-size: var(--font-size-xl);">
                        <c:choose>
                            <c:when test="${not empty allocation}">
                                Room ${allocation.roomNumber}
                            </c:when>
                            <c:otherwise>
                                <span style="color: var(--color-text-muted);">Unallotted</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Outstanding Fees</div>
                    <div class="stat-value" style="color: ${pendingDues > 0 ? 'var(--color-danger)' : 'var(--color-success)'};">
                        $${pendingDues}
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Active Grievances</div>
                    <div class="stat-value" style="color: ${openComplaintsCount > 0 ? 'var(--color-warning)' : 'var(--color-text-primary)'};">
                        ${openComplaintsCount}
                    </div>
                </div>
            </div>

            <div class="page-header-row" style="margin-top: var(--space-6);">
                <h2>Resident Services</h2>
            </div>

            <div class="filter-bar">
                <a href="${pageContext.request.contextPath}/student/profile" class="btn btn-secondary">View Profile</a>
                <a href="${pageContext.request.contextPath}/student/room" class="btn btn-secondary">Room & Roommates</a>
                <a href="${pageContext.request.contextPath}/student/fees" class="btn btn-secondary">Fee Invoices</a>
                <a href="${pageContext.request.contextPath}/student/complaints" class="btn btn-primary">File Maintenance Issue</a>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

