package RegistrarCatalogos.Plantilla;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DBUtil {

    // Credenciales de la base de datos (¡CAMBIA ESTOS VALORES!)
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/datasoft?useSSL=false&serverTimezone=UTC"; // Cambia el nombre de la base de datos
    private static final String USER = "root";
    private static final String PASSWORD = "123465";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    // --- Operaciones CRUD para YourCatalogItem ---

    public static ObservableList<YourItem> getAllYourCatalogItems() throws SQLException {
        ObservableList<YourItem> items = FXCollections.observableArrayList();
        // Cambia 'your_table_name' por el nombre real de tu tabla
        String sql = "SELECT id, nombre FROM your_table_name ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new YourItem(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return items;
    }

    public static void addYourCatalogItem(YourItem item) throws SQLException {
        // Cambia 'your_table_name' por el nombre real de tu tabla
        String sql = "INSERT INTO your_table_name (id, nombre) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getId());
            pstmt.setString(2, item.getName());
            pstmt.executeUpdate();
        }
    }

    public static void updateYourCatalogItem(YourItem item) throws SQLException {
        // Cambia 'your_table_name' por el nombre real de tu tabla
        String sql = "UPDATE your_table_name SET nombre = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteYourCatalogItem(int id) throws SQLException {
        // Cambia 'your_table_name' por el nombre real de tu tabla
        String sql = "DELETE FROM your_table_name WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public static boolean yourCatalogItemIdExists(int id) throws SQLException {
        // Cambia 'your_table_name' por el nombre real de tu tabla
        String sql = "SELECT COUNT(*) FROM your_table_name WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}