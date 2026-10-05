package com.hostelmanagement.auth;

import com.hostelmanagement.model.User;

public class AuthService {

    // Temporary Phase 1 users
    private static final User ADMIN =
            new User("admin", "admin123", "ADMIN", "Admin / Warden");

    private static final User STUDENT =
            new User("student", "student123", "STUDENT", "Student");

    public static User login(String username, String password) {

        if (username.equals(ADMIN.getUsername())
                && password.equals(ADMIN.getPassword())) {

            return ADMIN;
        }

        if (username.equals(STUDENT.getUsername())
                && password.equals(STUDENT.getPassword())) {

            return STUDENT;
        }

        return null;
    }
}