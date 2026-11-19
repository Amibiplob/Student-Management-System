package com.studentmanagement.dao;

import com.studentmanagement.model.Department;
import com.studentmanagement.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** JDBC operations for departments and their student counts. */
public class DepartmentDAO {

    public List<Department> findAllDepartments() throws SQLException {
        String sql = "SELECT d.id, d.name, d.description, COUNT(s.id) AS student_count "
                + "FROM departments d LEFT JOIN students s ON s.department_id = d.id "
                + "GROUP BY d.id, d.name, d.description ORDER BY d.name, d.id";
        List<Department> departments = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                departments.add(mapDepartment(results));
            }
        }
        return departments;
    }

    public int countDepartments() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM departments");
             ResultSet results = statement.executeQuery()) {
            results.next();
            return results.getInt(1);
        }
    }

    public Department findDepartmentById(int id) throws SQLException {
        String sql = "SELECT d.id, d.name, d.description, COUNT(s.id) AS student_count "
                + "FROM departments d LEFT JOIN students s ON s.department_id = d.id "
                + "WHERE d.id = ? GROUP BY d.id, d.name, d.description";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapDepartment(results) : null;
            }
        }
    }

    public boolean exists(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT 1 FROM departments WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }
        }
    }

    public boolean addDepartment(Department department) throws SQLException {
        String sql = "INSERT INTO departments (name, description) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, department.getName());
            setDescription(statement, 2, department.getDescription());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean updateDepartment(Department department) throws SQLException {
        String sql = "UPDATE departments SET name = ?, description = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, department.getName());
            setDescription(statement, 2, department.getDescription());
            statement.setInt(3, department.getId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteDepartment(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int studentCount;
                try (PreparedStatement lock = connection.prepareStatement(
                        "SELECT id FROM departments WHERE id = ? FOR UPDATE")) {
                    lock.setInt(1, id);
                    try (ResultSet results = lock.executeQuery()) {
                        if (!results.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement count = connection.prepareStatement(
                        "SELECT COUNT(*) FROM students WHERE department_id = ?")) {
                    count.setInt(1, id);
                    try (ResultSet results = count.executeQuery()) {
                        results.next();
                        studentCount = results.getInt(1);
                    }
                }
                if (studentCount > 0) {
                    throw new SQLException("Department still has students.", "23503");
                }
                try (PreparedStatement delete = connection.prepareStatement(
                        "DELETE FROM departments WHERE id = ?")) {
                    delete.setInt(1, id);
                    boolean deleted = delete.executeUpdate() == 1;
                    connection.commit();
                    return deleted;
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private void setDescription(PreparedStatement statement, int index, String description)
            throws SQLException {
        if (description == null || description.isEmpty()) {
            statement.setNull(index, java.sql.Types.VARCHAR);
        } else {
            statement.setString(index, description);
        }
    }

    private Department mapDepartment(ResultSet results) throws SQLException {
        Department department = new Department();
        department.setId(results.getInt("id"));
        department.setName(results.getString("name"));
        department.setDescription(results.getString("description"));
        department.setStudentCount(results.getInt("student_count"));
        return department;
    }
}
