package com.example.servlet;

import com.example.dao.TaskDAO;
import com.example.model.Task;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/tasks/*")
public class TasksServlet extends HttpServlet {

    private TaskDAO dao = new TaskDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        try (PrintWriter out = resp.getWriter()) {
            if (pathInfo == null || "/".equals(pathInfo)) {
                List<Task> tasks = dao.findAll();
                out.print(gson.toJson(tasks));
            } else {
                String[] parts = pathInfo.split("/");
                if (parts.length >= 2) {
                    int id = Integer.parseInt(parts[1]);
                    Task t = dao.findById(id);
                    if (t != null) {
                        out.print(gson.toJson(t));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        Task task = gson.fromJson(sb.toString(), Task.class);
        try {
            Task created = dao.insert(task);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            try (PrintWriter out = resp.getWriter()) {
                out.print(gson.toJson(created));
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length >= 2) {
            int id = Integer.parseInt(parts[1]);
            // read JSON body into a generic object to detect which fields are present
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String body = sb.toString();
            try {
                Task existing = dao.findById(id);
                if (existing == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                // merge changes
                com.google.gson.JsonObject obj = gson.fromJson(body, com.google.gson.JsonObject.class);
                if (obj.has("title") && !obj.get("title").isJsonNull()) {
                    existing.setTitle(obj.get("title").getAsString());
                }
                if (obj.has("completed") && !obj.get("completed").isJsonNull()) {
                    existing.setCompleted(obj.get("completed").getAsBoolean());
                }

                boolean updated = dao.update(existing);
                if (updated) {
                    try (PrintWriter out = resp.getWriter()) {
                        out.print(gson.toJson(existing));
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length >= 2) {
            int id = Integer.parseInt(parts[1]);
            try {
                boolean deleted = dao.delete(id);
                if (deleted) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
    }
}
