package Home.User.Modelo;

import BD.BDconexion;
import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioDAO {

    private final SucursalDAO sucursalDAO = new SucursalDAO();

    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre_usuario, email, usuario, password) VALUES (?, ?, ?, ?)";
        String sqlInsertUsuarioSucursal = "INSERT INTO usuario_sucursal (usuario_id, sucursal_id) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, usuario.getNombreUsuario());
                ps.setString(2, usuario.getEmail());
                ps.setString(3, usuario.getUsuario());
                ps.setString(4, usuario.getPassword());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int nuevoId = keys.getInt(1);
                    usuario.setUsuarioId(nuevoId);

                    try (PreparedStatement psRel = conn.prepareStatement(sqlInsertUsuarioSucursal)) {
                        for (Sucursal suc : usuario.getSucursales()) {
                            psRel.setInt(1, nuevoId);
                            psRel.setInt(2, suc.getSucursalId());
                            psRel.addBatch();
                        }
                        psRel.executeBatch();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void actualizar(Usuario usuario) throws SQLException {
        String sqlUpdateUsuario = "UPDATE usuarios SET nombre_usuario = ?, email = ?, usuario = ?, password = ? WHERE usuario_id = ?";
        String sqlInsertRelacion = "INSERT INTO usuario_sucursal (usuario_id, sucursal_id) VALUES (?, ?)";
        String sqlDeleteRelacion = "DELETE FROM usuario_sucursal WHERE usuario_id = ? AND sucursal_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateUsuario)) {
                ps.setString(1, usuario.getNombreUsuario());
                ps.setString(2, usuario.getEmail());
                ps.setString(3, usuario.getUsuario());
                ps.setString(4, usuario.getPassword());
                ps.setInt(5, usuario.getUsuarioId());
                ps.executeUpdate();
            }

            List<Sucursal> sucursalesAnteriores = obtenerSucursalesDeUsuario(usuario.getUsuarioId());
            List<Sucursal> nuevas = usuario.getSucursales();

            List<Integer> nuevasIds = nuevas.stream().map(Sucursal::getSucursalId).collect(Collectors.toList());
            List<Integer> anterioresIds = sucursalesAnteriores.stream().map(Sucursal::getSucursalId).collect(Collectors.toList());

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsertRelacion)) {
                for (Sucursal sucursal : nuevas) {
                    if (!anterioresIds.contains(sucursal.getSucursalId())) {
                        psInsert.setInt(1, usuario.getUsuarioId());
                        psInsert.setInt(2, sucursal.getSucursalId());
                        psInsert.addBatch();
                    }
                }
                psInsert.executeBatch();
            }

            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeleteRelacion)) {
                for (Sucursal anterior : sucursalesAnteriores) {
                    if (!nuevasIds.contains(anterior.getSucursalId())) {
                        psDelete.setInt(1, usuario.getUsuarioId());
                        psDelete.setInt(2, anterior.getSucursalId());
                        psDelete.addBatch();
                    }
                }
                psDelete.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void eliminar(Usuario usuario) {
        String sqlDeleteRel = "DELETE FROM usuario_sucursal WHERE usuario_id = ?";
        String sqlDeleteUsuario = "DELETE FROM usuarios WHERE usuario_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psRel = conn.prepareStatement(sqlDeleteRel)) {
                psRel.setInt(1, usuario.getUsuarioId());
                psRel.executeUpdate();
            }

            try (PreparedStatement psUsu = conn.prepareStatement(sqlDeleteUsuario)) {
                psUsu.setInt(1, usuario.getUsuarioId());
                psUsu.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT usuario_id, nombre_usuario, email, usuario, password FROM usuarios WHERE usuario_id = ?";
        Usuario usuario = null;
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setUsuarioId(rs.getInt("usuario_id"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setUsuario(rs.getString("usuario"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setSucursales(obtenerSucursalesDeUsuario(id));
                }
            }
        }
        return usuario;
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT usuario_id, nombre_usuario, email, usuario, password FROM usuarios ORDER BY usuario_id";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                int id = rs.getInt("usuario_id");
                usuario.setUsuarioId(id);
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setEmail(rs.getString("email"));
                usuario.setUsuario(rs.getString("usuario"));
                usuario.setPassword(rs.getString("password"));
                usuario.setSucursales(obtenerSucursalesDeUsuario(id));
                lista.add(usuario);
            }
        }
        return lista;
    }

    public ObservableList<Usuario> listarUsuarioFiltro(String searchTerm, String filter) throws SQLException {
        ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
        String baseQuery = "SELECT usuario_id, nombre_usuario, email, usuario, password FROM usuarios";
        boolean hasSearch = searchTerm != null && !searchTerm.trim().isEmpty();

        String whereClause = "";
        if (hasSearch) {
            switch (filter) {
                case "nombreUsuario":
                    whereClause = " WHERE nombre_usuario LIKE ?";
                    break;
                case "email":
                    whereClause = " WHERE email LIKE ?";
                    break;
                case "usuario":
                    whereClause = " WHERE usuario LIKE ?";
                    break;
                case "usuario_id":
                    whereClause = " WHERE CAST(usuario_id AS CHAR) LIKE ?";
                    break;
                default:
                    whereClause = " WHERE nombre_usuario LIKE ?";
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
                    Usuario usuario = new Usuario();
                    int id = rs.getInt("usuario_id");
                    usuario.setUsuarioId(id);
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setUsuario(rs.getString("usuario"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setSucursales(obtenerSucursalesDeUsuario(id));
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }

    public Usuario login(String usuarioStr, String clave) {
        Usuario usuarioEncontrado = null;

        String sql = "SELECT usuario_id, nombre_usuario, email, usuario, password FROM usuarios WHERE usuario = ? AND password = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuarioStr);
            stmt.setString(2, clave);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuarioEncontrado = new Usuario();
                    int id = rs.getInt("usuario_id");
                    usuarioEncontrado.setUsuarioId(id);
                    usuarioEncontrado.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuarioEncontrado.setEmail(rs.getString("email"));
                    usuarioEncontrado.setUsuario(rs.getString("usuario"));
                    usuarioEncontrado.setPassword(rs.getString("password"));
                    usuarioEncontrado.setSucursales(obtenerSucursalesDeUsuario(id));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarioEncontrado;
    }

    public List<Sucursal> obtenerSucursalesDeUsuario(int usuarioId) {
        List<Sucursal> lista = new ArrayList<>();

        String sql = "SELECT s.sucursal_id, s.nombre, e.empresa_id, e.nombre AS empresa_nombre " +
                "FROM sucursal s " +
                "JOIN usuario_sucursal us ON s.sucursal_id = us.sucursal_id " +
                "JOIN empresa e ON s.empresa_id = e.empresa_id " +
                "WHERE us.usuario_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Empresa empresa = new Empresa();
                empresa.setEmpresaId(rs.getInt("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));

                Sucursal sucursal = new Sucursal();
                sucursal.setSucursalId(rs.getInt("sucursal_id"));
                sucursal.setNombre(rs.getString("nombre"));
                sucursal.setEmpresa(empresa);

                lista.add(sucursal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void actualizarInteligente(Usuario usuario, Set<Integer> sucursalesIniciales) throws SQLException {
        String sqlUpdateUsuario = "UPDATE usuarios SET nombre_usuario = ?, email = ?, usuario = ?, password = ? WHERE usuario_id = ?";
        String sqlInsertSucursal = "INSERT INTO usuario_sucursal (usuario_id, sucursal_id) VALUES (?, ?)";
        String sqlDeleteSucursal = "DELETE FROM usuario_sucursal WHERE usuario_id = ? AND sucursal_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateUsuario)) {
                ps.setString(1, usuario.getNombreUsuario());
                ps.setString(2, usuario.getEmail());
                ps.setString(3, usuario.getUsuario());
                ps.setString(4, usuario.getPassword());
                ps.setInt(5, usuario.getUsuarioId());
                ps.executeUpdate();
            }

            // Preparar sets para comparación
            Set<Integer> nuevasSucursales = new HashSet<>();
            for (Sucursal suc : usuario.getSucursales()) {
                nuevasSucursales.add(suc.getSucursalId());
            }

            // Sucursales nuevas (insertar)
            Set<Integer> aInsertar = new HashSet<>(nuevasSucursales);
            aInsertar.removeAll(sucursalesIniciales);

            // Sucursales eliminadas (delete)
            Set<Integer> aEliminar = new HashSet<>(sucursalesIniciales);
            aEliminar.removeAll(nuevasSucursales);

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsertSucursal);
                 PreparedStatement psDelete = conn.prepareStatement(sqlDeleteSucursal)) {

                for (Integer sucursalId : aInsertar) {
                    psInsert.setInt(1, usuario.getUsuarioId());
                    psInsert.setInt(2, sucursalId);
                    psInsert.addBatch();
                }
                psInsert.executeBatch();

                for (Integer sucursalId : aEliminar) {
                    psDelete.setInt(1, usuario.getUsuarioId());
                    psDelete.setInt(2, sucursalId);
                    psDelete.addBatch();
                }
                psDelete.executeBatch();
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void asignarRolesAUsuario(int usuarioId, List<Rol> roles) throws SQLException {
        String sqlInsert = "INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            for (Rol rol : roles) {
                ps.setInt(1, usuarioId);
                ps.setInt(2, rol.getRolId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Rol> obtenerRolesDeUsuario(int usuarioId) throws SQLException {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT r.rol_id, r.nombre, r.descripcion " +
                "FROM rol r JOIN usuario_rol ur ON r.rol_id = ur.rol_id " +
                "WHERE ur.usuario_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

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


}
