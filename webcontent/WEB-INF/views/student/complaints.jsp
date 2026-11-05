<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="My Complaints" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/profile" class="nav-item">My Profile</a>
            <a href="${pageContext.request.contextPath}/student/room" class="nav-item">Allocated Room</a>
            <a href="${pageContext.request.contextPath}/student/fees" class="nav-item">Fees & Receipts</a>
            <a href="${pageContext.request.contextPath}/student/complaints" class="nav-item active">My Complaints</a>
            <a href="${pageContext.request.contextPath}/auth/change-password" class="nav-item">Change Password</a>
        </nav>
    </aside>

    <main class="main-content">
        <div class="page-body">
            <div class="page-header-row">
                <div>
                    <h1>Maintenance Grievances</h1>
                    <p class="text-muted">Report hostel facility problems and follow up on warden redressal in real time.</p>
                </div>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>
            <c:if test="${param.error == 'failed'}">
                <div class="alert alert-danger">Failed to file complaint. Please ensure details are valid.</div>
            </c:if>

            <div class="stat-card" style="margin-bottom: var(--space-6);">
                <h3 style="margin-bottom: var(--space-4);">Submit a New Maintenance Grievance</h3>
                <form action="${pageContext.request.contextPath}/student/complaints" method="post">
                    <div class="form-group">
                        <label class="form-label">Issue Category</label>
                        <select name="category" class="form-control" required style="max-width: 320px;">
                            <option value="ELECTRICAL">ELECTRICAL (Lights, Fans, Outlets)</option>
                            <option value="PLUMBING">PLUMBING (Taps, Showers, Drains)</option>
                            <option value="CLEANLINESS">CLEANLINESS (Room, Corridor, Washroom)</option>
                            <option value="OTHER">OTHER (Carpentry, Furniture, etc.)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Detailed Description of the Problem</label>
                        <textarea name="description" class="form-control" rows="3" placeholder="Explain the exact issue and location (e.g. Broken study lamp in bed 2)..." required></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary">Submit Grievance</button>
                </form>
            </div>

            <div class="page-header-row">
                <h2>Grievance History & Status</h2>
            </div>

            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Complaint #</th>
                            <th>Category</th>
                            <th>Description</th>
                            <th>Filed At</th>
                            <th>Status</th>
                            <th>Warden Resolution Remarks</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty complaintList}">
                                <tr>
                                    <td colspan="6" style="text-align: center; color: var(--color-text-muted); padding: var(--space-6);">
                                        You have not filed any maintenance grievances.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="c" items="${complaintList}">
                                    <tr>
                                        <td>#${c.complaintId}</td>
                                        <td><span class="badge badge-info">${c.category}</span></td>
                                        <td>${c.description}</td>
                                        <td>${c.filedAt}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${c.status == 'RESOLVED'}">
                                                    <span class="badge badge-success">RESOLVED</span>
                                                </c:when>
                                                <c:when test="${c.status == 'IN_PROGRESS'}">
                                                    <span class="badge badge-warning">IN PROGRESS</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-danger">PENDING</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${empty c.resolutionRemarks ? '<span style="color: var(--color-text-muted);">- In Queue -</span>' : c.resolutionRemarks}</td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

