package CatalogoGestion.Empresas.Modelo;

import BD.BDconexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SucursalDAO {
    private Connection conn = BDconexion.getInstance().getConnection();

    public List<Sucursal> obtenerPorEmpresa(int empresaId) throws SQLException {
        List<Sucursal> lista = new ArrayList<>();
        String sql = "SELECT * FROM sucursal WHERE empresa_id=?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empresaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sucursal s = new Sucursal();
                    s.setSucursalId(rs.getInt("sucursal_id"));
                    s.setNombre(rs.getString("nombre"));
                    s.setCodigo(rs.getString("codigo"));
                    s.setDireccion(rs.getString("direccion"));
                    s.setTelefono(rs.getString("telefono"));
                    s.setEmail(rs.getString("email"));
                    s.setCiudad(rs.getString("ciudad"));
                    s.setPais(rs.getString("pais"));
                    s.setEstado(rs.getBoolean("estado"));

                    // 🚨 Aquí es el punto clave:
                    Empresa empresa = new Empresa();
                    empresa.setEmpresaId(rs.getInt("empresa_id"));
                    s.setEmpresa(empresa); // Asigna objeto Empresa (aunque solo con ID)

                    lista.add(s);
                }
            }
        }
        return lista;
    }


    public Sucursal obtenerPorId(int sucursalId) throws SQLException {
        String sql = "SELECT * FROM sucursal WHERE sucursal_id=?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sucursalId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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

                // Obtener empresa asociada
                EmpresaDAO empresaDAO = new EmpresaDAO();
                Empresa empresa = empresaDAO.obtenerPorId(rs.getInt("empresa_id"));
                suc.setEmpresa(empresa);

                return suc;
            }
        }
        return null;
    }








    public void guardarSucursal(Sucursal sucursal) throws SQLException {
        String sql = "INSERT INTO sucursal (codigo, nombre, direccion, telefono, estado, empresa_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, sucursal.getCodigo());
            stmt.setString(2, sucursal.getNombre());
            stmt.setString(3, sucursal.getDireccion());
            stmt.setString(4, sucursal.getTelefono());
            stmt.setBoolean(5, sucursal.isEstado());
            //stmt.setInt(6, sucursal.getEmpresaId());
            stmt.setInt(6, sucursal.getEmpresa().getEmpresaId());


            stmt.executeUpdate();
        }
    }


    public void actualizarSucursal(Sucursal sucursal) throws SQLException {
        String sql = "UPDATE sucursal SET  nombre = ?, direccion = ?, telefono = ?, estado = ? WHERE sucursal_id = ?";
        try (Connection con = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
          //  stmt.setString(1, sucursal.getCodigo());

            stmt.setString(1, sucursal.getNombre());
            stmt.setString(2, sucursal.getDireccion());
            stmt.setString(3, sucursal.getTelefono());
            stmt.setBoolean(4, sucursal.isEstado());
            stmt.setInt(5, sucursal.getSucursalId());
            stmt.executeUpdate();
        }
    }




}
