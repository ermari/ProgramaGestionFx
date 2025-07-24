package Comprobantes.modelo;

import BD.BDconexion;
import Catalogo.Catalogo;
import Catalogo.CatalogoDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class ComprobanteDAO {

    private CatalogoDAO cuentaDAO = new CatalogoDAO(); // Para obtener los objetos Cuenta

    public void saveComprobante(Comprobante comprobante) throws SQLException {
        String insertComprobanteSQL = "INSERT INTO comprobantes (fecha, numero_comprobante, concepto) VALUES (?, ?, ?)";
        String insertDetalleSQL = "INSERT INTO detalle_comprobantes (id_comprobante, id_cuenta, debito, credito, descripcion) VALUES (?, ?, ?, ?,?)";

        //PreparedStatement ps = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);

        PreparedStatement stmtComprobante = null;
        PreparedStatement stmtDetalle = null;
        ResultSet rs = null;

        Connection conn = null;
        try {
            conn = BDconexion.getInstance().getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar el comprobante maestro
            stmtComprobante = conn.prepareStatement(insertComprobanteSQL, Statement.RETURN_GENERATED_KEYS);
            stmtComprobante.setDate(1, Date.valueOf(comprobante.getFecha()));
            stmtComprobante.setString(2, comprobante.getNumeroComprobante());
            stmtComprobante.setString(3, comprobante.getConcepto());
            stmtComprobante.executeUpdate();

            rs = stmtComprobante.getGeneratedKeys();
            int idComprobanteGenerado = 0;
            if (rs.next()) {
                idComprobanteGenerado = rs.getInt(1);
            } else {
                throw new SQLException("Error al obtener el ID del comprobante generado.");
            }
            comprobante.setIdComprobante(idComprobanteGenerado); // Asignar el ID generado al objeto

            // 2. Insertar los detalles del comprobante
            stmtDetalle = conn.prepareStatement(insertDetalleSQL);
            for (DetalleComprobante detalle : comprobante.getDetalles()) {
                stmtDetalle.setInt(1, idComprobanteGenerado);
                stmtDetalle.setInt(2, detalle.getContableCuenta().getCatalogoId());
                stmtDetalle.setDouble(3, detalle.getDebito());
                stmtDetalle.setDouble(4, detalle.getCredito());
                stmtDetalle.setString(5, detalle.getDescripcion());
                stmtDetalle.addBatch(); // Agregar al lote para inserción eficiente
            }
            stmtDetalle.executeBatch(); // Ejecutar todas las inserciones de detalle

            conn.commit(); // Confirmar la transacción
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Deshacer en caso de error
            }
            throw e; // Re-lanzar la excepción
        } finally {
            if (rs != null) rs.close();
            if (stmtComprobante != null) stmtComprobante.close();
            if (stmtDetalle != null) stmtDetalle.close();
            if (conn != null) conn.close();
        }
    }

    // Opcional: Método para cargar un comprobante con sus detalles (ejemplo)
    public Comprobante getComprobanteById(int idComprobante) throws SQLException {
        String selectComprobanteSQL = "SELECT id_comprobante, fecha, numero_comprobante, concepto FROM comprobantes WHERE id_comprobante = ?";
        String selectDetallesSQL = "SELECT id_detalle, id_cuenta, debito, credito FROM detalle_comprobantes WHERE id_comprobante = ?";

        Comprobante comprobante = null;
        try (Connection conn =BDconexion.getInstance().getConnection()){
            // Cargar el comprobante maestro
            try (PreparedStatement stmt = conn.prepareStatement(selectComprobanteSQL)) {
                stmt.setInt(1, idComprobante);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        comprobante = new Comprobante(
                                rs.getInt("id_comprobante"),
                                rs.getDate("fecha").toLocalDate(),
                                rs.getString("numero_comprobante"),
                                rs.getString("concepto")
                        );
                    }
                }
            }

            // Cargar los detalles si el comprobante existe
            if (comprobante != null) {
                List<DetalleComprobante> detalles = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(selectDetallesSQL)) {
                    stmt.setInt(1, idComprobante);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            int idCuenta = rs.getInt("id_cuenta");
                            Catalogo cuenta = cuentaDAO.getPorId(idCuenta); // Obtener la cuenta por ID
                            if (cuenta == null) {
                                // Manejar el error: cuenta no encontrada (ej. log, lanzar excepción)
                                System.err.println("Advertencia: Cuenta con ID " + idCuenta + " no encontrada para el detalle " + rs.getInt("id_detalle"));
                                continue;
                            }
                            DetalleComprobante detalle = new DetalleComprobante(
                                    rs.getInt("id_detalle"),
                                    idComprobante,
                                    cuenta,
                                    rs.getDouble("debito"),
                                    rs.getDouble("credito"),
                                    rs.getString("descripcion")
                            );
                                   detalles.add(detalle);
                        }
                    }
                }
                comprobante.setDetalles(detalles);
            }
        }
        return comprobante;
    }

    // Nuevo método para obtener comprobantes (con filtros opcionales)
    public List<Comprobante> obtenerComprobantes(LocalDate fechaInicio, LocalDate fechaFin, String numeroComprobante) throws SQLException {
        List<Comprobante> comprobantes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id_comprobante, fecha, numero_comprobante, concepto FROM comprobantes WHERE 1=1");
        if (fechaInicio != null) {
            sql.append(" AND fecha >= ?");
        }
        if (fechaFin != null) {
            sql.append(" AND fecha <= ?");
        }
        if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
            sql.append(" AND numero_comprobante LIKE ?"); // Búsqueda parcial
        }
        sql.append(" ORDER BY fecha DESC, numero_comprobante ASC");

        try (Connection conn =BDconexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (fechaInicio != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(fechaInicio));
            }
            if (fechaFin != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(fechaFin));
            }
            if (numeroComprobante != null && !numeroComprobante.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + numeroComprobante + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_comprobante");
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                String num = rs.getString("numero_comprobante");
                String conc = rs.getString("concepto");
                Comprobante comp = new Comprobante(id, fecha, num, conc);
                // Cargar los detalles para este comprobante
                comp.setDetalles(obtenerDetallesComprobante(id, conn)); // Pasa la misma conexión para la transacción
                comprobantes.add(comp);
            }
        }
        return comprobantes;
    }

    // Nuevo método para cargar detalles de un comprobante específico
    private List<DetalleComprobante> obtenerDetallesComprobante(int idComprobante, Connection conn) throws SQLException {
        List<DetalleComprobante> detalles = new ArrayList<>();
        // Asume que tienes un CatalogoDAO o un método para obtener Catalogo por ID
        // Aquí necesitarías instanciar un CatalogoDAO o pasarlo como parámetro
        CatalogoDAO catalogoDAO = new CatalogoDAO(); // O inyectarlo
        String sql = "SELECT id_cuenta, debito, credito FROM detalle_comprobantes WHERE id_comprobante = ?";

        // Utiliza la misma conexión para asegurar que es parte de la misma transacción (si fuera necesario)
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idComprobante);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int idCuenta = rs.getInt("id_cuenta");
                double debito = rs.getDouble("debito");
                double credito = rs.getDouble("credito");

                // Cargar el objeto Catalogo completo
                Catalogo cuenta = catalogoDAO.getPorId(idCuenta); // Necesitas este método en tu CatalogoDAO
                detalles.add(new DetalleComprobante(cuenta, debito, credito, "")); // Asume una descripción vacía al cargar
            }
        }
        return detalles;
    }

    // Nuevo método para eliminar un comprobante (y sus detalles)
    public void eliminarComprobante(int idComprobante) throws SQLException {
        String sqlDeleteDetalles = "DELETE FROM detalle_comprobantes WHERE id_comprobante = ?";
        String sqlDeleteComprobante = "DELETE FROM comprobantes WHERE id_comprobante = ?";

        try (Connection conn =BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            // Eliminar detalles primero
            try (PreparedStatement pstmtDetalles = conn.prepareStatement(sqlDeleteDetalles)) {
                pstmtDetalles.setInt(1, idComprobante);
                pstmtDetalles.executeUpdate();
            }

            // Luego eliminar el comprobante
            try (PreparedStatement pstmtComprobante = conn.prepareStatement(sqlDeleteComprobante)) {
                pstmtComprobante.setInt(1, idComprobante);
                pstmtComprobante.executeUpdate();
            }

            conn.commit(); // Confirmar transacción
        } catch (SQLException e) {
            // Manejar rollback
            throw e;
        }
    }


}