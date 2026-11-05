<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Room Management" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/students" class="nav-item">Students</a>
            <a href="${pageContext.request.contextPath}/admin/rooms" class="nav-item active">Rooms</a>
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
                    <h1>Room Inventory</h1>
                    <p class="text-muted">Manage hostel blocks, room capacities, fees, and bed availability.</p>
                </div>
                <button type="button" class="btn btn-primary" data-modal-open="addRoomModal">Add New Room</button>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <div class="filter-bar">
                <span>Filter Status:</span>
                <a href="${pageContext.request.contextPath}/admin/rooms?filter=ALL" class="btn btn-sm ${currentFilter == 'ALL' ? 'btn-primary' : 'btn-secondary'}">All Rooms</a>
                <a href="${pageContext.request.contextPath}/admin/rooms?filter=AVAILABLE" class="btn btn-sm ${currentFilter == 'AVAILABLE' ? 'btn-primary' : 'btn-secondary'}">Available</a>
                <a href="${pageContext.request.contextPath}/admin/rooms?filter=OCCUPIED" class="btn btn-sm ${currentFilter == 'OCCUPIED' ? 'btn-primary' : 'btn-secondary'}">Occupied</a>
            </div>

            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Room No.</th>
                            <th>Block / Wing</th>
                            <th>Total Capacity</th>
                            <th>Current Occupancy</th>
                            <th>Monthly Fee</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty roomList}">
                                <tr>
                                    <td colspan="7" style="text-align:center; padding: var(--space-6); color: var(--color-text-muted);">
                                        No rooms found under the selected filter.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="r" items="${roomList}">
                                    <tr>
                                        <td><strong>${r.roomNumber}</strong></td>
                                        <td>${r.blockWing}</td>
                                        <td>${r.capacity} beds</td>
                                        <td>${r.currentOccupancy} / ${r.capacity}</td>
                                        <td>$${r.monthlyFee}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${r.roomStatus == 'AVAILABLE'}">
                                                    <span class="badge badge-success">Available</span>
                                                </c:when>
                                                <c:when test="${r.roomStatus == 'FULL'}">
                                                    <span class="badge badge-danger">Full</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-warning">${r.roomStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div style="display: flex; gap: var(--space-2);">
                                                <button type="button" class="btn btn-secondary btn-sm"
                                                    onclick="openEditRoomModal('${r.roomId}', '${r.roomNumber}', '${r.blockWing}', '${r.capacity}', '${r.monthlyFee}', '${r.roomStatus}')">
                                                    Edit
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/rooms" method="post" onsubmit="return confirm('Delete room ${r.roomNumber}?');" style="display:inline;">
                                                    <input type="hidden" name="action" value="delete" />
                                                    <input type="hidden" name="roomId" value="${r.roomId}" />
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

<!-- Modal: Add Room -->
<div id="addRoomModal" class="modal-backdrop">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/rooms" method="post">
            <input type="hidden" name="action" value="add" />
            <div class="modal-header">
                <h3>Add New Hostel Room</h3>
                <button type="button" class="modal-close-btn" data-modal-close>&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Room Number</label>
                    <input type="text" name="roomNumber" placeholder="e.g. A-101" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Block / Wing</label>
                    <input type="text" name="blockWing" placeholder="e.g. Block A" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Bed Capacity</label>
                    <input type="number" name="capacity" min="1" max="6" value="2" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Monthly Accommodation Fee ($)</label>
                    <input type="number" step="0.01" name="monthlyFee" placeholder="e.g. 250.00" class="form-control" required />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Save Room</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Edit Room -->
<div id="editRoomModal" class="modal-backdrop">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/rooms" method="post">
            <input type="hidden" name="action" value="update" />
            <input type="hidden" id="editRoomId" name="roomId" />
            <div class="modal-header">
                <h3>Edit Room Information</h3>
                <button type="button" class="modal-close-btn" data-modal-close>&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Room Number</label>
                    <input type="text" id="editRoomNumber" name="roomNumber" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Block / Wing</label>
                    <input type="text" id="editBlockWing" name="blockWing" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Bed Capacity</label>
                    <input type="number" id="editCapacity" name="capacity" min="1" max="6" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Monthly Fee ($)</label>
                    <input type="number" step="0.01" id="editMonthlyFee" name="monthlyFee" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Room Status</label>
                    <select id="editRoomStatus" name="roomStatus" class="form-control">
                        <option value="AVAILABLE">AVAILABLE</option>
                        <option value="FULL">FULL</option>
                        <option value="MAINTENANCE">MAINTENANCE</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Update Room</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
