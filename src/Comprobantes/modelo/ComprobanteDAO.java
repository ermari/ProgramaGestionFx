package Comprobantes.modelo;

import BD.BDconexion;
import Catalogo.Catalogo;
import Catalogo.CatalogoDAO;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import CatalogoGestion.MasterCatalogo.Modelo.DetalleCatalogo;
import CatalogoGestion.MasterCatalogo.Modelo.DetalleCatalogoDAO;
import CatalogoGestion.Periodo.Periodo;
import CatalogoGestion.Periodo.PeriodoDAO;
import Home.User.Modelo.Usuario;
import Home.User.Modelo.UsuarioDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ComprobanteDAO {

    private final CatalogoDAO cuentaDAO = new CatalogoDAO();
    private final DetalleCatalogoDAO detalleCatalogoDAO = new DetalleCatalogoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();
    private final PeriodoDAO periodoDAO = new PeriodoDAO(); // ✅ agregado

    // ================= INSERT ==================
    public void saveComprobante(Comprobante comprobante) throws SQLException {
        String insertComprobanteSQL = """
            INSERT INTO comprobante 
            (fecha_comprobante, numero_comprobante, concepto, usuario_id, sucursal_id, fecha_registro, tipo_documento_id, periodo_id) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String insertDetalleSQL = """
            INSERT INTO detalle_comprobante 
            (numero_linea, comprobante_id, cuenta_id, descripcion, debito, credito, usuario_id, fecha_registro) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        int usuarioId = comprobante.getUsuario().getUsuarioId();

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            // Insertar comprobante
            try (PreparedStatement stmtComprobante = conn.prepareStatement(insertComprobanteSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmtComprobante.setDate(1, Date.valueOf(comprobante.getFecha()));
                stmtComprobante.setString(2, comprobante.getNumeroComprobante());
                stmtComprobante.setString(3, comprobante.getConcepto());
                stmtComprobante.setInt(4, usuarioId);
                stmtComprobante.setInt(5, comprobante.getSucursal().getSucursalId());
                stmtComprobante.setDate(6, Date.valueOf(comprobante.getFechaRegistro()));
                stmtComprobante.setInt(7, comprobante.getTipoDocumento().getDetalleCatalogoId());
                stmtComprobante.setInt(8, comprobante.getPeriodo().getId());
                stmtComprobante.executeUpdate();

                try (ResultSet rs = stmtComprobante.getGeneratedKeys()) {
                    if (rs.next()) {
                        comprobante.setIdComprobante(rs.getInt(1));
                    } else {
                        throw new SQLException("Error al obtener el ID del comprobante generado.");
                    }
                }
            }

            // Insertar detalles
            try (PreparedStatement stmtDetalle = conn.prepareStatement(insertDetalleSQL)) {
                int numeroLinea = 1;
                for (DetalleComprobante detalle : comprobante.getDetalles()) {
                    stmtDetalle.setInt(1, numeroLinea++);
                    stmtDetalle.setInt(2, comprobante.getIdComprobante());
                    stmtDetalle.setInt(3, detalle.getContableCuenta().getCatalogoId());
                    stmtDetalle.setString(4, detalle.getDescripcion());
                    stmtDetalle.setBigDecimal(5, detalle.getDebito());
                    stmtDetalle.setBigDecimal(6, detalle.getCredito());
                    stmtDetalle.setInt(7, usuarioId);
                    stmtDetalle.setDate(8, Date.valueOf(detalle.getFechaRegistro()));
                    stmtDetalle.addBatch();
                }
                stmtDetalle.executeBatch();
            }

            conn.commit();
        }
    }

    // ================= SELECT BY ID ==================
    public Comprobante getById(int idComprobante) throws SQLException {
        String selectComprobanteSQL = """
            SELECT comprobante_id, fecha_comprobante, numero_comprobante, concepto, 
                   usuario_id, sucursal_id, fecha_registro, tipo_documento_id, periodo_id
            FROM comprobante 
            WHERE comprobante_id = ?
        """;

        String selectDetallesSQL = """
            SELECT detalle_id, numero_linea, cuenta_id, descripcion, 
                   debito, credito, usuario_id, fecha_registro 
            FROM detalle_comprobante 
            WHERE comprobante_id = ?
        """;

        Comprobante comprobante = null;

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(selectComprobanteSQL)) {
                stmt.setInt(1, idComprobante);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Usuario usuario = usuarioDAO.getById(rs.getInt("usuario_id")).orElse(null);
                        DetalleCatalogo tipoDocumento = detalleCatalogoDAO.getById(rs.getInt("tipo_documento_id")).orElse(null);
                        Sucursal sucursal = sucursalDAO.getById(rs.getInt("sucursal_id")).orElse(null);
                        Periodo periodo = periodoDAO.obtenerPorId(rs.getInt("periodo_id"));

                        Date sqlFecha = rs.getDate("fecha_comprobante");
                        Date sqlFechaRegistro = rs.getDate("fecha_registro");

                        comprobante = new Comprobante(
                                new SimpleIntegerProperty(rs.getInt("comprobante_id")),
                                sqlFecha != null ? sqlFecha.toLocalDate() : null,
                                rs.getString("numero_comprobante"),
                                rs.getString("concepto"),
                                usuario,
                                sqlFechaRegistro != null ? sqlFechaRegistro.toLocalDate() : null,
                                tipoDocumento,
                                sucursal,
                                periodo
                        );
                    }
                }
            }

            // Cargar detalles
            if (comprobante != null) {
                List<DetalleComprobante> detalles = new ArrayList<>();
                try (PreparedStatement stmtDet = conn.prepareStatement(selectDetallesSQL)) {
                    stmtDet.setInt(1, idComprobante);
                    try (ResultSet rsDet = stmtDet.executeQuery()) {
                        while (rsDet.next()) {
                            Catalogo cuenta = cuentaDAO.getPorId(rsDet.getInt("cuenta_id"));
                            Usuario usuarioDetalle = usuarioDAO.getById(rsDet.getInt("usuario_id")).orElse(null);

                            DetalleComprobante detalle = new DetalleComprobante(
                                    rsDet.getInt("detalle_id"),
                                    rsDet.getInt("numero_linea"),
                                    comprobante,
                                    cuenta,
                                    rsDet.getString("descripcion"),
                                    rsDet.getBigDecimal("debito"),
                                    rsDet.getBigDecimal("credito"),
                                    usuarioDetalle,
                                    rsDet.getDate("fecha_registro").toLocalDate()
                            );
                            detalles.add(detalle);
                        }
                    }
                }
                comprobante.setDetalles((ObservableList<DetalleComprobante>) detalles);
            }
        }
        return comprobante;
    }

    // ================= SELECT LIST ==================
    public List<Comprobante> obtenerComprobantes(LocalDate fechaInicio, LocalDate fechaFin, String numeroComprobante) throws SQLException {
        List<Comprobante> comprobantes = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
           SELECT c.comprobante_id, c.fecha_comprobante, c.numero_comprobante,
                          c.usuario_id, c.fecha_registro, c.tipo_documento_id,
                          c.sucursal_id, c.periodo_id, c.concepto,
                          d.debito, d.credito 
                   FROM comprobante c
                   JOIN (
                       SELECT comprobante_id, SUM(debito) AS debito, SUM(credito) AS credito
                       FROM detalle_comprobante d
                       GROUP BY comprobante_id
                   ) d ON c.comprobante_id = d.comprobante_id
                   WHERE 1=1
        """);

        if (fechaInicio != null) sql.append(" AND fecha_comprobante >= ?");
        if (fechaFin != null) sql.append(" AND fecha_comprobante <= ?");
        if (numeroComprobante != null && !numeroComprobante.isEmpty()) sql.append(" AND numero_comprobante LIKE ?");
        sql.append(" ORDER BY fecha_comprobante DESC, numero_comprobante ASC");

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (fechaInicio != null) pstmt.setDate(paramIndex++, Date.valueOf(fechaInicio));
            if (fechaFin != null) pstmt.setDate(paramIndex++, Date.valueOf(fechaFin));
            if (numeroComprobante != null && !numeroComprobante.isEmpty()) pstmt.setString(paramIndex++, "%" + numeroComprobante + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = usuarioDAO.getById(rs.getInt("usuario_id")).orElse(null);
                    DetalleCatalogo tipoDocumento = detalleCatalogoDAO.getById(rs.getInt("tipo_documento_id")).orElse(null);
                    Sucursal sucursal = sucursalDAO.getById(rs.getInt("sucursal_id")).orElse(null);
                    Periodo periodo = periodoDAO.obtenerPorId(rs.getInt("periodo_id"));

                    Date sqlFecha = rs.getDate("fecha_comprobante");
                    Date sqlFechaRegistro = rs.getDate("fecha_registro");

                    Comprobante comprobante = new Comprobante(
                            new SimpleIntegerProperty(rs.getInt("comprobante_id")),
                            sqlFecha != null ? sqlFecha.toLocalDate() : null,
                            rs.getString("numero_comprobante"),
                            rs.getString("concepto"),
                            usuario,
                            sqlFechaRegistro != null ? sqlFechaRegistro.toLocalDate() : null,
                            tipoDocumento,
                            sucursal,
                            periodo
                    );

                    // 👇 asignar débitos y créditos
                    comprobante.setDebito(rs.getBigDecimal("debito"));
                    comprobante.setCredito(rs.getBigDecimal("credito"));

                    comprobantes.add(comprobante);

                }
            }

            // Cargar detalles de todos los comprobantes en lote
            if (!comprobantes.isEmpty()) {
                List<Integer> idsComprobantes = comprobantes.stream()
                        .map(Comprobante::getIdComprobante)
                        .toList();

                String placeholders = idsComprobantes.stream().map(id -> "?").collect(Collectors.joining(","));
                String sqlDetalles = "SELECT detalle_id, numero_linea, comprobante_id, cuenta_id, descripcion, debito, credito, usuario_id, fecha_registro FROM detalle_comprobante WHERE comprobante_id IN (" + placeholders + ")";

                try (PreparedStatement pstmtDetalles = conn.prepareStatement(sqlDetalles)) {
                    for (int i = 0; i < idsComprobantes.size(); i++) {
                        pstmtDetalles.setInt(i + 1, idsComprobantes.get(i));
                    }

                    try (ResultSet rsDetalles = pstmtDetalles.executeQuery()) {
                        Map<Integer, Comprobante> mapaComprobantes = comprobantes.stream()
                                .collect(Collectors.toMap(Comprobante::getIdComprobante, comp -> comp));

                        while (rsDetalles.next()) {
                            int idComp = rsDetalles.getInt("comprobante_id");
                            Comprobante comp = mapaComprobantes.get(idComp);
                            if (comp != null) {
                                Catalogo cuenta = catalogoDAO.getPorId(rsDetalles.getInt("cuenta_id"));
                                Usuario usuarioDetalle = usuarioDAO.getById(rsDetalles.getInt("usuario_id")).orElse(null);

                                DetalleComprobante detalle = new DetalleComprobante(
                                        rsDetalles.getInt("detalle_id"),
                                        rsDetalles.getInt("numero_linea"),
                                        comp,
                                        cuenta,
                                        rsDetalles.getString("descripcion"),
                                        rsDetalles.getBigDecimal("debito"),
                                        rsDetalles.getBigDecimal("credito"),
                                        usuarioDetalle,
                                        rsDetalles.getDate("fecha_registro").toLocalDate()
                                );
                                comp.addDetalle(detalle);
                            }
                        }
                    }
                }
            }
        }
        return comprobantes;
    }

    // ================= DELETE ==================
    public void eliminarComprobante(int idComprobante) throws SQLException {
        String sqlDeleteDetalles = "DELETE FROM detalle_comprobante WHERE comprobante_id = ?";
        String sqlDeleteComprobante = "DELETE FROM comprobante WHERE comprobante_id = ?";

        try (Connection conn = BDconexion.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtDetalles = conn.prepareStatement(sqlDeleteDetalles)) {
                pstmtDetalles.setInt(1, idComprobante);
                pstmtDetalles.executeUpdate();
            }

            try (PreparedStatement pstmtComprobante = conn.prepareStatement(sqlDeleteComprobante)) {
                pstmtComprobante.setInt(1, idComprobante);
                pstmtComprobante.executeUpdate();
            }

            conn.commit();
        }
    }
}
