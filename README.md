# 🍽️ Restaurant Management System  

A **Java-based** restaurant management system that enables **menu management, order processing, and customer interactions**.  
This project follows **OOP principles**, integrates **PostgreSQL**, and provides a **REST API** for external communication.  

---

## 🔹 Features  
✔ **Menu Management:** Adding, viewing, sorting, and filtering menu items.  
✔ **Order Processing:** Creating and managing orders with statuses (`PENDING`, `IN_PROGRESS`, `COMPLETED`).  
✔ **Database Integration:** PostgreSQL for persistent storage and SQL query execution.  
✔ **REST API:** Allows external applications to interact with the system via HTTP requests.  

---

## 🔹 Technologies Used  
- **Java** (Object-Oriented Programming, multi-threading)  
- **PostgreSQL** (Database management)  
- **JDBC** (Java Database Connectivity)  
- **HTTPServer** (Lightweight REST API implementation)  
- **JSON (Jackson Library)** (Data serialization and deserialization)  

---

## 🚀 How to Run  

1️⃣ **Set up PostgreSQL** and create the required tables.  
2️⃣ **Run `Main.java`** to start the application.  
3️⃣ **Interact using** the console menu or send HTTP requests.  

---

## 📂 Project Structure  

📦 restaurant-management 
┣ 📂 src ┃
┣ 📜 Main.java // Main entry point 
┃┣ 📂 models // Data models (entity classes) 
┃ ┃ ┣ 📜 MenuItem.java // Menu item class
┃ ┃ ┣ 📜 Order.java // Order class
┃ ┃ ┣ 📜 OrderStatus.java // Enum for order statuses
┃ ┃ ┣ 📜 Customer.java // Customer class 
┃ ┃ ┣ 📜 Employee.java // Employee class (inherits from Person) 
┃ ┃ ┣ 📜 Person.java // Abstract class for people 
┃ ┃ ┣ 📜 Restaurant.java // Restaurant class
┃ ┣ 📂 database // Database operations
┃ ┃ ┣ 📜 DBHelper.java // Database connection
┃ ┃ ┣ 📜 RestaurantDB.java // SQL queries (CRUD operations)
┃ ┣ 📂 services // Business logic 
┃ ┃ ┣ 📜 InventoryManager.java // Menu management (search, filtering, sorting) 
┃ ┣ 📂 api // REST API 
┃ ┃ ┣ 📜 MenuHandler.java // Request handler for /menu
┃ ┃ ┣ 📜 OrderHandler.java // Request handler for /orders 
┃ ┣ 📜 README.md // Project description
---

## 📌 Class Descriptions  

### **Main**  
✔ The main entry point of the program.  
✔ Starts the **HTTP server** and **console menu** for database operations.  

### **MenuItem**  
✔ Represents a **restaurant menu item** with attributes such as name, price, and category.  

**Methods:**  
- Getters/Setters (`getId()`, `setName()`, `getPrice()`, `setCategory()`).  
- `toString()` – returns a string representation of the object.  

### **Order**  
✔ Represents a **customer order**, including its status, total amount, and list of items.  

**Methods:**  
- Getters/Setters (`getOrderId()`, `setStatus()`, `getItems()`, `setTotalAmount()`).  
- `toString()` – returns a string representation of the order.  

### **OrderStatus (Enum)**  
✔ Defines the possible **states of an order**.  

**Statuses:**  
- `PENDING` – awaiting processing  
- `IN_PROGRESS` – in preparation  
- `COMPLETED` – completed  

### **Person (Abstract Class)**  
✔ Base class for all system users (employees and customers).  

**Methods:**  
- `getName()`, `getContact()` – access user data.  
- `abstract void displayDetails()` – abstract method for displaying user details.  

### **Employee (Inherits from Person)**  
✔ Represents a **restaurant employee** with an additional `position` attribute.  

**Methods:**  
- `getPosition()` – retrieves the employee's position.  
- `displayDetails()` – overrides `displayDetails()` from `Person`.  

### **Customer**  
✔ Represents a **restaurant customer** with an ID, name, and contact information.  

**Methods:**  
- Getters/Setters (`getCustomerId()`, `getName()`, `getContact()`).  
- `displayCustomer()` – displays customer information.  

### **Restaurant**  
✔ Stores the **restaurant's name and location**.  

**Methods:**  
- `displayRestaurant()` – displays restaurant information.  

### **InventoryManager**  
✔ Manages **menu items**, including **searching, filtering, and sorting**.  

**Methods:**  
- `addItem(MenuItem item)` – adds an item to the menu.  
- `filterItems(String category)` – filters items by category.  
- `searchItems(String keyword)` – searches items by name.  
- `sortItemsByPrice(boolean ascending)` – sorts items by price.  

### **DBHelper**  
✔ Handles **database connections** to PostgreSQL.  

**Methods:**  
- `connect()` – establishes a database connection.  

### **RestaurantDB**  
✔ Executes **SQL queries** for managing menu items and orders.  

**Methods:**  
- `insertMenuItem(MenuItem item)` – adds a new menu item.  
- `insertOrder(Order order)` – creates a new order.  
- `displayAllOrders()` – retrieves all orders.  
- `deleteOrder(int orderId)` – deletes an order.  

### **OrderHandler**  
✔ **Handles HTTP requests** related to orders (`/orders`).  

**Methods:**  
- `handle(HttpExchange exchange)` – processes HTTP requests.  
- `getOrders(Connection conn)` – retrieves orders in **JSON format**.  
- `createOrder(Connection conn, HttpExchange exchange)` – creates a new order.  
- `sendResponse(HttpExchange exchange, String response, int statusCode)` – sends responses to clients.  

### **MenuHandler**  
✔ **Handles HTTP requests** related to menu items (`/menu`).  

**Methods:**  
- `handle(HttpExchange exchange)` – processes HTTP requests.  
- `getMenuItems(Connection conn)` – retrieves menu items in **JSON format**.  
- `createMenuItem(Connection conn, HttpExchange exchange)` – adds a new menu item to the database.  

---

## 🌐 API Endpoints  

📌 **Examples of API usage:**  

### **Get All Menu Items**  
```http
GET http://localhost:8081/menu
Accept: application/json
