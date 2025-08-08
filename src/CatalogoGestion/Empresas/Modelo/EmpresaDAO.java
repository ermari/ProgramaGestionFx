package CatalogoGestion.Empresas.Modelo;

import BD.BDconexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDAO {
    private Connection conn = BDconexion.getInstance().getConnection();

    public List<Empresa> listarEmpresas() throws SQLException {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT * FROM Empresa";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Empresa e = new Empresa();
                e.setEmpresaId(rs.getInt("empresa_id"));
                e.setNombre(rs.getString("nombre"));
                e.setRazonSocial(rs.getString("razon_social"));
                e.setRuc(rs.getString("ruc"));
                e.setDireccion(rs.getString("direccion"));
                e.setTelefono(rs.getString("telefono"));
                e.setEmail(rs.getString("email"));
                e.setRepresentante(rs.getString("representante_legal"));
                e.setTipoEmpresa(rs.getString("tipo_empresa"));
                e.setFechaConstitucion(rs.getDate("fecha_constitucion").toLocalDate());
                e.setEstado(rs.getBoolean("estado"));
                lista.add(e);
            }
        }
        return lista;
    }

    public Empresa obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE empresa_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Empresa e = new Empresa();
                    e.setEmpresaId(rs.getInt("empresa_id"));
                    e.setNombre(rs.getString("nombre"));
                    // Agrega más campos si tienes más
                    return e;
                }
            }
        }
        return null;
    }

    public void guardarEmpresa(Empresa e) throws SQLException {
        String sql = "INSERT INTO Empresa (nombre, razon_social, ruc, direccion, telefono, email, representante_legal, tipo_empresa, fecha_constitucion, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getRazonSocial());
            stmt.setString(3, e.getRuc());
            stmt.setString(4, e.getDireccion());
            stmt.setString(5, e.getTelefono());
            stmt.setString(6, e.getEmail());
            stmt.setString(7, e.getRepresentante());
            stmt.setString(8, e.getTipoEmpresa());
            stmt.setDate(9, Date.valueOf(e.getFechaConstitucion()));
            stmt.setBoolean(10, e.isEstado());
            stmt.executeUpdate();
        }
    }

    public void actualizarEmpresa(Empresa e) throws SQLException {
        String sql = "UPDATE Empresa SET nombre=?, razon_social=?, ruc=?, direccion=?, telefono=?, email=?, representante_legal=?, tipo_empresa=?, fecha_constitucion=?, estado=? WHERE empresa_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getRazonSocial());
            stmt.setString(3, e.getRuc());
            stmt.setString(4, e.getDireccion());
            stmt.setString(5, e.getTelefono());
            stmt.setString(6, e.getEmail());
            stmt.setString(7, e.getRepresentante());
            stmt.setString(8, e.getTipoEmpresa());
            stmt.setDate(9, Date.valueOf(e.getFechaConstitucion()));
            stmt.setBoolean(10, e.isEstado());
            stmt.setInt(11, e.getEmpresaId());
            stmt.executeUpdate();
        }
    }
}
