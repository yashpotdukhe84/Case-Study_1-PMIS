package com.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/student_db";
        String user = "******";
        String password = "******";

        // String query = "SELECT id, name, age FROM students";

        try (Connection con =
                     DriverManager.getConnection(url, user, password)) {

            System.out.println("MySQL connected successfully!");

            // Statement stmt = con.createStatement();

            // ResultSet resultSet = stmt.executeQuery(query);

            // while (resultSet.next()) {
            //     int id = resultSet.getInt("id");
            //     String name = resultSet.getString("name");
            //     int age = resultSet.getInt("age");

            //     System.out.println(
            //         "ID: " + id +
            //         ", Name: " + name +
            //         ", Age: " + age
            //     );
            // }
            // resultSet.close();
            // stmt.close();

        } catch (SQLException e) {
            System.out.println("Database connection or query failed!");
            e.printStackTrace();
        }
    }
}
