// src/DatabaseConnection.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mariadb://localhost:3306/car_rental?useGssApi=false";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTables() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createCustomerTable = "CREATE TABLE IF NOT EXISTS Customer (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "contactDetails VARCHAR(255) NOT NULL" +
                    ")";
            stmt.executeUpdate(createCustomerTable);

            String createCarCategoryTable = "CREATE TABLE IF NOT EXISTS CarCategory (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL" +
                    ")";
            stmt.executeUpdate(createCarCategoryTable);

            String createCarTable = "CREATE TABLE IF NOT EXISTS Car (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "model VARCHAR(255) NOT NULL," +
                    "status VARCHAR(255) NOT NULL," +
                    "categoryId INT," +
                    "FOREIGN KEY (categoryId) REFERENCES CarCategory(id)" +
                    ")";
            stmt.executeUpdate(createCarTable);

            String createRentalTable = "CREATE TABLE IF NOT EXISTS Rental (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "startDate DATE NOT NULL," +
                    "endDate DATE NOT NULL," +
                    "rentalPrice DOUBLE NOT NULL," +
                    "dailyPrice DOUBLE NOT NULL," +
                    "customerId INT," +
                    "carId INT," +
                    "FOREIGN KEY (customerId) REFERENCES Customer(id)," +
                    "FOREIGN KEY (carId) REFERENCES Car(id)" +
                    ")";
            stmt.executeUpdate(createRentalTable);
        }
    }
}