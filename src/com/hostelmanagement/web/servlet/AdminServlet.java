package com.hostelmanagement.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.hostelmanagement.dao.AllocationDAO;
import com.hostelmanagement.dao.ComplaintDAO;
import com.hostelmanagement.dao.FeeDAO;
import com.hostelmanagement.dao.RoomDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Allocation;
import com.hostelmanagement.model.Complaint;
import com.hostelmanagement.model.Room;
import com.hostelmanagement.model.Student;

public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        try {
            if ("/admin/students".equals(servletPath)) {
                handleStudentsGet(request, response);
            } else {
                handleDashboardGet(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in AdminServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if ("/admin/students".equals(servletPath)) {
            handleStudentsPost(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }

    private void handleDashboardGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Student> students = StudentDAO.getAllStudents();
        List<Room> allRooms = RoomDAO.getAllRooms();
        List<Room> availableRooms = RoomDAO.getAvailableRooms();
        List<Room> occupiedRooms = RoomDAO.getOccupiedRooms();
        List<Allocation> allocations = AllocationDAO.getAllAllocations();
        List<Complaint> complaints = ComplaintDAO.getAllComplaints();

        long activeAllocations = allocations.stream()
                .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                .count();

        long pendingComplaints = complaints.stream()
                .filter(c -> "PENDING".equalsIgnoreCase(c.getStatus()))
                .count();

        int pendingFeesCount = FeeDAO.getPendingFees().size();

        request.setAttribute("totalStudents", students.size());
        request.setAttribute("totalRooms", allRooms.size());
        request.setAttribute("availableRooms", availableRooms.size());
        request.setAttribute("occupiedRooms", occupiedRooms.size());
        request.setAttribute("activeAllocations", activeAllocations);
        request.setAttribute("pendingComplaints", pendingComplaints);
        request.setAttribute("pendingFeesCount", pendingFeesCount);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    private void handleStudentsGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Student> studentList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            studentList = StudentDAO.searchStudents(keyword.trim());
            request.setAttribute("keyword", keyword.trim());
        } else {
            studentList = StudentDAO.getAllStudents();
        }

        if ("true".equals(request.getParameter("added"))) {
            request.setAttribute("message", "Student created successfully.");
        } else if ("true".equals(request.getParameter("updated"))) {
            request.setAttribute("message", "Student profile updated successfully.");
        } else if ("true".equals(request.getParameter("deleted"))) {
            request.setAttribute("message", "Student deleted successfully.");
        }

        request.setAttribute("studentList", studentList);
        request.getRequestDispatcher("/WEB-INF/views/admin/students.jsp").forward(request, response);
    }

    private void handleStudentsPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                String fullName = request.getParameter("fullName");
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String rollNumber = request.getParameter("rollNumber");
                String course = request.getParameter("course");
                String contactNumber = request.getParameter("contactNumber");
                String emergencyContact = request.getParameter("emergencyContact");
                String address = request.getParameter("address");

                boolean success = StudentDAO.addStudentWithAccount(
                        fullName, username, password, rollNumber, course, contactNumber, emergencyContact, address
                );

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/students?added=true");
                } else {
                    request.setAttribute("error", "Failed to add student. Username or Roll Number may already exist.");
                    handleStudentsGet(request, response);
                }
            } else if ("update".equalsIgnoreCase(action)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                String fullName = request.getParameter("fullName");
                String rollNumber = request.getParameter("rollNumber");
                String course = request.getParameter("course");
                String contactNumber = request.getParameter("contactNumber");
                String emergencyContact = request.getParameter("emergencyContact");
                String address = request.getParameter("address");

                boolean success = StudentDAO.updateStudent(
                        studentId, fullName, rollNumber, course, contactNumber, emergencyContact, address
                );

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/students?updated=true");
                } else {
                    request.setAttribute("error", "Failed to update student profile.");
                    handleStudentsGet(request, response);
                }
            } else if ("delete".equalsIgnoreCase(action)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                boolean success = StudentDAO.deleteStudent(studentId);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/students?deleted=true");
                } else {
                    request.setAttribute("error", "Failed to delete student (may have active room or fee records).");
                    handleStudentsGet(request, response);
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/students");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            try {
                handleStudentsGet(request, response);
            } catch (SQLException ex) {
                throw new ServletException(ex);
            }
        }
    }
}
