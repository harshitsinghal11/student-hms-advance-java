<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String regCtx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Student Registration" />
</jsp:include>

<main class="page-body auth-page">
    <div class="auth-card">
        <div class="auth-card-header">
            <img src="<%=regCtx%>/images/logo.svg" alt="CampusStay Emblem" class="auth-card-logo" width="36" height="36" />
            <h2>Create Account</h2>
            <p class="text-muted">Register as a student resident for hostel allotment</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="<%=regCtx%>/auth/register" method="post">
            <div class="form-group">
                <label for="fullName" class="form-label">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="form-control" placeholder="e.g. Amit Sharma" required />
            </div>
            <div class="form-group">
                <label for="username" class="form-label">Username</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="Choose a unique username" required />
            </div>
            <div class="form-group">
                <label for="password" class="form-label">Password (Min 8 Characters)</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="At least 8 characters" required minlength="8" />
            </div>
            <div class="form-group">
                <label for="confirmPassword" class="form-label">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Re-type your password" required minlength="8" />
            </div>
            <button type="submit" class="btn btn-primary">Register Account</button>
        </form>

        <p class="auth-switch">Already registered? <a href="<%=regCtx%>/auth/login">Back to Sign In</a></p>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
