package com.studentmanagement.security;

import com.studentmanagement.model.Role;
import com.studentmanagement.model.UserAccount;

/** Server-side role rules shared by the request filter and direct route checks. */
public final class AuthorizationPolicy {

    private AuthorizationPolicy() {
    }

    public static boolean allows(UserAccount user, String path, String method,
            String action, String recordId) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        Role role = user.getRole();
        boolean get = "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method);
        boolean write = !get && !"OPTIONS".equalsIgnoreCase(method);

        if ("/dashboard".equals(path)) {
            return get;
        }
        if ("/logout".equals(path)) {
            return "POST".equalsIgnoreCase(method);
        }
        if ("/students".equals(path)) {
            if (role == Role.ADMIN) {
                return true;
            }
            if (role == Role.TEACHER) {
                return get && (action == null || action.isEmpty() || "view".equals(action));
            }
            return get && "view".equals(action) && user.getStudentId() != null
                    && String.valueOf(user.getStudentId()).equals(recordId);
        }
        if ("/departments".equals(path)) {
            if (role == Role.ADMIN) {
                return true;
            }
            return role == Role.TEACHER && get && (action == null || action.isEmpty());
        }
        if ("/users".equals(path)) {
            return role == Role.ADMIN;
        }

        if ("/dashboard.jsp".equals(path)) {
            return get && (role == Role.ADMIN || role == Role.TEACHER);
        }
        if ("/student-dashboard.jsp".equals(path)) {
            return false; // Only the dashboard servlet may forward here after loading the linked record.
        }
        if ("/students.jsp".equals(path) || "/student-details.jsp".equals(path)) {
            return get && (role == Role.ADMIN || role == Role.TEACHER);
        }
        if ("/add-student.jsp".equals(path) || "/edit-student.jsp".equals(path)) {
            return get && role == Role.ADMIN;
        }
        if ("/departments.jsp".equals(path)) {
            return get && (role == Role.ADMIN || role == Role.TEACHER);
        }
        if ("/users.jsp".equals(path)) {
            return get && role == Role.ADMIN;
        }
        return false;
    }
}
