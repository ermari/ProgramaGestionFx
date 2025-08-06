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

    public List<Permiso> listarPermisos() throws SQLException {
        List<Permiso> lista = new ArrayList<>();
        String sql = "SELECT permiso_id, nombre, descripcion FROM permiso";

        try (Connection conn = BDconexion.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Permiso permiso = new Permiso();
                permiso.setPermisoId(rs.getInt("permiso_id"));
                permiso.setNombre(rs.getString("nombre"));
                permiso.setDescripcion(rs.getString("descripcion"));
                lista.add(permiso);
            }
        }
        return lista;
    }


    public int insertarConPermisos(Rol rol, List<Permiso> permisos) throws SQLException {
        String sqlInsertRol = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";
        String sqlInsertRel = "INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlInsertRol, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, rol.getNombre());
                ps.setString(2, rol.getDescripcion());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int rolId = keys.getInt(1);
                        rol.setRolId(rolId);

                        try (PreparedStatement psRel = conn.prepareStatement(sqlInsertRel)) {
                            for (Permiso permiso : permisos) {
                                psRel.setInt(1, rolId);
                                psRel.setInt(2, permiso.getPermisoId());
                                psRel.addBatch();
                            }
                            psRel.executeBatch();
                        }

                        conn.commit();
                        return rolId;
                    } else {
                        conn.rollback();
                        throw new SQLException("No se pudo obtener el ID del rol insertado.");
                    }
                }
            }
        }
    }

    public void actualizarConPermisos(Rol rol, List<Permiso> permisos) throws SQLException {
        String sqlUpdate = "UPDATE rol SET nombre = ?, descripcion = ? WHERE rol_id = ?";
        String sqlDeleteRel = "DELETE FROM rol_permiso WHERE rol_id = ?";
        String sqlInsertRel = "INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, rol.getNombre());
                ps.setString(2, rol.getDescripcion());
                ps.setInt(3, rol.getRolId());
                ps.executeUpdate();
            }

            try (PreparedStatement psDel = conn.prepareStatement(sqlDeleteRel)) {
                psDel.setInt(1, rol.getRolId());
                psDel.executeUpdate();
            }

            try (PreparedStatement psIns = conn.prepareStatement(sqlInsertRel)) {
                for (Permiso permiso : permisos) {
                    psIns.setInt(1, rol.getRolId());
                    psIns.setInt(2, permiso.getPermisoId());
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }

            conn.commit();
        }
    }

    public void eliminar(int rolId) throws SQLException {
        String sqlDeleteRelacion = "DELETE FROM rol_permiso WHERE rol_id = ?";
        String sqlDeleteRol = "DELETE FROM rol WHERE rol_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psRel = conn.prepareStatement(sqlDeleteRelacion)) {
                psRel.setInt(1, rolId);
                psRel.executeUpdate();
            }

            try (PreparedStatement psRol = conn.prepareStatement(sqlDeleteRol)) {
                psRol.setInt(1, rolId);
                psRol.executeUpdate();
            }

            conn.commit();
        }
    }

    public List<Permiso> obtenerPermisosDelRol(int rolId) throws SQLException {
        List<Permiso> permisos = new ArrayList<>();
        String sql = """
            SELECT p.permiso_id, p.nombre, p.descripcion
            FROM permiso p
            INNER JOIN rol_permiso rp ON p.permiso_id = rp.permiso_id
            WHERE rp.rol_id = ?
            """;

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rolId);
            ResultSet rs = ps.executeQuery();

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

    public Rol obtenerRolPorId(int rolId) throws SQLException {
        String sql = "SELECT rol_id, nombre, descripcion FROM rol WHERE rol_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rolId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Rol rol = new Rol();
                rol.setRolId(rs.getInt("rol_id"));
                rol.setNombre(rs.getString("nombre"));
                rol.setDescripcion(rs.getString("descripcion"));
                return rol;
            }
        }

        return null; // si no se encontró el rol con ese ID
    }
}
