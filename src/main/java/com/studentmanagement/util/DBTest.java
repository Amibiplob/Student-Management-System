package com.studentmanagement.util;

import java.sql.Connection;
import java.sql.SQLException;

public class DBTest {

    public static void main(String[] args) {
        boolean jdbcConnectionOpened = false;
        try {
            System.out.println(DBConnection.configurationSummary());
            System.out.println(DBConnection.runtimeSettingsSummary());
            System.out.println(DBConnection.networkProbeSummary());
            try (Connection connection = DBConnection.getConnection()) {
                jdbcConnectionOpened = true;
                connection.setReadOnly(true);
                if (!connection.isValid(2)) {
                    throw new IllegalStateException("PostgreSQL returned an invalid connection.");
                }
                System.out.println("Database connected successfully.");
            }
        } catch (SQLException exception) {
            System.err.println(DBConnection.sanitizedDiagnostic(exception));
            System.exit(1);
        } catch (Exception exception) {
            if (jdbcConnectionOpened) {
                System.err.println("JDBC connection opened, but the read-only validity check failed.");
            } else {
                System.err.println("Database configuration or connection check failed.");
            }
            System.exit(1);
        }
    }
}
