package com.studentmanagement.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Opt-in setup for the three academic demonstration accounts. */
public final class InitialUserSeeder {

    private static final Logger LOGGER = Logger.getLogger(InitialUserSeeder.class.getName());
    private static final String DEMO_PASSWORD = "123456";
    private static final String RAHIM_NAME = "Rahim Ahmed";
    private static final String RAHIM_EMAIL = "rahim@example.com";

    public static void main(String[] args) {
        new InitialUserSeeder().seed();
    }

    public void seed() {
        if (!"true".equalsIgnoreCase(System.getenv("DEMO_ACCOUNTS_ENABLED"))) {
            LOGGER.info("Demo account setup skipped; set DEMO_ACCOUNTS_ENABLED=true for one application startup to create or update the demonstration accounts.");
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                ensureStaffAccount(connection, "CampusDesk Administrator", "admin@admin.com", "ADMIN");
                ensureStaffAccount(connection, "CampusDesk Teacher", "teacher@teacher.com", "TEACHER");
                int rahimId = findUniqueRahimId(connection);
                ensureStudentAccount(connection, rahimId);
                connection.commit();
                LOGGER.info("The ADMIN, TEACHER, and STUDENT demonstration accounts were created or updated.");
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Demo accounts could not be initialized. Check the Neon schema and database configuration.", exception);
        }
    }

    private void ensureStaffAccount(Connection connection, String fullName, String email, String role)
            throws SQLException {
        String sql = "INSERT INTO app_users (full_name, email, password_hash, role, is_active, student_id) "
                + "VALUES (?, ?, ?, ?, TRUE, NULL) "
                + "ON CONFLICT (LOWER(email)) DO UPDATE SET password_hash = EXCLUDED.password_hash, "
                + "role = EXCLUDED.role, is_active = TRUE, student_id = NULL, "
                + "updated_at = CURRENT_TIMESTAMP";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, DEMO_PASSWORD);
            statement.setString(4, role);
            statement.executeUpdate();
        }
    }

    private int findUniqueRahimId(Connection connection) throws SQLException {
        String sql = "SELECT id FROM students WHERE LOWER(name) = LOWER(?) "
                + "AND LOWER(email) = LOWER(?) FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, RAHIM_NAME);
            statement.setString(2, RAHIM_EMAIL);
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    throw new SQLException("No existing Rahim Ahmed student row matched rahim@example.com.");
                }
                int id = results.getInt("id");
                if (results.next()) {
                    throw new SQLException("More than one student row matched Rahim Ahmed / rahim@example.com.");
                }
                return id;
            }
        }
    }

    private void ensureStudentAccount(Connection connection, int studentId) throws SQLException {
        String sql = "INSERT INTO app_users (full_name, email, password_hash, role, is_active, student_id) "
                + "SELECT s.name, ?, ?, 'STUDENT', TRUE, s.id FROM students s WHERE s.id = ? "
                + "ON CONFLICT (LOWER(email)) DO UPDATE SET password_hash = EXCLUDED.password_hash, "
                + "role = EXCLUDED.role, is_active = TRUE, student_id = EXCLUDED.student_id, "
                + "updated_at = CURRENT_TIMESTAMP";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "student@student.com");
            statement.setString(2, DEMO_PASSWORD);
            statement.setInt(3, studentId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("The STUDENT demonstration account could not be linked to the existing Rahim Ahmed row.");
            }
        }
    }
}
