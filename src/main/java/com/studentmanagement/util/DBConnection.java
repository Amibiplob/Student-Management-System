/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentmanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author biplob
 */

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/student_management";

    private static final String USER = "postgres";

    // Replace with the password you configured in PostgreSQL
    private static final String PASSWORD = "pass";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}