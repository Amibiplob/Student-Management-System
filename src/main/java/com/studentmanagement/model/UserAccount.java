package com.studentmanagement.model;

import java.io.Serializable;

/** Safe profile and role data for sessions and views. Credentials are never stored here. */
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private Integer studentId;
    private String studentName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getRoleName() { return role == null ? "" : role.name(); }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}
