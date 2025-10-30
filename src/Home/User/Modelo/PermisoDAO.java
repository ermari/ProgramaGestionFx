package Home.User.Modelo;

import BD.BDconexion;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermisoDAO {

    // Listar todos los permisos disponibles
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



    // Listar permisos asignados a un usuario
    public List<Permiso> listarPermisosPorUsuario(int usuarioId) throws SQLException {
        List<Permiso> permisos = new ArrayList<>();
        String sql = "SELECT p.permiso_id, p.nombre, p.descripcion " +
                "FROM permiso p " +
                "JOIN usuario_permiso up ON p.permiso_id = up.permiso_id " +
                "WHERE up.usuario_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
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

    // Asignar múltiples permisos a un usuario
    public void asignarPermisosAUsuario(int usuarioId, List<Permiso> permisos) throws SQLException {
        String sql = "INSERT INTO usuario_permiso (usuario_id, permiso_id) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Permiso permiso : permisos) {
                ps.setInt(1, usuarioId);
                ps.setInt(2, permiso.getPermisoId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // Eliminar un permiso específico de un usuario
    public void eliminarPermisoDeUsuario(int usuarioId, int permisoId) throws SQLException {
        String sql = "DELETE FROM usuario_permiso WHERE usuario_id = ? AND permiso_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setInt(2, permisoId);
            ps.executeUpdate();
        }
    }

    // Eliminar todos los permisos de un usuario
    public void eliminarTodosPermisosDeUsuario(int usuarioId) throws SQLException {
        String sql = "DELETE FROM usuario_permiso WHERE usuario_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.executeUpdate();
        }
    }

    public void modificar(Permiso permiso) throws SQLException {
        String sql = "UPDATE permiso SET nombre = ?, descripcion = ? WHERE permiso_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, permiso.getNombre());
            ps.setString(2, permiso.getDescripcion());
            ps.setInt(3, permiso.getPermisoId());
            ps.executeUpdate();
        }
    }



    public List<Permiso> buscarPorNombre(String nombre) throws SQLException {
        List<Permiso> permisos = new ArrayList<>();
        String sql = "SELECT * FROM permiso WHERE LOWER(nombre) LIKE LOWER(?)";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Permiso p = new Permiso();
                p.setPermisoId(rs.getInt("permiso_id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                permisos.add(p);
            }
        }
        return permisos;
    }

    public List<Permiso> obtenerPermisosPorRol(int rolId) throws SQLException {
        List<Permiso> lista = new ArrayList<>();
        String sql = "SELECT p.permiso_id, p.nombre, p.descripcion " +
                "FROM permiso p JOIN rol_permiso rp ON p.permiso_id = rp.permiso_id " +
                "WHERE rp.rol_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rolId);
            ResultSet rs = ps.executeQuery();
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

    public void actualizar(Permiso permiso) throws SQLException {
        String sql = "UPDATE permiso SET nombre = ?, descripcion = ? WHERE permiso_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, permiso.getNombre());
            ps.setString(2, permiso.getDescripcion());
            ps.setInt(3, permiso.getPermisoId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int permisoId) throws SQLException {
        String sql = "DELETE FROM permiso WHERE permiso_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, permisoId);
            ps.executeUpdate();
        }
    }

    public List<Permiso> listar() throws SQLException {
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

    public Permiso buscarPorId(int permisoId) throws SQLException {
        String sql = "SELECT * FROM permiso WHERE permiso_id = ?";
        Permiso permiso = null;

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, permisoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                permiso = new Permiso();
                permiso.setPermisoId(rs.getInt("permiso_id"));
                permiso.setNombre(rs.getString("nombre"));
                permiso.setDescripcion(rs.getString("descripcion"));
            }
        }

        return permiso;
    }


}
