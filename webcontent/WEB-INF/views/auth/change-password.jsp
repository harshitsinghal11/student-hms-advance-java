<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Change Password" />
</jsp:include>

<main class="page-body auth-page">
    <div class="auth-card">
        <h2>Change Password</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/auth/change-password" method="post">
            <div class="form-group">
                <label for="oldPassword" class="form-label">Current Password</label>
                <input type="password" id="oldPassword" name="oldPassword" class="form-control" required />
            </div>
            <div class="form-group">
                <label for="newPassword" class="form-label">New Password</label>
                <input type="password" id="newPassword" name="newPassword" class="form-control" required minlength="8" />
            </div>
            <div class="form-group">
                <label for="confirmPassword" class="form-label">Confirm New Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required minlength="8" />
            </div>
            <button type="submit" class="btn btn-primary">Update Password</button>
        </form>
    </div>
</main>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

