import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class MenuHandler implements HttpHandler {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/alisher";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "260380mb";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("\n📩 Received HTTP request: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());

        String method = exchange.getRequestMethod();
        String response;
        int statusCode = 200;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            switch (method) {
                case "GET" -> response = getMenuItems(conn);
                case "POST" -> response = createMenuItem(conn, exchange);
                default -> {
                    response = "{\"error\": \"Method Not Allowed\"}";
                    statusCode = 405;
                }
            }
        } catch (SQLException e) {
            response = "{\"error\": \"Database error: " + e.getMessage() + "\"}";
            statusCode = 500;
            e.printStackTrace();
        }

        sendResponse(exchange, response, statusCode);
    }

    private String getMenuItems(Connection conn) throws SQLException, IOException {
        String query = "SELECT * FROM MenuItem";
        List<Map<String, Object>> items = new ArrayList<>();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("name", rs.getString("name"));
                item.put("price", rs.getDouble("price"));
                item.put("category", rs.getString("category"));
                items.add(item);
            }
        }

        return objectMapper.writeValueAsString(items);
    }

    private String createMenuItem(Connection conn, HttpExchange exchange) throws IOException, SQLException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            requestBody.append(line);
        }
        br.close();
        isr.close();

        MenuItem item = objectMapper.readValue(requestBody.toString(), MenuItem.class);
        String query = "INSERT INTO MenuItem (name, price, category) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getCategory());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                item.setId(rs.getInt("id"));
            }
        }

        return objectMapper.writeValueAsString(item);
    }

    /**
     * Метод для отправки HTTP-ответа
     */
    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
