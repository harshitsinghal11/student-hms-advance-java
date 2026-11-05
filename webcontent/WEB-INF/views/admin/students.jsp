<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Student Management" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/students" class="nav-item active">Students</a>
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
                    <h1>Student Directory</h1>
                    <p class="text-muted">Manage hostel student profiles, enrollments, and accounts.</p>
                </div>
                <button type="button" class="btn btn-primary" data-modal-open="addStudentModal">Add New Student</button>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/students" method="get" class="filter-bar">
                <input type="text" name="keyword" value="${keyword}" placeholder="Search name, roll number, course..." class="form-control" style="max-width: 350px;" />
                <button type="submit" class="btn btn-secondary">Search</button>
                <c:if test="${not empty keyword}">
                    <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-secondary">Clear</a>
                </c:if>
            </form>

            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Roll Number</th>
                            <th>Full Name</th>
                            <th>Course</th>
                            <th>Contact</th>
                            <th>Emergency Contact</th>
                            <th>Address</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty studentList}">
                                <tr>
                                    <td colspan="7" style="text-align:center; padding: var(--space-6); color: var(--color-text-muted);">
                                        No student records found.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="s" items="${studentList}">
                                    <tr>
                                        <td><strong>${s.rollNumber}</strong></td>
                                        <td>${s.fullName}</td>
                                        <td>${s.course}</td>
                                        <td>${s.contactNumber}</td>
                                        <td>${s.emergencyContact}</td>
                                        <td>${s.address}</td>
                                        <td>
                                            <div style="display: flex; gap: var(--space-2);">
                                                <button type="button" class="btn btn-secondary btn-sm"
                                                    onclick="openEditStudentModal('${s.studentId}', '${s.fullName}', '${s.rollNumber}', '${s.course}', '${s.contactNumber}', '${s.emergencyContact}', '${s.address}')">
                                                    Edit
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/students" method="post" onsubmit="return confirm('Delete student ${s.fullName}?');" style="display:inline;">
                                                    <input type="hidden" name="action" value="delete" />
                                                    <input type="hidden" name="studentId" value="${s.studentId}" />
                                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                                </form>
                                            </div>
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

<!-- Modal: Add Student -->
<div id="addStudentModal" class="modal-backdrop">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/students" method="post">
            <input type="hidden" name="action" value="add" />
            <div class="modal-header">
                <h3>Add New Student</h3>
                <button type="button" class="modal-close-btn" data-modal-close>&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Full Name</label>
                    <input type="text" name="fullName" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Username (for portal login)</label>
                    <input type="text" name="username" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Initial Password</label>
                    <input type="password" name="password" class="form-control" required minlength="8" />
                </div>
                <div class="form-group">
                    <label class="form-label">Roll Number</label>
                    <input type="text" name="rollNumber" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Course / Branch</label>
                    <input type="text" name="course" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Contact Number</label>
                    <input type="text" name="contactNumber" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Emergency Contact</label>
                    <input type="text" name="emergencyContact" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Permanent Address</label>
                    <textarea name="address" class="form-control" rows="2"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Create Student</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Edit Student -->
<div id="editStudentModal" class="modal-backdrop">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/students" method="post">
            <input type="hidden" name="action" value="update" />
            <input type="hidden" id="editStudentId" name="studentId" />
            <div class="modal-header">
                <h3>Edit Student Details</h3>
                <button type="button" class="modal-close-btn" data-modal-close>&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Full Name</label>
                    <input type="text" id="editFullName" name="fullName" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Roll Number</label>
                    <input type="text" id="editRollNumber" name="rollNumber" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Course</label>
                    <input type="text" id="editCourse" name="course" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Contact Number</label>
                    <input type="text" id="editContactNumber" name="contactNumber" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Emergency Contact</label>
                    <input type="text" id="editEmergencyContact" name="emergencyContact" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Address</label>
                    <textarea id="editAddress" name="address" class="form-control" rows="2"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
