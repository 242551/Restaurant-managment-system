import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class OrderHandler implements HttpHandler {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/alisher";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "260380mb";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        int statusCode = 200;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            switch (method) {
                case "GET" -> response = getOrders(conn);
                case "POST" -> response = createOrder(conn, exchange);
                default -> {
                    response = "{\"error\": \"Method Not Allowed\"}";
                    statusCode = 405;
                }
            }
        } catch (SQLException e) {
            response = "{\"error\": \"Database error: " + e.getMessage() + "\"}";
            statusCode = 500;
        } catch (Exception e) {
            response = "{\"error\": \"Unexpected error: " + e.getMessage() + "\"}";
            statusCode = 500;
        }

        sendResponse(exchange, response, statusCode);
    }

    private String getOrders(Connection conn) throws SQLException {
        String query = "SELECT * FROM \"Order\"";
        List<Map<String, Object>> orders = new ArrayList<>();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", rs.getInt("order_id"));
                order.put("status", rs.getString("status"));
                order.put("totalAmount", rs.getDouble("total_amount"));
                orders.add(order);
            }
        }

        try {
            return objectMapper.writeValueAsString(orders);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to process JSON\"}";
        }
    }

    private String createOrder(Connection conn, HttpExchange exchange) throws IOException, SQLException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            requestBody.append(line);
        }
        br.close();
        isr.close();

        Order order = objectMapper.readValue(requestBody.toString(), Order.class);
        String query = "INSERT INTO \"Order\" (status, total_amount) VALUES (?, ?) RETURNING order_id";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, order.getStatus());
            stmt.setDouble(2, order.getTotalAmount());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                order.setOrderId(rs.getInt("order_id"));
            }
        }

        try {
            return objectMapper.writeValueAsString(order);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to process JSON\"}";
        }
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
