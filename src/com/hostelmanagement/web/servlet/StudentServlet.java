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

import com.hostelmanagement.dao.AllocationDAO;
import com.hostelmanagement.dao.ComplaintDAO;
import com.hostelmanagement.dao.FeeDAO;
import com.hostelmanagement.dao.RoomDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Allocation;
import com.hostelmanagement.model.Complaint;
import com.hostelmanagement.model.Fee;
import com.hostelmanagement.model.Room;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.model.User;

public class StudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        try {
            if ("/student/profile".equals(servletPath)) {
                handleProfileGet(request, response);
            } else if ("/student/room".equals(servletPath)) {
                handleRoomGet(request, response);
            } else {
                handleDashboardGet(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in StudentServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if ("/student/profile".equals(servletPath)) {
            handleProfilePost(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }

    private void handleDashboardGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Student student = StudentDAO.getStudentByUserId(user.getUserId());
        Allocation activeAlloc = null;
        double pendingDues = 0.0;
        long openComplaintsCount = 0;

        if (student != null) {
            activeAlloc = AllocationDAO.getActiveAllocationByStudentId(student.getStudentId());

            List<Fee> fees = FeeDAO.getFeesByStudentId(student.getStudentId());
            for (Fee f : fees) {
                if (!"PAID".equalsIgnoreCase(f.getPaymentStatus())) {
                    pendingDues += (f.getAmountDue() - f.getAmountPaid());
                }
            }

            List<Complaint> complaints = ComplaintDAO.getComplaintsByStudentId(student.getStudentId());
            openComplaintsCount = complaints.stream()
                    .filter(c -> !"RESOLVED".equalsIgnoreCase(c.getStatus()))
                    .count();
        }

        request.setAttribute("student", student);
        request.setAttribute("allocation", activeAlloc);
        request.setAttribute("pendingDues", pendingDues);
        request.setAttribute("openComplaintsCount", openComplaintsCount);

        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(request, response);
    }

    private void handleProfileGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Student student = StudentDAO.getStudentByUserId(user.getUserId());
        if ("true".equals(request.getParameter("updated"))) {
            request.setAttribute("message", "Profile details updated successfully.");
        }

        request.setAttribute("student", student);
        request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(request, response);
    }

    private void handleProfilePost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            Student student = StudentDAO.getStudentByUserId(user.getUserId());
            if (student == null) {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
                return;
            }

            String fullName = request.getParameter("fullName");
            String contactNumber = request.getParameter("contactNumber");
            String emergencyContact = request.getParameter("emergencyContact");
            String address = request.getParameter("address");

            boolean ok = StudentDAO.updateStudent(
                    student.getStudentId(),
                    fullName != null && !fullName.trim().isEmpty() ? fullName.trim() : student.getFullName(),
                    student.getCourse(),
                    contactNumber != null ? contactNumber.trim() : student.getContactNumber(),
                    emergencyContact != null ? emergencyContact.trim() : student.getEmergencyContact(),
                    address != null ? address.trim() : student.getAddress()
            );

            if (ok) {
                // Update session currentUser name if modified
                if (fullName != null && !fullName.trim().isEmpty()) {
                    session.setAttribute("currentUser", new User(user.getUserId(), fullName.trim(), user.getUsername(), user.getRole()));
                }
                response.sendRedirect(request.getContextPath() + "/student/profile?updated=true");
            } else {
                request.setAttribute("error", "Failed to update profile.");
                request.setAttribute("student", student);
                request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Error updating student profile", e);
        }
    }

    private void handleRoomGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Student student = StudentDAO.getStudentByUserId(user.getUserId());
        Allocation allocation = null;
        Room room = null;
        List<Allocation> roommates = List.of();

        if (student != null) {
            allocation = AllocationDAO.getActiveAllocationByStudentId(student.getStudentId());
            if (allocation != null) {
                room = RoomDAO.getRoomById(allocation.getRoomId());
                final int currentStudentId = student.getStudentId();
                final int allocatedRoomId = allocation.getRoomId();

                roommates = AllocationDAO.getAllAllocations().stream()
                        .filter(a -> a.getRoomId() == allocatedRoomId && "ACTIVE".equalsIgnoreCase(a.getStatus()) && a.getStudentId() != currentStudentId)
                        .collect(Collectors.toList());
            }
        }

        request.setAttribute("student", student);
        request.setAttribute("allocation", allocation);
        request.setAttribute("room", room);
        request.setAttribute("roommates", roommates);

        request.getRequestDispatcher("/WEB-INF/views/student/room.jsp").forward(request, response);
    }
}
