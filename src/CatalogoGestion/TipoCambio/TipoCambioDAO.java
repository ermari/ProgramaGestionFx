package CatalogoGestion.TipoCambio;

import BD.BDconexion;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TipoCambioDAO {

    // Método para obtener la conexión (debes implementar esta parte)
   /* private Connection getConnection() throws SQLException {
        // Ejemplo de conexión simple. Reemplaza con tus datos.
        String url = "jdbc:mysql://localhost:3306/nombre_de_tu_db";
        String user = "tu_usuario";
        String password = "tu_password";
        return DriverManager.getConnection(url, user, password);
    }
    */

    /**
     * Lista todos los registros de tipo de cambio.
     * @return Una lista de objetos TipoCambio.
     */
    public List<TipoCambio> listar() {
        List<TipoCambio> lista = new ArrayList<>();
        String sql = "SELECT * FROM TipoCambio ORDER BY fechaInicio DESC";
        try (Connection conn =  BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TipoCambio tc = new TipoCambio(
                        rs.getInt("tipoCambioId"),
                        rs.getDate("fechaInicio").toLocalDate(),
                        rs.getDate("fechaFin").toLocalDate(),
                        rs.getBigDecimal("valor")
                );
                lista.add(tc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Lista los registros de tipo de cambio a partir de una fecha de inicio.
     * @param fechaInicio La fecha de inicio desde la cual buscar.
     * @return Una lista de objetos TipoCambio.
     */
    public List<TipoCambio> listarPorFechaInicio(LocalDate fechaInicio) {
        List<TipoCambio> lista = new ArrayList<>();
        String sql = "SELECT * FROM TipoCambio WHERE fechaInicio >= ? ORDER BY fechaInicio ASC";
        try (Connection conn =  BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TipoCambio tc = new TipoCambio(
                            rs.getInt("tipoCambioId"),
                            rs.getDate("fechaInicio").toLocalDate(),
                            rs.getDate("fechaFin").toLocalDate(),
                            rs.getBigDecimal("valor")
                    );
                    lista.add(tc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Lista los registros de tipo de cambio entre dos fechas.
     * @param fechaInicio La fecha de inicio del rango.
     * @param fechaFin La fecha de fin del rango.
     * @return Una lista de objetos TipoCambio.
     */
    public List<TipoCambio> listarEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<TipoCambio> lista = new ArrayList<>();
        String sql = "SELECT * FROM TipoCambio WHERE fechaInicio BETWEEN ? AND ? ORDER BY fechaInicio ASC";
        try (Connection conn =  BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TipoCambio tc = new TipoCambio(
                            rs.getInt("tipoCambioId"),
                            rs.getDate("fechaInicio").toLocalDate(),
                            rs.getDate("fechaFin").toLocalDate(),
                            rs.getBigDecimal("valor")
                    );
                    lista.add(tc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Crea un nuevo registro de tipo de cambio.
     * @param tc El objeto TipoCambio a crear.
     */
    public void crear(TipoCambio tc) {
        String sql = "INSERT INTO TipoCambio (fechaInicio, fechaFin, valor) VALUES (?, ?, ?)";
        try (Connection conn =  BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(tc.getFechaInicio()));
            stmt.setDate(2, Date.valueOf(tc.getFechaFin()));
            stmt.setBigDecimal(3, tc.getValor());
            stmt.executeUpdate();
            System.out.println("Registro de tipo de cambio creado exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifica un registro de tipo de cambio existente.
     * @param tc El objeto TipoCambio con los datos actualizados.
     */
    public void modificar(TipoCambio tc) {
        String sql = "UPDATE TipoCambio SET fechaInicio = ?, fechaFin = ?, valor = ? WHERE tipoCambioId = ?";
        try (Connection conn =  BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(tc.getFechaInicio()));
            stmt.setDate(2, Date.valueOf(tc.getFechaFin()));
            stmt.setBigDecimal(3, tc.getValor());
            stmt.setInt(4, tc.getTipoCambioId());
            stmt.executeUpdate();
            System.out.println("Registro de tipo de cambio modificado exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina un registro de tipo de cambio por su ID.
     * @param tipoCambioId El ID del registro a eliminar.
     */
    public void eliminar(int tipoCambioId) {
        String sql = "DELETE FROM TipoCambio WHERE tipoCambioId = ?";
        try (Connection conn =  BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tipoCambioId);
            stmt.executeUpdate();
            System.out.println("Registro de tipo de cambio eliminado exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}