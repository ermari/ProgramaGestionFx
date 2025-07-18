package Comprobantes;

import Catalogo.Catalogo;
import Catalogo.CatalogoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class BusquedaCatalogoController {

    @FXML private TreeTableView<Catalogo> treeTableCatalogos;
    @FXML private TreeTableColumn<Catalogo, String> colCodigo;
    @FXML private TreeTableColumn<Catalogo, String> colValor;
    @FXML private TextField filterField;

    private Consumer<Catalogo> onCuentaSeleccionada;
    private CatalogoDAO catalogoDAO = new CatalogoDAO();

    public void initialize() throws SQLException {
        colCodigo.setCellValueFactory(new TreeItemPropertyValueFactory<>("codigo"));
        colValor.setCellValueFactory(new TreeItemPropertyValueFactory<>("valor"));
        cargarCatalogo(null);

        filterField.setOnKeyReleased(this::filtrar);
    }

    private void filtrar(KeyEvent event) {
        try {
            cargarCatalogo(filterField.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setOnCuentaSeleccionada(Consumer<Catalogo> callback) {
        this.onCuentaSeleccionada = callback;
    }

    @FXML
    public void seleccionarCuenta() {
        TreeItem<Catalogo> item = treeTableCatalogos.getSelectionModel().getSelectedItem();
        if (item != null && onCuentaSeleccionada != null) {
            onCuentaSeleccionada.accept(item.getValue());
            ((Stage) treeTableCatalogos.getScene().getWindow()).close();
        }
    }

    private void cargarCatalogo(String filtro) throws SQLException {
        treeTableCatalogos.setRoot(null);
        TreeItem<Catalogo> root = new TreeItem<>(new Catalogo(0, null, "", "Raíz", "", 0, false));
        Map<Integer, TreeItem<Catalogo>> mapa = new HashMap<>();
        mapa.put(0, root);

        List<Catalogo> listaCatalogo = catalogoDAO.obtenerTodos();

        for (Catalogo item : listaCatalogo) {
            boolean coincideFiltro = filtro == null || filtro.isEmpty()
                    || item.getCodigo().toLowerCase().contains(filtro.toLowerCase())
                    || item.getValor().toLowerCase().contains(filtro.toLowerCase());

            if (!coincideFiltro) continue;

            TreeItem<Catalogo> nodo = new TreeItem<>(item);
            mapa.put(item.getCatalogoId(), nodo);

            int idPadre = (item.getCatalogoSup() != null) ? item.getCatalogoSup() : 0;
            TreeItem<Catalogo> padre = mapa.getOrDefault(idPadre, root);
            padre.getChildren().add(nodo);
        }

        treeTableCatalogos.setRoot(root);
        treeTableCatalogos.setShowRoot(false);
    }


}