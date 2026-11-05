package com.hostelmanagement.web.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.hostelmanagement.model.User;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Filter initialization
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        String requestUri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String pathWithinApp = requestUri.substring(contextPath.length());

        if (currentUser == null) {
            resp.sendRedirect(contextPath + "/auth/login");
            return;
        }

        if (pathWithinApp.startsWith("/admin/") && !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator role required.");
            return;
        }

        if (pathWithinApp.startsWith("/student/") && !"STUDENT".equalsIgnoreCase(currentUser.getRole()) && !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Student role required.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic
    }
}
