<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Student Registration" />
</jsp:include>

<main class="page-body auth-page">
    <div class="auth-card">
        <h2>Register as Student</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/auth/register" method="post">
            <div class="form-group">
                <label for="fullName" class="form-label">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="form-control" required />
            </div>
            <div class="form-group">
                <label for="username" class="form-label">Username</label>
                <input type="text" id="username" name="username" class="form-control" required />
            </div>
            <div class="form-group">
                <label for="password" class="form-label">Password (Min 8 Characters)</label>
                <input type="password" id="password" name="password" class="form-control" required minlength="8" />
            </div>
            <div class="form-group">
                <label for="confirmPassword" class="form-label">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required minlength="8" />
            </div>
            <button type="submit" class="btn btn-primary">Create Account</button>
        </form>
        <p class="auth-switch">Already registered? <a href="${pageContext.request.contextPath}/auth/login">Back to Login</a></p>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

