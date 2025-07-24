package Comprobantes.Controller;

import Catalogo.Catalogo;
import Catalogo.CatalogoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class BusquedaCatalogoController {

    @FXML private TreeTableView<Catalogo> treeTableCatalogos;
    @FXML private TreeTableColumn<Catalogo, String> colCodigo;
    @FXML private TreeTableColumn<Catalogo, String> colValor;
    @FXML private TreeTableColumn<Catalogo, Void> colSeleccionar;
    @FXML private TextField filterField;


    private Consumer<Catalogo> onCuentaSeleccionada;
    private CatalogoDAO catalogoDAO = new CatalogoDAO();

    public void initialize() throws SQLException {

        colCodigo.setCellValueFactory(new TreeItemPropertyValueFactory<>("codigo"));
        colValor.setCellValueFactory(new TreeItemPropertyValueFactory<>("valor"));
        cargarCatalogo(null);
        configurarColumnaSeleccion();

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

    private void configurarColumnaSeleccion() {
        colSeleccionar.setCellFactory(col -> new TreeTableCell<>() {
            private final Button btn = new Button("");

            {
                // Configurar ícono solo una vez
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/resources/images/check32.png")));
                icon.setFitWidth(16);
                icon.setFitHeight(16);
                btn.setGraphic(icon);
                btn.setContentDisplay(ContentDisplay.LEFT); // Ícono a la izquierda del texto

                btn.setOnAction(e -> {
                    Catalogo cuenta = getTreeTableRow().getItem();
                    if (cuenta != null && onCuentaSeleccionada != null) {

                        onCuentaSeleccionada.accept(cuenta);
                        // Cierra la ventana actual (modal)
                        Stage stage = (Stage) getScene().getWindow();
                        stage.close();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);
                if (empty || getTreeTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Catalogo cuenta = getTreeTableRow().getItem();
                    // Mostrar botón solo para cuentas de detalle
                    if (!"S".equals(cuenta.getExpandible())) {
                        setGraphic(btn);
                        setStyle(""); // estilo normal para cuentas de detalle
                    } else {
                        setGraphic(null);
                        setStyle("-fx-text-fill: #007acc; -fx-font-weight: bold;");

                    }
                }
            }
        });
    }


}