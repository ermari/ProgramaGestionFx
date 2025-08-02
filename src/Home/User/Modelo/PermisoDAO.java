package Home.User.Modelo;

import BD.BDconexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermisoDAO {

    public List<Permiso> listarPermisos() throws SQLException {
        List<Permiso> permisos = new ArrayList<>();
        String sql = "SELECT * FROM permiso";

        try (Connection conn = BDconexion.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Permiso permiso = new Permiso();
                permiso.setPermisoId(rs.getInt("permiso_id"));
                permiso.setNombre(rs.getString("nombre"));
                permiso.setDescripcion(rs.getString("descripcion"));
                permisos.add(permiso);
            }
        }
        return permisos;
    }

    public void insertar(Permiso permiso) throws SQLException {
        String sql = "INSERT INTO permiso (nombre, descripcion) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, permiso.getNombre());
            ps.setString(2, permiso.getDescripcion());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    permiso.setPermisoId(rs.getInt(1));
                }
            }
        }
    }
}
