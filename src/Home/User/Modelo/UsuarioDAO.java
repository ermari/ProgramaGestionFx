package Home.User.Modelo;

import BD.BDconexion;
import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsuarioDAO {

    public List<Usuario> listarUsuarios() throws SQLException {
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

    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre_usuario, email, usuario, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, usuario.getPassword());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setUsuarioId(rs.getInt(1));
                }
            }

            // Insertar relaciones
            insertarSucursalesDelUsuario(usuario.getUsuarioId(), usuario.getSucursales());
            insertarRolesDelUsuario(usuario.getUsuarioId(), usuario.getRoles());
        }
    }

    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre_usuario = ?, email = ?, usuario = ?, password = ? WHERE usuario_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, usuario.getPassword());
            ps.setInt(5, usuario.getUsuarioId());
            ps.executeUpdate();

            // Eliminar relaciones anteriores
            eliminarRelaciones(usuario.getUsuarioId());

            // Insertar relaciones nuevas
            insertarSucursalesDelUsuario(usuario.getUsuarioId(), usuario.getSucursales());
            insertarRolesDelUsuario(usuario.getUsuarioId(), usuario.getRoles());
        }
    }

    public void eliminar(int usuarioId) throws SQLException {
        String sqlRelSucursal = "DELETE FROM usuario_sucursal WHERE usuario_id = ?";
        String sqlRelRol = "DELETE FROM usuario_rol WHERE usuario_id = ?";
        String sqlUsuario = "DELETE FROM usuarios WHERE usuario_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlRelSucursal);
                 PreparedStatement ps2 = conn.prepareStatement(sqlRelRol);
                 PreparedStatement ps3 = conn.prepareStatement(sqlUsuario)) {

                ps1.setInt(1, usuarioId);
                ps1.executeUpdate();

                ps2.setInt(1, usuarioId);
                ps2.executeUpdate();

                ps3.setInt(1, usuarioId);
                ps3.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // -------- Relaciones con sucursales y roles --------

    public List<Sucursal> obtenerSucursalesDelUsuario(int usuarioId) throws SQLException {
        List<Sucursal> lista = new ArrayList<>();
        String sql = """
            SELECT 
                s.sucursal_id, s.nombre AS sucursal_nombre, s.direccion, s.empresa_id,
                e.nombre AS empresa_nombre, e.razon_social, e.ruc, e.direccion AS empresa_direccion
            FROM sucursal s
            JOIN usuario_sucursal us ON s.sucursal_id = us.sucursal_id
            JOIN empresa e ON s.empresa_id = e.empresa_id
            WHERE us.usuario_id = ?""";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Empresa empresa = new Empresa();
                empresa.setEmpresaId(rs.getInt("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setRazonSocial(rs.getString("razon_social"));
                empresa.setRuc(rs.getString("ruc"));
                empresa.setDireccion(rs.getString("empresa_direccion"));

                Sucursal suc = new Sucursal();
                suc.setSucursalId(rs.getInt("sucursal_id"));
                suc.setNombre(rs.getString("sucursal_nombre"));
                suc.setDireccion(rs.getString("direccion"));
                suc.setEmpresa(empresa); // ⬅️ Aquí sí seteas la empresa completa

                lista.add(suc);
            }
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






    public List<Rol> obtenerRolesDelUsuario(int usuarioId) throws SQLException {
        List<Rol> lista = new ArrayList<>();
        String sql = """
                SELECT r.rol_id, r.nombre, r.descripcion
                FROM rol r
                JOIN usuario_rol ur ON r.rol_id = ur.rol_id
                WHERE ur.usuario_id = ?""";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setRolId(rs.getInt("rol_id"));
                rol.setNombre(rs.getString("nombre"));
                rol.setDescripcion(rs.getString("descripcion"));
                lista.add(rol);
            }
        }

        return lista;
    }

    private void insertarSucursalesDelUsuario(int usuarioId, List<Sucursal> sucursales) throws SQLException {
        String sql = "INSERT INTO usuario_sucursal (usuario_id, sucursal_id) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Sucursal s : sucursales) {
                ps.setInt(1, usuarioId);
                ps.setInt(2, s.getSucursalId());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private void insertarRolesDelUsuario(int usuarioId, List<Rol> roles) throws SQLException {
        String sql = "INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (?, ?)";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Rol r : roles) {
                ps.setInt(1, usuarioId);
                ps.setInt(2, r.getRolId());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private void eliminarRelaciones(int usuarioId) throws SQLException {
        try (Connection conn = BDconexion.getInstance().getConnection()) {
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM usuario_sucursal WHERE usuario_id = ?");
                 PreparedStatement ps2 = conn.prepareStatement("DELETE FROM usuario_rol WHERE usuario_id = ?")) {

                ps1.setInt(1, usuarioId);
                ps1.executeUpdate();

                ps2.setInt(1, usuarioId);
                ps2.executeUpdate();
            }
        }
    }

    public Usuario login(String usuario, String password) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario user = new Usuario();
                user.setUsuarioId(rs.getInt("usuario_id"));
                user.setNombreUsuario(rs.getString("nombre_usuario"));
                user.setEmail(rs.getString("email"));
                user.setUsuario(rs.getString("usuario"));
                user.setPassword(rs.getString("password")); // O puedes omitir esto por seguridad

                // Cargar roles asociados
                user.setRoles(obtenerRolesDelUsuario(user.getUsuarioId()));

                // Cargar sucursales asociadas
                user.setSucursales(obtenerSucursalesDelUsuario(user.getUsuarioId()));

                return user;
            } else {
                return null; // No encontrado
            }
        }
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



}
