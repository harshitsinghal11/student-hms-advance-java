<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Fee Management" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/students" class="nav-item">Students</a>
            <a href="${pageContext.request.contextPath}/admin/rooms" class="nav-item">Rooms</a>
            <a href="${pageContext.request.contextPath}/admin/allocations" class="nav-item">Allocations</a>
            <a href="${pageContext.request.contextPath}/admin/fees" class="nav-item active">Fees</a>
            <a href="${pageContext.request.contextPath}/admin/complaints" class="nav-item">Complaints</a>
            <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item">Reports</a>
            <a href="${pageContext.request.contextPath}/auth/change-password" class="nav-item">Change Password</a>
        </nav>
    </aside>

    <main class="main-content">
        <div class="page-body">
            <div class="page-header-row">
                <div>
                    <h1>Hostel Fee Management</h1>
                    <p class="text-muted">Generate fee bills, audit pending dues, and log payment receipt references.</p>
                </div>
                <button type="button" class="btn btn-primary" data-modal-open="generateFeeModal">Issue Fee Invoice</button>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <div class="filter-bar">
                <span>View Filter:</span>
                <a href="${pageContext.request.contextPath}/admin/fees?filter=ALL" class="btn btn-sm ${currentFilter == 'ALL' ? 'btn-primary' : 'btn-secondary'}">All Invoices</a>
                <a href="${pageContext.request.contextPath}/admin/fees?filter=PENDING" class="btn btn-sm ${currentFilter == 'PENDING' ? 'btn-primary' : 'btn-secondary'}">Pending / Overdue Only</a>
            </div>

            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Invoice #</th>
                            <th>Student</th>
                            <th>Roll Number</th>
                            <th>Amount Due</th>
                            <th>Amount Paid</th>
                            <th>Remaining</th>
                            <th>Due Date</th>
                            <th>Status</th>
                            <th>Transaction Ref</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty feeList}">
                                <tr>
                                    <td colspan="10" style="text-align:center; padding: var(--space-6); color: var(--color-text-muted);">
                                        No fee records found.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="f" items="${feeList}">
                                    <tr>
                                        <td>#${f.feeId}</td>
                                        <td><strong>${f.studentName}</strong></td>
                                        <td>${f.rollNumber}</td>
                                        <td>$${f.amountDue}</td>
                                        <td>$${f.amountPaid}</td>
                                        <td style="color: ${f.amountDue > f.amountPaid ? 'var(--color-danger)' : 'var(--color-success)'};">
                                            $${f.amountDue - f.amountPaid}
                                        </td>
                                        <td>${f.dueDate}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${f.paymentStatus == 'PAID'}">
                                                    <span class="badge badge-success">PAID</span>
                                                </c:when>
                                                <c:when test="${f.paymentStatus == 'OVERDUE'}">
                                                    <span class="badge badge-danger">OVERDUE</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-warning">PENDING</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${empty f.transactionRef ? '-' : f.transactionRef}</td>
                                        <td>
                                            <c:if test="${f.paymentStatus != 'PAID'}">
                                                <button type="button" class="btn btn-primary btn-sm"
                                                    onclick="openRecordPaymentModal('${f.feeId}', '${f.studentName}', ${f.amountDue}, ${f.amountPaid})">
                                                    Record Payment
                                                </button>
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

<!-- Modal: Issue Fee Invoice -->
<div id="generateFeeModal" class="modal-backdrop">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/fees" method="post">
            <input type="hidden" name="action" value="generate" />
            <div class="modal-header">
                <h3>Generate Student Fee Invoice</h3>
                <button type="button" class="modal-close-btn" data-modal-close>&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Student</label>
                    <select name="studentId" class="form-control" required>
                        <option value="" disabled selected>-- Select Student --</option>
                        <c:forEach var="st" items="${studentList}">
                            <option value="${st.studentId}">${st.fullName} (${st.rollNumber} - ${st.course})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Amount Due ($)</label>
                    <input type="number" step="0.01" name="amountDue" placeholder="e.g. 500.00" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Payment Due Date</label>
                    <input type="date" name="dueDate" class="form-control" required />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Generate Bill</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Record Payment -->
<div id="recordPaymentModal" class="modal-backdrop">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/fees" method="post">
            <input type="hidden" name="action" value="recordPayment" />
            <input type="hidden" id="paymentFeeId" name="feeId" />
            <div class="modal-header">
                <h3>Record Payment Receipt</h3>
                <button type="button" class="modal-close-btn" data-modal-close>&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Student</label>
                    <div id="paymentStudentName" style="font-weight: var(--font-weight-bold); margin-bottom: var(--space-2);"></div>
                </div>
                <div class="form-group">
                    <label class="form-label">Outstanding Balance</label>
                    <div id="paymentAmountDue" style="color: var(--color-danger); font-weight: var(--font-weight-bold);"></div>
                </div>
                <div class="form-group">
                    <label class="form-label">Amount Paid ($)</label>
                    <input type="number" step="0.01" id="paymentAmountPaid" name="amountPaid" class="form-control" required />
                </div>
                <div class="form-group">
                    <label class="form-label">Payment Transaction Reference (UPI / Bank / Receipt #)</label>
                    <input type="text" name="transactionRef" placeholder="e.g. TXN-94829482" class="form-control" required />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Confirm Payment</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

