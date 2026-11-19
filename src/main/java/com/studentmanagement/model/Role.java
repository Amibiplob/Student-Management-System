package com.studentmanagement.model;

public enum Role {
    ADMIN,
    TEACHER,
    STUDENT;

    public static Role fromDatabase(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A user role is required.");
        }
        return Role.valueOf(value.toUpperCase(java.util.Locale.ROOT));
    }
}
