package com.hostelmanagement.model;

import java.sql.Timestamp;

public class Complaint {

    private final int complaintId;
    private final int studentId;
    private final String studentName;
    private final String rollNumber;
    private final String roomNumber;
    private final String category;
    private final String description;
    private final String status;
    private final String resolutionRemarks;
    private final Timestamp filedAt;
    private final Timestamp resolvedAt;

    public Complaint(
            int complaintId,
            int studentId,
            String studentName,
            String rollNumber,
            String roomNumber,
            String category,
            String description,
            String status,
            String resolutionRemarks,
            Timestamp filedAt,
            Timestamp resolvedAt
    ) {
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.roomNumber = roomNumber;
        this.category = category;
        this.description = description;
        this.status = status;
        this.resolutionRemarks = resolutionRemarks;
        this.filedAt = filedAt;
        this.resolvedAt = resolvedAt;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getResolutionRemarks() {
        return resolutionRemarks;
    }

    public Timestamp getFiledAt() {
        return filedAt;
    }

    public Timestamp getResolvedAt() {
        return resolvedAt;
    }
}
