package com.hostelmanagement.model;

public class User {

    private final int userId;
    private final String fullName;
    private final String username;
    private final String role;

    public User(
            int userId,
            String fullName,
            String username,
            String role
    ) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
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

    public String getRole() {
        return role;
    }
}