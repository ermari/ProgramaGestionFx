package CatalogoGestion.Empresas.Modelo;

import BD.BDconexion;
import Home.User.Modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SucursalDAO {


    public Optional<Sucursal> getById(int id) throws SQLException {
        String sql = "SELECT sucursal_id, empresa_id, nombre, codigo, direccion, telefono, email," +
                " ciudad, pais, estado, fecha_registro FROM datasoft.sucursal  WHERE sucursal_id = ?";
        Sucursal sucursal = null;

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establece el valor del ID en el placeholder (?)
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Si se encuentra un resultado, crea el objeto Usuario
                    sucursal = new Sucursal(
                            rs.getInt("sucursal_id"),
                            rs.getString("codigo"),
                            rs.getString("nombre")
                    );
                }
            }
        }
        return Optional.ofNullable(sucursal);
    }


    // Se mantiene la conexión en el DAO, pero cada método debe manejarla
    // de forma segura con try-with-resources para cerrar los recursos.

    /**
     * Obtiene una lista de sucursales asociadas a una empresa específica.
     * @param empresaId El ID de la empresa.
     * @return Una lista de objetos Sucursal.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public List<Sucursal> obtenerPorEmpresa(int empresaId) throws SQLException {
        List<Sucursal> lista = new ArrayList<>();
        String sql = "SELECT * FROM sucursal WHERE empresa_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empresaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(crearSucursalDesdeResultSet(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Obtiene una sucursal por su ID.
     * @param sucursalId El ID de la sucursal.
     * @return El objeto Sucursal, o null si no se encuentra.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public Sucursal obtenerPorId(int sucursalId) throws SQLException {
        String sql = "SELECT * FROM sucursal WHERE sucursal_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sucursalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearSucursalDesdeResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Inserta un nuevo registro de sucursal en la base de datos.
     * @param sucursal El objeto Sucursal a guardar.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public void guardarSucursal(Sucursal sucursal) throws SQLException {
        String sql = "INSERT INTO sucursal (nombre, codigo, direccion, telefono, email, ciudad, pais, estado, empresa_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, sucursal.getNombre());
            stmt.setString(2, sucursal.getCodigo());
            stmt.setString(3, sucursal.getDireccion());
            stmt.setString(4, sucursal.getTelefono());
            stmt.setString(5, sucursal.getEmail());
            stmt.setString(6, sucursal.getCiudad());
            stmt.setString(7, sucursal.getPais());
            stmt.setBoolean(8, sucursal.isEstado());
            stmt.setInt(9, sucursal.getEmpresa().getEmpresaId());

            stmt.executeUpdate();
        }
    }

    /**
     * Actualiza un registro de sucursal existente en la base de datos.
     * @param sucursal El objeto Sucursal con los datos actualizados.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public void actualizarSucursal(Sucursal sucursal) throws SQLException {
        String sql = "UPDATE sucursal SET nombre = ?, codigo = ?, direccion = ?, telefono = ?, email = ?, ciudad = ?, pais = ?, estado = ? WHERE sucursal_id = ?";
        try (Connection con = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, sucursal.getNombre());
            stmt.setString(2, sucursal.getCodigo());
            stmt.setString(3, sucursal.getDireccion());
            stmt.setString(4, sucursal.getTelefono());
            stmt.setString(5, sucursal.getEmail());
            stmt.setString(6, sucursal.getCiudad());
            stmt.setString(7, sucursal.getPais());
            stmt.setBoolean(8, sucursal.isEstado());
            stmt.setInt(9, sucursal.getSucursalId());

            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para crear un objeto Sucursal a partir de un ResultSet.
     * Esto evita la repetición de código.
     * @param rs El ResultSet del cual leer los datos.
     * @return Un objeto Sucursal completamente poblado.
     * @throws SQLException Si ocurre un error al leer del ResultSet.
     */
    private Sucursal crearSucursalDesdeResultSet(ResultSet rs) throws SQLException {
        Sucursal suc = new Sucursal();
        suc.setSucursalId(rs.getInt("sucursal_id"));
        suc.setNombre(rs.getString("nombre"));
        suc.setCodigo(rs.getString("codigo"));
        suc.setDireccion(rs.getString("direccion"));
        suc.setTelefono(rs.getString("telefono"));
        suc.setEmail(rs.getString("email"));
        suc.setCiudad(rs.getString("ciudad"));
        suc.setPais(rs.getString("pais"));
        suc.setEstado(rs.getBoolean("estado"));

        // Asignación de la empresa, creando un objeto Empresa con el ID.
        Empresa empresa = new Empresa();
        empresa.setEmpresaId(rs.getInt("empresa_id"));
        suc.setEmpresa(empresa);

        return suc;
    }
}