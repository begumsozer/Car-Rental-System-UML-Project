// src/CarCategory.java
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CarCategory {
    private int id;
    private String name;

    public CarCategory(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void save() throws SQLException {
        String sql = String.format("INSERT INTO CarCategory (name) VALUES ('%s')", name);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        }
    }

    public static CarCategory findById(int id) throws SQLException {
        String sql = "SELECT * FROM CarCategory WHERE id = " + id;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                CarCategory category = new CarCategory(rs.getString("name"));
                category.id = id;
                return category;
            }
        }
        return null;
    }
}