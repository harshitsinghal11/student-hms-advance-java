package com.hostelmanagement.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.hostelmanagement.auth.AuthService;
import com.hostelmanagement.model.User;

public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equalsIgnoreCase("/login")) {
            handleLoginGet(request, response);
        } else if (pathInfo.equalsIgnoreCase("/register")) {
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        } else if (pathInfo.equalsIgnoreCase("/logout")) {
            handleLogout(request, response);
        } else if (pathInfo.equalsIgnoreCase("/change-password")) {
            handleChangePasswordGet(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equalsIgnoreCase("/login")) {
            handleLoginPost(request, response);
        } else if (pathInfo.equalsIgnoreCase("/register")) {
            handleRegisterPost(request, response);
        } else if (pathInfo.equalsIgnoreCase("/change-password")) {
            handleChangePasswordPost(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }

    private void handleLoginGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            } else {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
                return;
            }
        }

        if ("true".equals(request.getParameter("registered"))) {
            request.setAttribute("message", "Registration successful! You can now log in.");
        } else if ("true".equals(request.getParameter("loggedOut"))) {
            request.setAttribute("message", "You have been logged out successfully.");
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    private void handleLoginPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Username and password are required.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = AuthService.login(username.trim(), password);
            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("currentUser", user);
                session.setAttribute("userRole", user.getRole());

                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/student/dashboard");
                }
            } else {
                request.setAttribute("error", "Invalid username or password.");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error occurred during login. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    private void handleRegisterPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (fullName == null || fullName.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 8) {
            request.setAttribute("error", "Password must be at least 8 characters.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        try {
            boolean success = AuthService.registerStudent(fullName.trim(), username.trim(), password);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/auth/login?registered=true");
            } else {
                request.setAttribute("error", "Username already exists. Please choose a different username.");
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Registration error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }

    private void handleChangePasswordGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
    }

    private void handleChangePasswordPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (oldPassword == null || newPassword == null || newPassword.length() < 8) {
            request.setAttribute("error", "New password must be at least 8 characters long.");
            request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New passwords do not match.");
            request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
            return;
        }

        try {
            boolean updated = AuthService.changePassword(user.getUserId(), oldPassword, newPassword);
            if (updated) {
                request.setAttribute("message", "Password updated successfully.");
            } else {
                request.setAttribute("error", "Current password is incorrect.");
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/auth/login?loggedOut=true");
    }
}
