package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import com.studentmanagement.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, d.name AS department_name "
                + "FROM students s "
                + "LEFT JOIN departments d ON s.department_id = d.id "
                + "ORDER BY s.id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students "
                + "(name, email, phone, gender, date_of_birth, address, department_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setStudentFields(ps, student, false);
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students "
                + "SET name=?, email=?, phone=?, gender=?, "
                + "date_of_birth=?, address=?, department_id=? "
                + "WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setStudentFields(ps, student, false);
            ps.setInt(8, student.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Student getStudentById(int id) {
        String sql = "SELECT s.*, d.name AS department_name "
                + "FROM students s "
                + "LEFT JOIN departments d ON s.department_id = d.id "
                + "WHERE s.id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, d.name AS department_name "
                + "FROM students s "
                + "LEFT JOIN departments d ON s.department_id = d.id "
                + "WHERE LOWER(s.name) LIKE LOWER(?) "
                + "OR LOWER(s.email) LIKE LOWER(?) "
                + "ORDER BY s.id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    private void setStudentFields(PreparedStatement ps, Student student,
                                  boolean includeId)
            throws SQLException {
        ps.setString(1, student.getName());
        ps.setString(2, student.getEmail());
        ps.setString(3, student.getPhone());
        ps.setString(4, student.getGender());

        if (student.getDateOfBirth() == null
                || student.getDateOfBirth().trim().isEmpty()) {
            ps.setNull(5, Types.DATE);
        } else {
            ps.setDate(5, Date.valueOf(student.getDateOfBirth()));
        }

        ps.setString(6, student.getAddress());

        if (student.getDepartmentId() > 0) {
            ps.setInt(7, student.getDepartmentId());
        } else {
            ps.setNull(7, Types.INTEGER);
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setGender(rs.getString("gender"));

        Date dob = rs.getDate("date_of_birth");
        student.setDateOfBirth(dob == null ? null : dob.toString());

        student.setAddress(rs.getString("address"));
        student.setDepartmentId(rs.getInt("department_id"));
        student.setDepartmentName(rs.getString("department_name"));
        return student;
    }
}
