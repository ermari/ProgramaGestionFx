package Catalogo;

import BD.BDconexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {
    public List<Catalogo> obtenerTodos() throws SQLException {
        List<Catalogo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Catalogo ORDER BY orden";

        PreparedStatement ps = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Catalogo cat = new Catalogo(
                    rs.getInt("catalogo_id"),
                    rs.getObject("catalogo_sup") != null ? rs.getInt("catalogo_sup") : null,
                    rs.getString("codigo"),
                    rs.getString("valor"),
                    rs.getString("descripcion"),
                    rs.getInt("orden")
            );

            // AGREGAR: leer el campo expandible
            cat.setExpandible(rs.getString("expandible"));

            lista.add(cat);
        }

        return lista;
    }

    public Catalogo getPorId(int id) throws SQLException {
        Catalogo catalogo = null;
        String sql = "SELECT * FROM Catalogo WHERE catalogo_id = ?";
        PreparedStatement ps = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            catalogo = new Catalogo(
                    rs.getInt("catalogo_id"),
                    rs.getObject("catalogo_sup") != null ? rs.getInt("catalogo_sup") : null,
                    rs.getString("codigo"),
                    rs.getString("valor"),
                    rs.getString("descripcion"),
                    rs.getInt("orden")
            );

            // AGREGAR: leer el campo expandible
            catalogo.setExpandible(rs.getString("expandible"));
        }

        return catalogo;
    }

    public void insertar(Catalogo cat) throws SQLException {
        String sql = "INSERT INTO Catalogo (catalogo_sup, codigo, valor, descripcion, orden, expandible) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
        ps.setObject(1, cat.getCatalogoSup());
        ps.setString(2, cat.getCodigo());
        ps.setString(3, cat.getValor());
        ps.setString(4, cat.getDescripcion());
        ps.setInt(5, cat.getOrden());
        ps.setString(6, cat.getExpandible());
        ps.executeUpdate();
    }

    public void actualizar(Catalogo cat) throws SQLException {
        String sql = "UPDATE Catalogo SET catalogo_sup=?, codigo=?, valor=?, descripcion=?, orden=?, expandible=? WHERE catalogo_id=?";
        PreparedStatement ps = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
        ps.setObject(1, cat.getCatalogoSup());
        ps.setString(2, cat.getCodigo());
        ps.setString(3, cat.getValor());
        ps.setString(4, cat.getDescripcion());
        ps.setInt(5, cat.getOrden());
        ps.setString(6, cat.getExpandible());
        ps.setInt(7, cat.getCatalogoId());
        ps.executeUpdate();
    }

    public void eliminar(int catalogoId) throws SQLException {
        String sql = "DELETE FROM Catalogo WHERE catalogo_id = ?";
        PreparedStatement ps = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
        ps.setInt(1, catalogoId);
        ps.executeUpdate();
    }
}
