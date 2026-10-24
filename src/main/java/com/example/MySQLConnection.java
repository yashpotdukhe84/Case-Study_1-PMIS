
package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/banking_db";
    private static final String USER = "***";  // username for your MySQL database
    private static final String PASSWORD = "***"; // user password for your MySQL database

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {

        String query = "SELECT customer_id, name, address, contact FROM customers";

        try (Connection con = getConnection()) {

            System.out.println("MySQL connected successfully!");
            System.out.println("Database: " +
                    con.getCatalog());

            try (PreparedStatement ps = con.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                System.out.println("\n----- CUSTOMERS -----");

                while (rs.next()) {
                    System.out.println(
                            "ID: " + rs.getInt("customer_id") +
                            ", Name: " + rs.getString("name") +
                            ", Address: " + rs.getString("address") +
                            ", Contact: " + rs.getString("contact")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection or query failed!");
            e.printStackTrace();
        }
    }
}
