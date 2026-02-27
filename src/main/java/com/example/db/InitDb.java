package com.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InitDb {
    private static final String URL = "jdbc:sqlite:todo.db";

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            System.exit(1);
        }

        String ddl = "CREATE TABLE IF NOT EXISTS tasks (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "title TEXT NOT NULL, " +
                     "completed BOOLEAN NOT NULL DEFAULT 0" +
                     ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
            System.out.println("Initialized database at: " + URL);
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            System.exit(2);
        }
    }
}
