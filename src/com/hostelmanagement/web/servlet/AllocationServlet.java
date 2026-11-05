package com.hostelmanagement.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.hostelmanagement.dao.AllocationDAO;
import com.hostelmanagement.dao.RoomDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Allocation;
import com.hostelmanagement.model.Room;
import com.hostelmanagement.model.Student;

public class AllocationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Allocation> allocationList = AllocationDAO.getAllAllocations();
            List<Room> availableRooms = RoomDAO.getAvailableRooms();
            List<Student> allStudents = StudentDAO.getAllStudents();

            List<Student> unallocatedStudents = new ArrayList<>();
            for (Student s : allStudents) {
                if (AllocationDAO.getActiveAllocationByStudentId(s.getStudentId()) == null) {
                    unallocatedStudents.add(s);
                }
            }

            if ("true".equals(request.getParameter("allocated"))) {
                request.setAttribute("message", "Room successfully allocated.");
            } else if ("true".equals(request.getParameter("transferred"))) {
                request.setAttribute("message", "Room transfer completed successfully.");
            } else if ("true".equals(request.getParameter("vacated"))) {
                request.setAttribute("message", "Room vacated successfully.");
            }

            request.setAttribute("allocationList", allocationList);
            request.setAttribute("availableRooms", availableRooms);
            request.setAttribute("unallocatedStudents", unallocatedStudents);

            request.getRequestDispatcher("/WEB-INF/views/admin/allocations.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error in AllocationServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("allocate".equalsIgnoreCase(action)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                int roomId = Integer.parseInt(request.getParameter("roomId"));

                boolean ok = AllocationDAO.allocateRoom(studentId, roomId);
                if (ok) {
                    response.sendRedirect(request.getContextPath() + "/admin/allocations?allocated=true");
                } else {
                    request.setAttribute("error", "Room allocation failed. Room might be full or student is already allocated.");
                    doGet(request, response);
                }
            } else if ("transfer".equalsIgnoreCase(action)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                int newRoomId = Integer.parseInt(request.getParameter("newRoomId"));

                boolean ok = AllocationDAO.changeRoom(studentId, newRoomId);
                if (ok) {
                    response.sendRedirect(request.getContextPath() + "/admin/allocations?transferred=true");
                } else {
                    request.setAttribute("error", "Room transfer failed. Target room might be full.");
                    doGet(request, response);
                }
            } else if ("vacate".equalsIgnoreCase(action)) {
                int allocationId = Integer.parseInt(request.getParameter("allocationId"));

                boolean ok = AllocationDAO.vacateRoom(allocationId);
                if (ok) {
                    response.sendRedirect(request.getContextPath() + "/admin/allocations?vacated=true");
                } else {
                    request.setAttribute("error", "Failed to vacate allocation.");
                    doGet(request, response);
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/allocations");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Operation failed: " + e.getMessage());
            doGet(request, response);
        }
    }
}
