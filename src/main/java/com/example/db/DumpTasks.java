package com.example.db;

import java.sql.*;

public class DumpTasks {
    private static final String URL = "jdbc:sqlite:todo.db";
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id,title,completed FROM tasks")) {
            while (rs.next()) {
                System.out.printf("%d | %s | %b%n", rs.getInt(1), rs.getString(2), rs.getBoolean(3));
            }
        }
    }
}
