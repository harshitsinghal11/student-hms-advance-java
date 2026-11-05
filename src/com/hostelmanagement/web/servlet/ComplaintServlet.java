package com.hostelmanagement.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.hostelmanagement.dao.ComplaintDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Complaint;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.model.User;

public class ComplaintServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        try {
            if ("/student/complaints".equals(servletPath)) {
                handleStudentComplaintsGet(request, response);
            } else {
                handleAdminComplaintsGet(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in ComplaintServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        try {
            if ("/student/complaints".equals(servletPath)) {
                handleStudentComplaintPost(request, response);
            } else {
                handleAdminComplaintPost(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Error handling complaint action", e);
        }
    }

    private void handleAdminComplaintsGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String filter = request.getParameter("filter");
        List<Complaint> all = ComplaintDAO.getAllComplaints();
        List<Complaint> filtered;

        if (filter != null && !filter.isEmpty() && !"ALL".equalsIgnoreCase(filter)) {
            final String currentStatus = filter;
            filtered = all.stream()
                    .filter(c -> currentStatus.equalsIgnoreCase(c.getStatus()))
                    .collect(Collectors.toList());
        } else {
            filtered = all;
            filter = "ALL";
        }

        if ("true".equals(request.getParameter("updated"))) {
            request.setAttribute("message", "Complaint status updated successfully.");
        }

        request.setAttribute("complaintList", filtered);
        request.setAttribute("currentFilter", filter.toUpperCase());
        request.getRequestDispatcher("/WEB-INF/views/admin/complaints.jsp").forward(request, response);
    }

    private void handleStudentComplaintsGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Student student = StudentDAO.getStudentByUserId(user.getUserId());
        List<Complaint> complaints = (student != null)
                ? ComplaintDAO.getComplaintsByStudentId(student.getStudentId())
                : List.of();

        if ("true".equals(request.getParameter("submitted"))) {
            request.setAttribute("message", "Your complaint has been submitted successfully.");
        }

        request.setAttribute("student", student);
        request.setAttribute("complaintList", complaints);
        request.getRequestDispatcher("/WEB-INF/views/student/complaints.jsp").forward(request, response);
    }

    private void handleStudentComplaintPost(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Student student = StudentDAO.getStudentByUserId(user.getUserId());
        if (student == null) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard?error=no_profile");
            return;
        }

        String category = request.getParameter("category");
        String description = request.getParameter("description");

        boolean ok = ComplaintDAO.submitComplaint(student.getStudentId(), category, description);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/student/complaints?submitted=true");
        } else {
            response.sendRedirect(request.getContextPath() + "/student/complaints?error=failed");
        }
    }

    private void handleAdminComplaintPost(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int complaintId = Integer.parseInt(request.getParameter("complaintId"));
        String status = request.getParameter("status");
        String remarks = request.getParameter("remarks");

        boolean ok = ComplaintDAO.updateComplaintStatus(complaintId, status, remarks);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/admin/complaints?updated=true");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/complaints?error=failed");
        }
    }
}
