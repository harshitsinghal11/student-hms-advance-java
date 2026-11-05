package com.hostelmanagement.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.hostelmanagement.dao.ReportDAO;

public class ReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String activeTab = request.getParameter("tab");
        if (activeTab == null || activeTab.trim().isEmpty()) {
            activeTab = "occupancy";
        }

        try {
            List<String[]> roomReport = ReportDAO.getRoomReport();
            List<String[]> studentReport = ReportDAO.getStudentReport();
            List<String[]> feeReport = ReportDAO.getFeeReport();
            List<String[]> complaintReport = ReportDAO.getComplaintReport();
            String occupancySummary = ReportDAO.getOccupancySummary();

            request.setAttribute("activeTab", activeTab);
            request.setAttribute("roomReport", roomReport);
            request.setAttribute("studentReport", studentReport);
            request.setAttribute("feeReport", feeReport);
            request.setAttribute("complaintReport", complaintReport);
            request.setAttribute("occupancySummary", occupancySummary);

            request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error generating reports", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
