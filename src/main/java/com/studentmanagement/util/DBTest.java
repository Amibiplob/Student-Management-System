/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentmanagement.util;

import java.sql.Connection;
/**
 *
 * @author biplob
 */


public class DBTest {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            System.out.println("Database connected successfully!");

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
