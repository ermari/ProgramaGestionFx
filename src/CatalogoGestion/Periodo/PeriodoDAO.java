package CatalogoGestion.Periodo;

import BD.BDconexion;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeriodoDAO {

    /**
     * Inserta un nuevo periodo en la base de datos.
     * @param periodo El objeto Periodo a guardar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public static void guardarPeriodo(Periodo periodo) throws SQLException {
        String sql = "INSERT INTO periodo (nombre, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, periodo.getNombre());
            ps.setDate(2, Date.valueOf(periodo.getFechaInicio()));
            ps.setDate(3, Date.valueOf(periodo.getFechaFin()));
            ps.setBoolean(4, periodo.isEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    periodo.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Obtiene una lista de todos los periodos de la base de datos.
     * @return Una lista de objetos Periodo.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Periodo> listarPeriodos() throws SQLException {
        List<Periodo> lista = new ArrayList<>();
        String sql = "SELECT * FROM periodo";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearPeriodoDesdeResultSet(rs));
            }
        }
        return lista;
    }

    /**
     * Obtiene un periodo por su ID.
     * @param id El ID del periodo.
     * @return El objeto Periodo, o null si no se encuentra.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public Periodo obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM periodo WHERE periodo_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearPeriodoDesdeResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Actualiza un periodo existente en la base de datos.
     * @param periodo El objeto Periodo con los datos actualizados.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public static void actualizarPeriodo(Periodo periodo) throws SQLException {
        String sql = "UPDATE periodo SET nombre = ?, fecha_inicio = ?, fecha_fin = ?, estado = ? WHERE periodo_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, periodo.getNombre());
            ps.setDate(2, Date.valueOf(periodo.getFechaInicio()));
            ps.setDate(3, Date.valueOf(periodo.getFechaFin()));
            ps.setBoolean(4, periodo.isEstado());
            ps.setInt(5, periodo.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un periodo de la base de datos por su ID.
     * @param id El ID del periodo a eliminar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void eliminarPeriodo(int id) throws SQLException {
        String sql = "DELETE FROM periodo WHERE periodo_id = ?";
        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Método auxiliar para crear un objeto Periodo a partir de un ResultSet.
     * @param rs El ResultSet del cual leer los datos.
     * @return Un objeto Periodo completamente poblado.
     * @throws SQLException Si ocurre un error al leer del ResultSet.
     */
    private Periodo crearPeriodoDesdeResultSet(ResultSet rs) throws SQLException {
        Periodo periodo = new Periodo();
        periodo.setId(rs.getInt("periodo_id"));
        periodo.setNombre(rs.getString("nombre"));
        periodo.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        periodo.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        return periodo;
    }

    /**
     * Obtiene un periodo Activo
     * @return El objeto Periodo, o null si no se encuentra.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Periodo> obtenerPeriodosActivos() throws SQLException {
        String sql = "SELECT periodo_id, nombre, fecha_inicio, fecha_fin, estado, " +
                "CONCAT('Desde ', DATE_FORMAT(fecha_inicio, '%d/%m/%Y'), " +
                "' hasta ', DATE_FORMAT(fecha_fin, '%d/%m/%Y')) AS descripcion " +
                "FROM periodo WHERE estado = 1";

        List<Periodo> periodos = new ArrayList<>();

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Periodo p = new Periodo();
                p.setId(rs.getInt("periodo_id"));
                p.setNombre(rs.getString("nombre"));
                p.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                p.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                p.setDescripcion(rs.getString("descripcion")); // <-- aquí ya viene la concatenación
                periodos.add(p);
            }
        }

        return periodos;
    }



}