package com.hostelmanagement.model;

import java.sql.Date;

public class Allocation {

    private final int allocationId;
    private final int studentId;
    private final String studentName;
    private final String rollNumber;
    private final int roomId;
    private final String roomNumber;
    private final String blockWing;
    private final Date allocationDate;
    private final Date vacateDate;
    private final String status;

    public Allocation(
            int allocationId,
            int studentId,
            String studentName,
            String rollNumber,
            int roomId,
            String roomNumber,
            String blockWing,
            Date allocationDate,
            Date vacateDate,
            String status
    ) {
        this.allocationId = allocationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.blockWing = blockWing;
        this.allocationDate = allocationDate;
        this.vacateDate = vacateDate;
        this.status = status;
    }

    public int getAllocationId() {
        return allocationId;
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

    public int getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getBlockWing() {
        return blockWing;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public Date getVacateDate() {
        return vacateDate;
    }

    public String getStatus() {
        return status;
    }
}
