<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${not empty param.pageTitle ? param.pageTitle : (not empty pageTitle ? pageTitle : 'Hostel MS')} - Student Hostel Management System</title>
    <link rel="icon" type="image/svg+xml" href="<%=ctx%>/images/logo.svg">
    <!-- Primary Stylesheet with Cache-Busting -->
    <link rel="stylesheet" href="<%=ctx%>/css/global.css?v=2.5">

    <!-- Critical CSS safeguard to guarantee instant styling regardless of container caching -->
    <style>
        :root {
            --color-bg-canvas: #f8fafc;
            --color-bg-surface: #ffffff;
            --color-text-primary: #0f172a;
            --color-border-subtle: #e2e8f0;
            --color-brand-primary: #1e40af;
            --header-height: 58px;
        }
        [data-theme="dark"] {
            --color-bg-canvas: #090d16;
            --color-bg-surface: #111827;
            --color-text-primary: #f8fafc;
            --color-border-subtle: #1e293b;
            --color-brand-primary: #3b82f6;
        }
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            background-color: var(--color-bg-canvas);
            color: var(--color-text-primary);
            line-height: 1.5;
            -webkit-font-smoothing: antialiased;
        }
        .app-header {
            height: var(--header-height);
            background-color: var(--color-bg-surface);
            border-bottom: 1px solid var(--color-border-subtle);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 1.5rem;
            position: sticky;
            top: 0;
            z-index: 100;
        }
        .brand-logo { width: 28px; height: 28px; max-width: 28px; max-height: 28px; display: block; }
    </style>
</head>
<body>
    <header class="app-header">
        <div class="brand-group">
            <c:if test="${not empty sessionScope.currentUser}">
                <button type="button" class="sidebar-toggle-btn" id="sidebarToggle" aria-label="Toggle Navigation Menu">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="3" y1="12" x2="21" y2="12"></line>
                        <line x1="3" y1="6" x2="21" y2="6"></line>
                        <line x1="3" y1="18" x2="21" y2="18"></line>
                    </svg>
                </button>
            </c:if>
            <a href="<%=ctx%>/" class="brand-link">
                <img src="<%=ctx%>/images/logo.svg" alt="CampusStay Logo" class="brand-logo" width="28" height="28" />
                <span class="brand-title">CampusStay</span>
            </a>
        </div>
        <div class="header-actions">
            <button type="button" class="theme-toggle-btn" id="themeToggle" aria-label="Toggle color theme" title="Toggle color theme">
                <svg class="sun-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="5"></circle>
                    <line x1="12" y1="1" x2="12" y2="3"></line>
                    <line x1="12" y1="21" x2="12" y2="23"></line>
                    <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
                    <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
                    <line x1="1" y1="12" x2="3" y2="12"></line>
                    <line x1="21" y1="12" x2="23" y2="12"></line>
                    <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
                    <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
                </svg>
                <svg class="moon-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
                </svg>
            </button>
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <div class="user-profile-badge">
                        <span class="user-avatar-initial">${sessionScope.currentUser.fullName.substring(0, 1).toUpperCase()}</span>
                        <div class="user-info-text">
                            <span class="user-fullname">${sessionScope.currentUser.fullName}</span>
                            <span class="user-role-pill">${sessionScope.currentUser.role}</span>
                        </div>
                    </div>
                    <a href="<%=ctx%>/auth/logout" class="btn btn-secondary btn-sm">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="<%=ctx%>/auth/login" class="nav-auth-link">Sign In</a>
                </c:otherwise>
            </c:choose>
        </div>
    </header>
