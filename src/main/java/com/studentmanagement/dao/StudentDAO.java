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

/** JDBC operations for student records. SQL failures are returned to the caller. */
public class StudentDAO {

    private static final String SELECT_STUDENTS =
            "SELECT s.*, d.name AS department_name FROM students s "
            + "LEFT JOIN departments d ON s.department_id = d.id ";

    public List<Student> findStudents(String search, Integer departmentId,
            boolean unassignedOnly) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_STUDENTS);
        List<String> conditions = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            conditions.add("(s.name ILIKE ? OR s.email ILIKE ? OR s.phone ILIKE ?)");
        }
        if (unassignedOnly) {
            conditions.add("s.department_id IS NULL");
        } else if (departmentId != null) {
            conditions.add("s.department_id = ?");
        }
        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(' ');
        }
        sql.append("ORDER BY s.name, s.id");

        List<Student> students = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int parameter = 1;
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim() + "%";
                statement.setString(parameter++, pattern);
                statement.setString(parameter++, pattern);
                statement.setString(parameter++, pattern);
            }
            if (departmentId != null && !unassignedOnly) {
                statement.setInt(parameter, departmentId);
            }
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    students.add(mapStudent(results));
                }
            }
        }
        return students;
    }

    public List<Student> findRecentStudents(int limit) throws SQLException {
        String sql = SELECT_STUDENTS + "ORDER BY s.created_at DESC NULLS LAST, s.id DESC LIMIT ?";
        List<Student> students = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    students.add(mapStudent(results));
                }
            }
        }
        return students;
    }

    public int countStudents() throws SQLException {
        return count("SELECT COUNT(*) FROM students");
    }

    private int count(String sql) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            results.next();
            return results.getInt(1);
        }
    }

    public Student findStudentById(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     SELECT_STUDENTS + "WHERE s.id = ?")) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapStudent(results) : null;
            }
        }
    }

    public boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students "
                + "(name, email, phone, gender, date_of_birth, address, department_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setStudentFields(statement, student);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, gender = ?, "
                + "date_of_birth = ?, address = ?, department_id = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setStudentFields(statement, student);
            statement.setInt(8, student.getId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteStudent(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM students WHERE id = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private void setStudentFields(PreparedStatement statement, Student student)
            throws SQLException {
        statement.setString(1, student.getName());
        statement.setString(2, student.getEmail());
        setOptionalString(statement, 3, student.getPhone());
        setOptionalString(statement, 4, student.getGender());
        if (student.getDateOfBirth() == null || student.getDateOfBirth().isEmpty()) {
            statement.setNull(5, Types.DATE);
        } else {
            statement.setDate(5, Date.valueOf(student.getDateOfBirth()));
        }
        setOptionalString(statement, 6, student.getAddress());
        if (student.getDepartmentId() == null) {
            statement.setNull(7, Types.INTEGER);
        } else {
            statement.setInt(7, student.getDepartmentId());
        }
    }

    private void setOptionalString(PreparedStatement statement, int index, String value)
            throws SQLException {
        if (value == null || value.isEmpty()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    private Student mapStudent(ResultSet results) throws SQLException {
        Student student = new Student();
        student.setId(results.getInt("id"));
        student.setName(results.getString("name"));
        student.setEmail(results.getString("email"));
        student.setPhone(results.getString("phone"));
        student.setGender(results.getString("gender"));
        Date dateOfBirth = results.getDate("date_of_birth");
        student.setDateOfBirth(dateOfBirth == null ? null : dateOfBirth.toString());
        student.setAddress(results.getString("address"));
        int departmentId = results.getInt("department_id");
        student.setDepartmentId(results.wasNull() ? null : departmentId);
        student.setDepartmentName(results.getString("department_name"));
        student.setCreatedAt(results.getTimestamp("created_at"));
        return student;
    }
}
