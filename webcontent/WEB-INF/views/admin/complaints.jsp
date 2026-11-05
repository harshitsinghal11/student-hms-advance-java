<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Complaint Management" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/students" class="nav-item">Students</a>
            <a href="${pageContext.request.contextPath}/admin/rooms" class="nav-item">Rooms</a>
            <a href="${pageContext.request.contextPath}/admin/allocations" class="nav-item">Allocations</a>
            <a href="${pageContext.request.contextPath}/admin/fees" class="nav-item">Fees</a>
            <a href="${pageContext.request.contextPath}/admin/complaints" class="nav-item active">Complaints</a>
            <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item">Reports</a>
            <a href="${pageContext.request.contextPath}/auth/change-password" class="nav-item">Change Password</a>
        </nav>
    </aside>

    <main class="main-content">
        <div class="page-body">
            <div class="page-header-row">
                <div>
                    <h1>Grievance Redressal Inbox</h1>
                    <p class="text-muted">Review maintenance issues reported by residents, update statuses, and log resolution remarks.</p>
                </div>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <div class="filter-bar">
                <span>Filter by Status:</span>
                <a href="${pageContext.request.contextPath}/admin/complaints?filter=ALL" class="btn btn-sm ${currentFilter == 'ALL' ? 'btn-primary' : 'btn-secondary'}">All Complaints</a>
                <a href="${pageContext.request.contextPath}/admin/complaints?filter=PENDING" class="btn btn-sm ${currentFilter == 'PENDING' ? 'btn-primary' : 'btn-secondary'}">Pending</a>
                <a href="${pageContext.request.contextPath}/admin/complaints?filter=IN_PROGRESS" class="btn btn-sm ${currentFilter == 'IN_PROGRESS' ? 'btn-primary' : 'btn-secondary'}">In Progress</a>
                <a href="${pageContext.request.contextPath}/admin/complaints?filter=RESOLVED" class="btn btn-sm ${currentFilter == 'RESOLVED' ? 'btn-primary' : 'btn-secondary'}">Resolved</a>
            </div>

            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#ID</th>
                            <th>Student</th>
                            <th>Roll Number</th>
                            <th>Room</th>
                            <th>Category</th>
                            <th>Description</th>
                            <th>Status</th>
                            <th>Filed At</th>
                            <th>Warden Remarks</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty complaintList}">
                                <tr>
                                    <td colspan="10" style="text-align:center; padding: var(--space-6); color: var(--color-text-muted);">
                                        No complaints found under the selected filter.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="c" items="${complaintList}">
                                    <tr>
                                        <td>#${c.complaintId}</td>
                                        <td><strong>${c.studentName}</strong></td>
                                        <td>${c.rollNumber}</td>
                                        <td>${empty c.roomNumber ? 'Unassigned' : c.roomNumber}</td>
                                        <td><span class="badge badge-info">${c.category}</span></td>
                                        <td>${c.description}</td>
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
                                        <td>${c.filedAt}</td>
                                        <td>${empty c.resolutionRemarks ? '-' : c.resolutionRemarks}</td>
                                        <td>
                                            <button type="button" class="btn btn-secondary btn-sm"
                                                onclick="openUpdateComplaintModal('${c.complaintId}', '${c.studentName}', '${c.category}', '${c.status}', '${c.resolutionRemarks}')">
                                                Update
                                            </button>
                                        </td>
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

<!-- Modal: Update Complaint -->
<div id="updateComplaintModal" class="modal-backdrop">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/complaints" method="post">
            <input type="hidden" id="complaintId" name="complaintId" />
            <div class="modal-header">
                <h3>Update Grievance Status</h3>
                <button type="button" class="modal-close-btn" data-modal-close>&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Resident & Complaint</label>
                    <div id="complaintStudent" style="font-weight: var(--font-weight-bold); margin-bottom: var(--space-2);"></div>
                </div>
                <div class="form-group">
                    <label class="form-label">Lifecycle Status</label>
                    <select id="complaintStatus" name="status" class="form-control" required>
                        <option value="PENDING">PENDING</option>
                        <option value="IN_PROGRESS">IN_PROGRESS</option>
                        <option value="RESOLVED">RESOLVED</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Warden Resolution Remarks</label>
                    <textarea id="complaintRemarks" name="remarks" class="form-control" rows="3" placeholder="Enter action taken, technician dispatched, or resolution details..."></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Save Resolution</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

