package com.hostelmanagement.web.servlet;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.hostelmanagement.dao.FeeDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Fee;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.model.User;

public class FeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        try {
            if ("/student/fees".equals(servletPath)) {
                handleStudentFeesGet(request, response);
            } else {
                handleAdminFeesGet(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in FeeServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if ("/admin/fees".equals(servletPath)) {
            handleAdminFeesPost(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/student/fees");
        }
    }

    private void handleAdminFeesGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String filter = request.getParameter("filter");
        List<Fee> feeList;

        if ("PENDING".equalsIgnoreCase(filter)) {
            feeList = FeeDAO.getPendingFees();
            filter = "PENDING";
        } else {
            feeList = FeeDAO.getAllFees();
            filter = "ALL";
        }

        List<Student> studentList = StudentDAO.getAllStudents();

        if ("true".equals(request.getParameter("generated"))) {
            request.setAttribute("message", "Fee invoice generated successfully.");
        } else if ("true".equals(request.getParameter("paid"))) {
            request.setAttribute("message", "Payment recorded successfully.");
        }

        request.setAttribute("feeList", feeList);
        request.setAttribute("studentList", studentList);
        request.setAttribute("currentFilter", filter);

        request.getRequestDispatcher("/WEB-INF/views/admin/fees.jsp").forward(request, response);
    }

    private void handleStudentFeesGet(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Student student = StudentDAO.getStudentByUserId(user.getUserId());
        List<Fee> feeList = (student != null) ? FeeDAO.getFeesByStudentId(student.getStudentId()) : List.of();

        double totalDue = 0.0;
        double totalPaid = 0.0;
        for (Fee f : feeList) {
            totalDue += f.getAmountDue();
            totalPaid += f.getAmountPaid();
        }

        request.setAttribute("student", student);
        request.setAttribute("feeList", feeList);
        request.setAttribute("totalDue", totalDue);
        request.setAttribute("totalPaid", totalPaid);
        request.setAttribute("balance", Math.max(0, totalDue - totalPaid));

        request.getRequestDispatcher("/WEB-INF/views/student/fees.jsp").forward(request, response);
    }

    private void handleAdminFeesPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("generate".equalsIgnoreCase(action)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                double amountDue = Double.parseDouble(request.getParameter("amountDue"));
                Date dueDate = Date.valueOf(request.getParameter("dueDate"));

                boolean ok = FeeDAO.recordFee(studentId, amountDue, dueDate);
                if (ok) {
                    response.sendRedirect(request.getContextPath() + "/admin/fees?generated=true");
                } else {
                    request.setAttribute("error", "Failed to generate fee invoice.");
                    handleAdminFeesGet(request, response);
                }
            } else if ("recordPayment".equalsIgnoreCase(action)) {
                int feeId = Integer.parseInt(request.getParameter("feeId"));
                double amountPaid = Double.parseDouble(request.getParameter("amountPaid"));
                String transactionRef = request.getParameter("transactionRef");

                boolean ok = FeeDAO.recordPayment(feeId, amountPaid, transactionRef);
                if (ok) {
                    response.sendRedirect(request.getContextPath() + "/admin/fees?paid=true");
                } else {
                    request.setAttribute("error", "Failed to record payment.");
                    handleAdminFeesGet(request, response);
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/fees");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error processing fee transaction: " + e.getMessage());
            try {
                handleAdminFeesGet(request, response);
            } catch (SQLException ex) {
                throw new ServletException(ex);
            }
        }
    }
}
