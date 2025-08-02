package Home.User.Modelo;

import BD.BDconexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public List<Rol> listarRoles() throws SQLException {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT * FROM rol";

        try (Connection conn = BDconexion.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setRolId(rs.getInt("rol_id"));
                rol.setNombre(rs.getString("nombre"));
                rol.setDescripcion(rs.getString("descripcion"));
                roles.add(rol);
            }
        }
        return roles;
    }

    public void insertar(Rol rol) throws SQLException {
        String sql = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, rol.getNombre());
            ps.setString(2, rol.getDescripcion());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    rol.setRolId(rs.getInt(1));
                }
            }
        }
    }
}
