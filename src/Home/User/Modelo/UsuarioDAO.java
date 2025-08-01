package Home.User.Modelo;

import BD.BDconexion;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final SucursalDAO sucursalDAO = new SucursalDAO();

    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre_usuario, email, usuario, password, sucursal_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, usuario.getPassword());
            ps.setInt(5, usuario.getSucursal().getSucursalId());

            ps.executeUpdate();
        }
    }

    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre_usuario = ?, email = ?, usuario = ?, password = ?, sucursal_id = ? " +
                "WHERE usuario_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, usuario.getPassword());
            ps.setInt(5, usuario.getSucursal().getSucursalId());
            ps.setInt(6, usuario.getUsuarioId());

            ps.executeUpdate();
        }
    }

    public void eliminar(Usuario usuario) {
        String sql = "DELETE FROM usuarios WHERE usuario_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuario.getUsuarioId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.usuario_id, u.nombre_usuario, u.email, u.usuario, u.password, u.sucursal_id " +
                "FROM usuarios u WHERE u.usuario_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.usuario_id, u.nombre_usuario, u.email, u.usuario, u.password, u.sucursal_id " +
                "FROM usuarios u ORDER BY u.usuario_id";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(rs.getInt("usuario_id"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setEmail(rs.getString("email"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setPassword(rs.getString("password"));

        int sucursalId = rs.getInt("sucursal_id");
        if (sucursalId > 0) {
            Sucursal sucursalCompleta = sucursalDAO.obtenerPorId(sucursalId);
            usuario.setSucursal(sucursalCompleta);
        } else {
            usuario.setSucursal(null);
        }

        return usuario;
    }

    public ObservableList<Usuario> listarUsuarioFiltro(String searchTerm, String filter) throws SQLException {
        ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
        String baseQuery = "SELECT u.usuario_id, u.nombre_usuario, u.email, u.usuario, u.password, u.sucursal_id FROM usuarios u";
        boolean hasSearch = searchTerm != null && !searchTerm.trim().isEmpty();

        String whereClause = "";
        if (hasSearch) {
            switch (filter) {
                case "nombreUsuario":
                    whereClause = " WHERE u.nombre_usuario LIKE ?";
                    break;
                case "email":
                    whereClause = " WHERE u.email LIKE ?";
                    break;
                case "usuario":
                    whereClause = " WHERE u.usuario LIKE ?";
                    break;
                case "usuario_id":
                    whereClause = " WHERE CAST(u.usuario_id AS CHAR) LIKE ?";
                    break;
                default:
                    whereClause = " WHERE u.nombre_usuario LIKE ?";
                    break;
            }
        }

        String sql = baseQuery + whereClause;

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            if (hasSearch) {
                pst.setString(1, "%" + searchTerm + "%");
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return usuarios;
    }

    public Usuario login(String usuario, String clave) {
        Usuario usuarioEncontrado = null;

        String sql = "SELECT u.usuario_id, u.nombre_usuario, u.email, u.usuario, u.password, u.sucursal_id " +
                "FROM usuarios u WHERE u.usuario = ? AND u.password = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, clave);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuarioEncontrado = new Usuario();
                usuarioEncontrado.setUsuarioId(rs.getInt("usuario_id"));
                usuarioEncontrado.setNombreUsuario(rs.getString("nombre_usuario"));
                usuarioEncontrado.setEmail(rs.getString("email"));
                usuarioEncontrado.setUsuario(rs.getString("usuario"));
                usuarioEncontrado.setPassword(rs.getString("password"));

                // ✅ Cargar sucursal usando SucursalDAO
                int sucursalId = rs.getInt("sucursal_id");
                if (sucursalId > 0) {
                    Sucursal sucursal = sucursalDAO.obtenerPorId(sucursalId);
                    usuarioEncontrado.setSucursal(sucursal);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarioEncontrado;
    }

}
