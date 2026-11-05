<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Allocated Room" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/profile" class="nav-item">My Profile</a>
            <a href="${pageContext.request.contextPath}/student/room" class="nav-item active">Allocated Room</a>
            <a href="${pageContext.request.contextPath}/student/fees" class="nav-item">Fees & Receipts</a>
            <a href="${pageContext.request.contextPath}/student/complaints" class="nav-item">My Complaints</a>
            <a href="${pageContext.request.contextPath}/auth/change-password" class="nav-item">Change Password</a>
        </nav>
    </aside>

    <main class="main-content">
        <div class="page-body">
            <div class="page-header-row">
                <div>
                    <h1>Hostel Accommodation — Room Details</h1>
                    <p class="text-muted">Information on your assigned room, block wing, bed capacity, and current roommates.</p>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty allocation}">
                    <div class="alert alert-danger">
                        <strong>No Active Room Allocation:</strong> You have not been allotted a hostel bed yet. Please visit or contact the Chief Warden / Hostel Administration office to complete your allotment.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-label">Assigned Room</div>
                            <div class="stat-value">Room ${allocation.roomNumber}</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-label">Block / Wing</div>
                            <div class="stat-value">${allocation.blockWing}</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-label">Room Capacity</div>
                            <div class="stat-value">${room.currentOccupancy} / ${room.capacity} beds</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-label">Monthly Fee</div>
                            <div class="stat-value">$${room.monthlyFee}</div>
                        </div>
                    </div>

                    <div class="page-header-row" style="margin-top: var(--space-6);">
                        <h2>Current Roommates</h2>
                    </div>

                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Resident Student</th>
                                    <th>Roll Number</th>
                                    <th>Allotment Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty roommates}">
                                        <tr>
                                            <td colspan="4" style="text-align: center; color: var(--color-text-muted); padding: var(--space-6);">
                                                No other students currently occupy beds in this room.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="rm" items="${roommates}">
                                            <tr>
                                                <td><strong>${rm.studentName}</strong></td>
                                                <td>${rm.rollNumber}</td>
                                                <td>${rm.allocationDate}</td>
                                                <td><span class="badge badge-success">ACTIVE</span></td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

