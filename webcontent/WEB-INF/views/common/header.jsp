<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${not empty param.pageTitle ? param.pageTitle : (not empty pageTitle ? pageTitle : 'Hostel MS')} - Student Hostel Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
</head>
<body>
    <header class="app-header">
        <a href="${pageContext.request.contextPath}/" class="brand-title">Hostel MS</a>
        <div class="user-nav">
            <c:if test="${not empty sessionScope.currentUser}">
                <span class="user-greeting">Logged in as: <strong>${sessionScope.currentUser.fullName}</strong> (${sessionScope.currentUser.role})</span>
                <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-secondary">Logout</a>
            </c:if>
        </div>
    </header>

