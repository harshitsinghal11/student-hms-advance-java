<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Fees & Receipts" />
</jsp:include>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="nav-item">Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/profile" class="nav-item">My Profile</a>
            <a href="${pageContext.request.contextPath}/student/room" class="nav-item">Allocated Room</a>
            <a href="${pageContext.request.contextPath}/student/fees" class="nav-item active">Fees & Receipts</a>
            <a href="${pageContext.request.contextPath}/student/complaints" class="nav-item">My Complaints</a>
            <a href="${pageContext.request.contextPath}/auth/change-password" class="nav-item">Change Password</a>
        </nav>
    </aside>

    <main class="main-content">
        <div class="page-body">
            <div class="page-header-row">
                <div>
                    <h1>Fee Ledger & Receipts</h1>
                    <p class="text-muted">Review your hostel accommodation invoices, clearance statuses, and payment history.</p>
                </div>
            </div>

            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Total Invoiced</div>
                    <div class="stat-value">$${totalDue}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Total Paid</div>
                    <div class="stat-value" style="color: var(--color-success);">$${totalPaid}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Outstanding Balance</div>
                    <div class="stat-value" style="color: ${balance > 0 ? 'var(--color-danger)' : 'var(--color-success)'};">
                        $${balance}
                    </div>
                </div>
            </div>

            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Invoice #</th>
                            <th>Amount Due</th>
                            <th>Amount Paid</th>
                            <th>Remaining</th>
                            <th>Due Date</th>
                            <th>Payment Date</th>
                            <th>Status</th>
                            <th>Transaction Ref</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty feeList}">
                                <tr>
                                    <td colspan="8" style="text-align: center; color: var(--color-text-muted); padding: var(--space-6);">
                                        No fee invoices recorded for your account.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="f" items="${feeList}">
                                    <tr>
                                        <td>#${f.feeId}</td>
                                        <td>$${f.amountDue}</td>
                                        <td>$${f.amountPaid}</td>
                                        <td style="color: ${f.amountDue > f.amountPaid ? 'var(--color-danger)' : 'var(--color-success)'};">
                                            $${f.amountDue - f.amountPaid}
                                        </td>
                                        <td>${f.dueDate}</td>
                                        <td>${empty f.paymentDate ? '-' : f.paymentDate}</td>
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

