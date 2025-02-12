import java.sql.*;


public class RestaurantDB {

    public static void insertMenuItem(MenuItem item) throws SQLException {
        String query = "INSERT INTO MenuItem (name, price, category) VALUES (?, ?, ?)";
        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getCategory());
            stmt.executeUpdate();
        }
    }

    public static void insertOrder(Order order) throws SQLException {
        String query = "INSERT INTO \"Order\" (status, total_amount) VALUES (?, ?)";
        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, order.getStatus());
            stmt.setDouble(2, order.getTotalAmount());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                for (MenuItem item : order.getItems()) {
                    insertOrderMenuItem(orderId, item);
                }
            }
        }
    }

    // This method should be outside insertOrder, so I moved it here
    public static void insertOrderMenuItem(int orderId, MenuItem item) throws SQLException {
        String query = "INSERT INTO Order_MenuItem (order_id, menu_item_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, item.getId());  // Assumes MenuItem has an ID
            stmt.setInt(3, 1);  // Assuming quantity is always 1 for simplicity
            stmt.executeUpdate();
        }
    }

    public static void displayAllOrders() throws SQLException {
        String query = "SELECT * FROM \"Order\"";
        try (Connection conn = DBHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String status = rs.getString("status");
                double totalAmount = rs.getDouble("total_amount");
                System.out.println("Order ID: " + orderId + ", Status: " + status + ", Total Amount: $" + totalAmount);
            }
        }
    }
}
