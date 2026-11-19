package com.studentmanagement.dao;

import com.studentmanagement.model.Role;
import com.studentmanagement.model.Student;
import com.studentmanagement.model.UserAccount;
import com.studentmanagement.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** JDBC access to app_users. */
public class UserDAO {

    private static final String USER_COLUMNS =
            "u.id, u.full_name, u.email, u.role, u.is_active, u.student_id";

    public UserAccount authenticate(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT " + USER_COLUMNS + ", u.password_hash FROM app_users u "
                + "WHERE LOWER(u.email) = LOWER(?) LIMIT 1";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim());
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    return null;
                }
                String storedPassword = results.getString("password_hash");
                boolean passwordMatches = password != null && password.equals(storedPassword);
                if (!passwordMatches || !results.getBoolean("is_active")) {
                    return null;
                }
                return mapUser(results, false);
            }
        }
    }

    public UserAccount findActiveById(int id) throws SQLException {
        String sql = "SELECT " + USER_COLUMNS + " FROM app_users u "
                + "WHERE u.id = ? AND u.is_active = TRUE";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapUser(results, false) : null;
            }
        }
    }

    public List<UserAccount> findAll() throws SQLException {
        String sql = "SELECT " + USER_COLUMNS + ", s.name AS student_name "
                + "FROM app_users u LEFT JOIN students s ON s.id = u.student_id "
                + "ORDER BY LOWER(u.email), u.id";
        List<UserAccount> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                users.add(mapUser(results, true));
            }
        }
        return users;
    }

    public UserAccount findById(int id) throws SQLException {
        String sql = "SELECT " + USER_COLUMNS + ", s.name AS student_name "
                + "FROM app_users u LEFT JOIN students s ON s.id = u.student_id WHERE u.id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapUser(results, true) : null;
            }
        }
    }

    public List<Student> findAvailableStudents(Integer accountId) throws SQLException {
        String sql = "SELECT s.id, s.name, s.email FROM students s WHERE NOT EXISTS ("
                + "SELECT 1 FROM app_users u WHERE u.student_id = s.id";
        if (accountId != null) {
            sql += " AND u.id <> ?";
        }
        sql += ") ORDER BY LOWER(s.name), s.id";
        List<Student> students = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (accountId != null) {
                statement.setInt(1, accountId);
            }
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Student student = new Student();
                    student.setId(results.getInt("id"));
                    student.setName(results.getString("name"));
                    student.setEmail(results.getString("email"));
                    students.add(student);
                }
            }
        }
        return students;
    }

    public boolean isStudentAvailable(int studentId, Integer accountId) throws SQLException {
        String sql = "SELECT 1 FROM students s WHERE s.id = ? AND NOT EXISTS ("
                + "SELECT 1 FROM app_users u WHERE u.student_id = s.id";
        if (accountId != null) {
            sql += " AND u.id <> ?";
        }
        sql += ")";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            if (accountId != null) {
                statement.setInt(2, accountId);
            }
            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }
        }
    }

    public boolean create(UserAccount user, String passwordValue) throws SQLException {
        String sql = "INSERT INTO app_users (full_name, email, password_hash, role, is_active, student_id) "
                + "VALUES (?, ?, ?, ?, TRUE, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, passwordValue);
            statement.setString(4, user.getRole().name());
            setStudentId(statement, 5, user.getStudentId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean update(UserAccount user, String passwordValue) throws SQLException {
        String sql = "UPDATE app_users SET full_name = ?, email = ?, role = ?, student_id = ?, "
                + "password_hash = COALESCE(?, password_hash), updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                UserAccount existing = findForUpdate(connection, user.getId());
                if (existing == null) {
                    connection.rollback();
                    return false;
                }
                if (existing.getRole() == Role.ADMIN && existing.isActive()
                        && user.getRole() != Role.ADMIN
                        && lockAndCountActiveAdministrators(connection) <= 1) {
                    throw new LastAdministratorException();
                }
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, user.getFullName());
                    statement.setString(2, user.getEmail());
                    statement.setString(3, user.getRole().name());
                    setStudentId(statement, 4, user.getStudentId());
                    if (passwordValue == null) {
                        statement.setNull(5, Types.VARCHAR);
                    } else {
                        statement.setString(5, passwordValue);
                    }
                    statement.setInt(6, user.getId());
                    boolean changed = statement.executeUpdate() == 1;
                    connection.commit();
                    return changed;
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    public boolean setActive(int id, boolean active) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                UserAccount existing = findForUpdate(connection, id);
                if (existing == null) {
                    connection.rollback();
                    return false;
                }
                if (!active && existing.getRole() == Role.ADMIN && existing.isActive()
                        && lockAndCountActiveAdministrators(connection) <= 1) {
                    throw new LastAdministratorException();
                }
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE app_users SET is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?")) {
                    statement.setBoolean(1, active);
                    statement.setInt(2, id);
                    boolean changed = statement.executeUpdate() == 1;
                    connection.commit();
                    return changed;
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private UserAccount findForUpdate(Connection connection, int id) throws SQLException {
        String sql = "SELECT " + USER_COLUMNS + " FROM app_users u WHERE u.id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapUser(results, false) : null;
            }
        }
    }

    private int lockAndCountActiveAdministrators(Connection connection) throws SQLException {
        int count = 0;
        String sql = "SELECT id FROM app_users WHERE role = 'ADMIN' AND is_active = TRUE "
                + "ORDER BY id FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                count++;
            }
        }
        return count;
    }

    private void setStudentId(PreparedStatement statement, int index, Integer studentId)
            throws SQLException {
        if (studentId == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, studentId);
        }
    }

    private UserAccount mapUser(ResultSet results, boolean includesStudentName) throws SQLException {
        UserAccount user = new UserAccount();
        user.setId(results.getInt("id"));
        user.setFullName(results.getString("full_name"));
        user.setEmail(results.getString("email"));
        try {
            user.setRole(Role.fromDatabase(results.getString("role")));
        } catch (IllegalArgumentException exception) {
            throw new SQLException("The database contains an unsupported user role.", exception);
        }
        user.setActive(results.getBoolean("is_active"));
        int studentId = results.getInt("student_id");
        user.setStudentId(results.wasNull() ? null : studentId);
        if (includesStudentName) {
            user.setStudentName(results.getString("student_name"));
        }
        return user;
    }

    public static final class LastAdministratorException extends SQLException {
        private static final long serialVersionUID = 1L;

        private LastAdministratorException() {
            super("At least one active administrator must remain.", "P0001");
        }
    }
}
