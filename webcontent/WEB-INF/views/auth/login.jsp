<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String loginCtx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Sign In" />
</jsp:include>

<main class="page-body auth-page">
    <div class="auth-card">
        <div class="auth-card-header">
            <img src="<%=loginCtx%>/images/logo.svg" alt="CampusStay Emblem" class="auth-card-logo" width="36" height="36" />
            <h2>Sign In</h2>
            <p class="text-muted">Enter your credentials to access the hostel portal</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>

        <form action="<%=loginCtx%>/auth/login" method="post">
            <div class="form-group">
                <label for="username" class="form-label">Username</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="e.g. amit118 or admin" required autofocus />
            </div>
            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Enter your account password" required />
            </div>
            <button type="submit" class="btn btn-primary">Sign In</button>
        </form>

        <p class="auth-switch">Don't have an account? <a href="<%=loginCtx%>/auth/register">Register as Student</a></p>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
