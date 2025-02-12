import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.*;

public class Main {
    private static final int PORT = 8081;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/alisher";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "260380mb";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        // Запускаем HTTP-сервер
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/menu", new MenuHandler());
        server.createContext("/orders", new OrderHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("✅ Server started at http://localhost:" + PORT);

        // Запускаем консольное меню для работы с базой данных
        runDatabaseOperations();
    }


    // 📌 Консольное управление базой данных
    public static void runDatabaseOperations() {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("✅ Connected to the database successfully!");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n📌 Choose an operation:");
                System.out.println("1️⃣ Add a new menu item");
                System.out.println("2️⃣ Add a new order");
                System.out.println("3️⃣ Update an order status");
                System.out.println("4️⃣ Delete an order");
                System.out.println("5️⃣ Read all orders");
                System.out.println("6️⃣ Exit");
                System.out.print("👉 Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.print("🍽 Enter item name: ");
                        String name = scanner.nextLine();
                        System.out.print("💲 Enter item price: ");
                        double price = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("📂 Enter item category: ");
                        String category = scanner.nextLine();

                        MenuItem menuItem = new MenuItem(0, name, price, category);
                        insertMenuItem(menuItem, con);
                        System.out.println("✅ Added new menu item: " + name);
                    }
                    case 2 -> {
                        System.out.print("📦 Enter order status: ");
                        String status = scanner.nextLine();

                        Order order = new Order(0);
                        order.setStatus(status);
                        insertOrder(con, order);
                        System.out.println("✅ Added new order with status: " + status);
                    }
                    case 3 -> {
                        System.out.print("✏ Enter order ID to update: ");
                        int orderId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("🔄 Enter new status: ");
                        String status = scanner.nextLine();

                        updateOrderStatus(con, orderId, status);
                        System.out.println("✅ Order ID " + orderId + " updated to status: " + status);
                    }
                    case 4 -> {
                        System.out.print("🗑 Enter order ID to delete: ");
                        int orderId = scanner.nextInt();
                        deleteOrder(con, orderId);
                        System.out.println("✅ Order ID " + orderId + " deleted.");
                    }
                    case 5 -> {
                        readAllOrders(con);
                    }
                    case 6 -> {
                        System.out.println("👋 Exiting program.");
                        return;
                    }
                    default -> System.out.println("❌ Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 📌 Отправка HTTP-ответа
    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // 📌 Работа с таблицей MenuItem
    private static void insertMenuItem(MenuItem item, Connection con) throws SQLException {
        String query = "INSERT INTO MenuItem (name, price, category) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getCategory());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                item.setId(rs.getInt("id"));
            }
        }
    }

    private static void insertOrder(Connection con, Order order) throws SQLException {
        String query = "INSERT INTO \"Order\" (status, total_amount) VALUES (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, order.getStatus());
            stmt.setDouble(2, order.getTotalAmount());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setOrderId(rs.getInt("order_id"));
                }
            }
        }
    }

    private static void updateOrderStatus(Connection con, int orderId, String status) throws SQLException {
        String query = "UPDATE \"Order\" SET status = ? WHERE order_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

    private static void deleteOrder(Connection con, int orderId) throws SQLException {
        String query = "DELETE FROM \"Order\" WHERE order_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }

    private static void readAllOrders(Connection con) throws SQLException {
        String query = "SELECT * FROM \"Order\"";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String status = rs.getString("status");
                double totalAmount = rs.getDouble("total_amount");
                System.out.println("📦 Order ID: " + orderId + ", Status: " + status + ", Total Amount: $" + totalAmount);
            }
        }
    }
}
