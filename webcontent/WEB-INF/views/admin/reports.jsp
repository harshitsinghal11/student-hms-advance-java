<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Hostel Reports" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/students" class="nav-item">Students</a>
            <a href="${pageContext.request.contextPath}/admin/rooms" class="nav-item">Rooms</a>
            <a href="${pageContext.request.contextPath}/admin/allocations" class="nav-item">Allocations</a>
            <a href="${pageContext.request.contextPath}/admin/fees" class="nav-item">Fees</a>
            <a href="${pageContext.request.contextPath}/admin/complaints" class="nav-item">Complaints</a>
            <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item active">Reports</a>
            <a href="${pageContext.request.contextPath}/auth/change-password" class="nav-item">Change Password</a>
        </nav>
    </aside>

    <main class="main-content">
        <div class="page-body">
            <div class="page-header-row">
                <div>
                    <h1>Analytical Hostel Reports</h1>
                    <p class="text-muted">Real-time analytical data for administrative audits, occupancy tracking, and accounts.</p>
                </div>
            </div>

            <nav class="tab-nav">
                <a href="${pageContext.request.contextPath}/admin/reports?tab=occupancy" class="tab-item ${activeTab == 'occupancy' ? 'active' : ''}">Occupancy Analytics</a>
                <a href="${pageContext.request.contextPath}/admin/reports?tab=students" class="tab-item ${activeTab == 'students' ? 'active' : ''}">Resident Demographics</a>
                <a href="${pageContext.request.contextPath}/admin/reports?tab=fees" class="tab-item ${activeTab == 'fees' ? 'active' : ''}">Fee Collection Audit</a>
                <a href="${pageContext.request.contextPath}/admin/reports?tab=complaints" class="tab-item ${activeTab == 'complaints' ? 'active' : ''}">Grievance Statistics</a>
            </nav>

            <c:choose>
                <%-- Tab: Occupancy --%>
                <c:when test="${activeTab == 'occupancy'}">
                    <c:if test="${not empty occupancySummary}">
                        <div class="stat-card" style="margin-bottom: var(--space-6); background-color: var(--color-bg-subtle);">
                            <div class="stat-label">Live Occupancy Metric</div>
                            <div style="font-size: var(--font-size-lg); font-weight: var(--font-weight-medium);">${occupancySummary}</div>
                        </div>
                    </c:if>

                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Room No</th>
                                    <th>Block / Wing</th>
                                    <th>Capacity</th>
                                    <th>Occupied</th>
                                    <th>Vacant Beds</th>
                                    <th>Monthly Fee</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${roomReport}">
                                    <tr>
                                        <td><strong>${row[0]}</strong></td>
                                        <td>${row[1]}</td>
                                        <td>${row[2]}</td>
                                        <td>${row[3]}</td>
                                        <td>${row[4]}</td>
                                        <td>$${row[5]}</td>
                                        <td><span class="badge ${row[6] == 'AVAILABLE' ? 'badge-success' : 'badge-danger'}">${row[6]}</span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>

                <%-- Tab: Students --%>
                <c:when test="${activeTab == 'students'}">
                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Roll Number</th>
                                    <th>Full Name</th>
                                    <th>Course</th>
                                    <th>Contact</th>
                                    <th>Allocated Room</th>
                                    <th>Allocation Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${studentReport}">
                                    <tr>
                                        <td><strong>${row[0]}</strong></td>
                                        <td>${row[1]}</td>
                                        <td>${row[2]}</td>
                                        <td>${row[3]}</td>
                                        <td>${row[4]}</td>
                                        <td>
                                            <span class="badge ${row[5] == 'ALLOCATED' ? 'badge-success' : 'badge-warning'}">${row[5]}</span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>

                <%-- Tab: Fees --%>
                <c:when test="${activeTab == 'fees'}">
                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Roll Number</th>
                                    <th>Student Name</th>
                                    <th>Total Billed</th>
                                    <th>Total Paid</th>
                                    <th>Outstanding Dues</th>
                                    <th>Clearance Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${feeReport}">
                                    <tr>
                                        <td><strong>${row[0]}</strong></td>
                                        <td>${row[1]}</td>
                                        <td>$${row[2]}</td>
                                        <td>$${row[3]}</td>
                                        <td style="color: ${row[4] != '0.00' ? 'var(--color-danger)' : 'var(--color-success)'};">$${row[4]}</td>
                                        <td>
                                            <span class="badge ${row[5] == 'PAID' ? 'badge-success' : 'badge-danger'}">${row[5]}</span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>

                <%-- Tab: Complaints --%>
                <c:when test="${activeTab == 'complaints'}">
                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Maintenance Category</th>
                                    <th>Total Reported</th>
                                    <th>Pending</th>
                                    <th>In Progress</th>
                                    <th>Resolved</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${complaintReport}">
                                    <tr>
                                        <td><strong>${row[0]}</strong></td>
                                        <td>${row[1]}</td>
                                        <td><span class="badge badge-danger">${row[2]}</span></td>
                                        <td><span class="badge badge-warning">${row[3]}</span></td>
                                        <td><span class="badge badge-success">${row[4]}</span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
            </c:choose>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

