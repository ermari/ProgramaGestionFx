package CatalogoGestion.MasterCatalogo.Modelo;

import BD.BDconexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetalleCatalogoDAO
{

    public Optional<DetalleCatalogo> getById(int id) throws SQLException {
        String sql = "SELECT " +
                    " DETALLE_CATALOGO_ID, CODIGO_ITEM, NOMBRE_ITEM " +
                     " FROM detalle_catalogo WHERE DETALLE_CATALOGO_ID = ?";

        DetalleCatalogo detalleCatalogo = null;

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establece el valor del ID en el placeholder (?)
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Si se encuentra un resultado, crea el objeto Usuario
                    detalleCatalogo = new DetalleCatalogo(
                            rs.getInt("DETALLE_CATALOGO_ID"),
                            rs.getString("CODIGO_ITEM"),
                            rs.getString("NOMBRE_ITEM")

                    );
                }
            }
        }
        return Optional.ofNullable(detalleCatalogo);
    }


    public List<DetalleCatalogo> obtenerPorCodigoMaster(String codigoPadre) throws SQLException {
        String sql = "SELECT dc.DETALLE_CATALOGO_ID, dc.CODIGO_ITEM, dc.NOMBRE_ITEM  " +
                      "FROM   master_catalogo mc " +
                      "join   detalle_catalogo dc on mc.MASTER_CATALOGO_ID=dc.MASTER_CATALOGO_ID " +
                      "WHERE mc.codigo =?";

        List<DetalleCatalogo> lista = new ArrayList<>();

        try (Connection conn = BDconexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoPadre);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleCatalogo detalleCatalogo = new DetalleCatalogo(
                            rs.getInt("DETALLE_CATALOGO_ID"),
                            rs.getString("CODIGO_ITEM"),
                            rs.getString("NOMBRE_ITEM")
                    );
                    lista.add(detalleCatalogo);
                }
            }
        }
        return lista;
    }



}
