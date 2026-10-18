package com.hostelmanagement.model;

import java.sql.Date;

public class Fee {

    private final int feeId;
    private final int studentId;
    private final String studentName;
    private final String rollNumber;
    private final double amountDue;
    private final double amountPaid;
    private final Date dueDate;
    private final Date paymentDate;
    private final String paymentStatus;
    private final String transactionRef;

    public Fee(
            int feeId,
            int studentId,
            String studentName,
            String rollNumber,
            double amountDue,
            double amountPaid,
            Date dueDate,
            Date paymentDate,
            String paymentStatus,
            String transactionRef
    ) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.dueDate = dueDate;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
        this.transactionRef = transactionRef;
    }

    public int getFeeId() {
        return feeId;
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

    public double getAmountDue() {
        return amountDue;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getPendingAmount() {
        return Math.max(0, amountDue - amountPaid);
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getTransactionRef() {
        return transactionRef;
    }
}
