package com.hostelmanagement.model;

public class Student {

    private final int studentId;
    private final int userId;
    private final String fullName;
    private final String username;
    private final String rollNumber;
    private final String course;
    private final String contactNumber;
    private final String emergencyContact;
    private final String address;

    public Student(
            int studentId,
            int userId,
            String fullName,
            String username,
            String rollNumber,
            String course,
            String contactNumber,
            String emergencyContact,
            String address
    ) {
        this.studentId = studentId;
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.rollNumber = rollNumber;
        this.course = course;
        this.contactNumber = contactNumber;
        this.emergencyContact = emergencyContact;
        this.address = address;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getCourse() {
        return course;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return rollNumber + " - " + fullName;
    }
}
