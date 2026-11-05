<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="My Profile" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/profile" class="nav-item active">My Profile</a>
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
                    <h1>Resident Student Profile</h1>
                    <p class="text-muted">Verify your university details and update your emergency contact info.</p>
                </div>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <div class="stat-card" style="max-width: 600px;">
                <form action="${pageContext.request.contextPath}/student/profile" method="post">
                    <div class="form-group">
                        <label class="form-label">University Roll Number</label>
                        <input type="text" class="form-control" value="${student.rollNumber}" readonly style="background-color: var(--color-bg-subtle);" />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Enrolled Course / Degree</label>
                        <input type="text" class="form-control" value="${student.course}" readonly style="background-color: var(--color-bg-subtle);" />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Portal Username</label>
                        <input type="text" class="form-control" value="${student.username}" readonly style="background-color: var(--color-bg-subtle);" />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Full Name</label>
                        <input type="text" name="fullName" class="form-control" value="${student.fullName}" required />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Personal Phone Number</label>
                        <input type="text" name="contactNumber" class="form-control" value="${student.contactNumber}" required />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Emergency Contact (Parent / Guardian)</label>
                        <input type="text" name="emergencyContact" class="form-control" value="${student.emergencyContact}" required />
                    </div>

                    <div class="form-group">
                        <label class="form-label">Permanent Residential Address</label>
                        <textarea name="address" class="form-control" rows="3" required>${student.address}</textarea>
                    </div>

                    <button type="submit" class="btn btn-primary" style="margin-top: var(--space-2);">Update Profile</button>
                </form>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

