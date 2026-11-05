package com.hostelmanagement.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.hostelmanagement.dao.RoomDAO;
import com.hostelmanagement.model.Room;

public class RoomServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String filter = request.getParameter("filter");
        List<Room> roomList;

        try {
            if ("AVAILABLE".equalsIgnoreCase(filter)) {
                roomList = RoomDAO.getAvailableRooms();
            } else if ("OCCUPIED".equalsIgnoreCase(filter)) {
                roomList = RoomDAO.getOccupiedRooms();
            } else {
                roomList = RoomDAO.getAllRooms();
                filter = "ALL";
            }

            if ("true".equals(request.getParameter("added"))) {
                request.setAttribute("message", "Room added successfully.");
            } else if ("true".equals(request.getParameter("updated"))) {
                request.setAttribute("message", "Room updated successfully.");
            } else if ("true".equals(request.getParameter("deleted"))) {
                request.setAttribute("message", "Room deleted successfully.");
            }

            request.setAttribute("roomList", roomList);
            request.setAttribute("currentFilter", filter.toUpperCase());
            request.getRequestDispatcher("/WEB-INF/views/admin/rooms.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error in RoomServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                String roomNumber = request.getParameter("roomNumber");
                String blockWing = request.getParameter("blockWing");
                int capacity = Integer.parseInt(request.getParameter("capacity"));
                double monthlyFee = Double.parseDouble(request.getParameter("monthlyFee"));

                boolean success = RoomDAO.addRoom(roomNumber.trim(), blockWing.trim(), capacity, monthlyFee);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/rooms?added=true");
                } else {
                    request.setAttribute("error", "Failed to add room. Room number may already exist.");
                    doGet(request, response);
                }
            } else if ("update".equalsIgnoreCase(action)) {
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                String roomNumber = request.getParameter("roomNumber");
                String blockWing = request.getParameter("blockWing");
                int capacity = Integer.parseInt(request.getParameter("capacity"));
                double monthlyFee = Double.parseDouble(request.getParameter("monthlyFee"));
                String roomStatus = request.getParameter("roomStatus");

                boolean success = RoomDAO.updateRoom(roomId, roomNumber.trim(), blockWing.trim(), capacity, monthlyFee, roomStatus);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/rooms?updated=true");
                } else {
                    request.setAttribute("error", "Failed to update room.");
                    doGet(request, response);
                }
            } else if ("delete".equalsIgnoreCase(action)) {
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                boolean success = RoomDAO.deleteRoom(roomId);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/rooms?deleted=true");
                } else {
                    request.setAttribute("error", "Cannot delete room with active occupants or history.");
                    doGet(request, response);
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/rooms");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            doGet(request, response);
        }
    }
}
