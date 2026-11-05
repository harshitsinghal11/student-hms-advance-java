<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Login" />
</jsp:include>

<main class="page-body auth-page">
    <div class="auth-card">
        <h2>Sign In</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <div class="form-group">
                <label for="username" class="form-label">Username</label>
                <input type="text" id="username" name="username" class="form-control" required autofocus />
            </div>
            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-control" required />
            </div>
            <button type="submit" class="btn btn-primary">Log In</button>
        </form>
        <p class="auth-switch">Don't have an account? <a href="${pageContext.request.contextPath}/auth/register">Register as Student</a></p>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

