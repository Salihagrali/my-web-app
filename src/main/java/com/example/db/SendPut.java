package com.example.db;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendPut {
    public static void main(String[] args) throws Exception {
        String url = "http://localhost:8080/my-webapp-project/api/tasks/3";
        String json = "{\"completed\":true}";
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type","application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        int code = conn.getResponseCode();
        System.out.println("PUT response code: " + code);
    }
}
