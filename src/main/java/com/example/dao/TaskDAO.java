package com.example.dao;

import com.example.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private static final String URL = "jdbc:sqlite:todo.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite driver not found", e);
        }
    }

    // ensure the tasks table exists when the DAO is first used
    private void initializeSchema() throws SQLException {
        String ddl = "CREATE TABLE IF NOT EXISTS tasks (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "title TEXT NOT NULL, " +
                     "completed BOOLEAN NOT NULL DEFAULT 0" +
                     ");";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public TaskDAO() {
        try {
            initializeSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    public List<Task> findAll() throws SQLException {
        String sql = "SELECT id, title, completed FROM tasks";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Task> list = new ArrayList<>();
            while (rs.next()) {
                Task t = new Task();
                t.setId(rs.getInt("id"));
                t.setTitle(rs.getString("title"));
                t.setCompleted(rs.getBoolean("completed"));
                list.add(t);
            }
            return list;
        }
    }

    public Task findById(int id) throws SQLException {
        String sql = "SELECT id, title, completed FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Task t = new Task();
                    t.setId(rs.getInt("id"));
                    t.setTitle(rs.getString("title"));
                    t.setCompleted(rs.getBoolean("completed"));
                    return t;
                }
                return null;
            }
        }
    }

    public Task insert(Task task) throws SQLException {
        String sql = "INSERT INTO tasks(title, completed) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.getTitle());
            ps.setBoolean(2, task.isCompleted());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating task failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setId(keys.getInt(1));
                }
            }
            return task;
        }
    }

    public boolean update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, completed = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setBoolean(2, task.isCompleted());
            ps.setInt(3, task.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
