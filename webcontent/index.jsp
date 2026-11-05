<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Redirect to login servlet by default
    response.sendRedirect(request.getContextPath() + "/auth/login");
%>

