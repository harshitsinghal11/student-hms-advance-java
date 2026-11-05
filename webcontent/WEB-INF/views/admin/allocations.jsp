<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Room Allocations" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/students" class="nav-item">Students</a>
            <a href="${pageContext.request.contextPath}/admin/rooms" class="nav-item">Rooms</a>
            <a href="${pageContext.request.contextPath}/admin/allocations" class="nav-item active">Allocations</a>
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
                    <h1>Room Allocations</h1>
                    <p class="text-muted">Track bed allotment history, assign vacant rooms, and process vacations.</p>
                </div>
                <button type="button" class="btn btn-primary" data-modal-open="allocateModal">New Room Allotment</button>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Allocation ID</th>
                            <th>Student Name</th>
                            <th>Roll Number</th>
                            <th>Room Number</th>
                            <th>Block / Wing</th>
                            <th>Allocation Date</th>
                            <th>Vacate Date</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty allocationList}">
                                <tr>
                                    <td colspan="9" style="text-align:center; padding: var(--space-6); color: var(--color-text-muted);">
                                        No room allocation records found.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="a" items="${allocationList}">
                                    <tr>
                                        <td>#${a.allocationId}</td>
                                        <td><strong>${a.studentName}</strong></td>
                                        <td>${a.rollNumber}</td>
                                        <td>Room ${a.roomNumber}</td>
                                        <td>${a.blockWing}</td>
                                        <td>${a.allocationDate}</td>
                                        <td>${empty a.vacateDate ? '-' : a.vacateDate}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${a.status == 'ACTIVE'}">
                                                    <span class="badge badge-success">ACTIVE</span>
                                                </c:when>
                                                <c:when test="${a.status == 'TRANSFERRED'}">
                                                    <span class="badge badge-info">TRANSFERRED</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-warning">${a.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:if test="${a.status == 'ACTIVE'}">
                                                <div style="display: flex; gap: var(--space-2);">
                                                    <form action="${pageContext.request.contextPath}/admin/allocations" method="post" onsubmit="return confirm('Vacate student ${a.studentName} from Room ${a.roomNumber}?');" style="display:inline;">
                                                        <input type="hidden" name="action" value="vacate" />
                                                        <input type="hidden" name="allocationId" value="${a.allocationId}" />
                                                        <button type="submit" class="btn btn-secondary btn-sm">Vacate</button>
                                                    </form>
                                                </div>
                                            </c:if>
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

<!-- Modal: Allocate Room -->
<div id="allocateModal" class="modal-backdrop">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/allocations" method="post">
            <input type="hidden" name="action" value="allocate" />
            <div class="modal-header">
                <h3>Allocate Student to Vacant Room</h3>
                <button type="button" class="modal-close-btn" data-modal-close>&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Select Unallocated Student</label>
                    <select name="studentId" class="form-control" required>
                        <c:choose>
                            <c:when test="${empty unallocatedStudents}">
                                <option value="" disabled selected>No unallocated students available</option>
                            </c:when>
                            <c:otherwise>
                                <option value="" disabled selected>-- Select a student --</option>
                                <c:forEach var="st" items="${unallocatedStudents}">
                                    <option value="${st.studentId}">${st.fullName} (${st.rollNumber} - ${st.course})</option>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Select Available Room</label>
                    <select name="roomId" class="form-control" required>
                        <c:choose>
                            <c:when test="${empty availableRooms}">
                                <option value="" disabled selected>No available rooms with vacant beds</option>
                            </c:when>
                            <c:otherwise>
                                <option value="" disabled selected>-- Select a room --</option>
                                <c:forEach var="rm" items="${availableRooms}">
                                    <option value="${rm.roomId}">Room ${rm.roomNumber} - ${rm.blockWing} (Occupancy: ${rm.currentOccupancy}/${rm.capacity})</option>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary" ${empty unallocatedStudents || empty availableRooms ? 'disabled' : ''}>Confirm Allotment</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
