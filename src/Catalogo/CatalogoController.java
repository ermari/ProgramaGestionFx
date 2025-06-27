package Catalogo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoController {

    @FXML
    private TreeTableView<Catalogo> treeTableCatalogos;
    @FXML
    private TreeTableColumn<Catalogo, String> colCodigo;
    @FXML
    private TreeTableColumn<Catalogo, String> colValor;
    @FXML
    private TreeTableColumn<Catalogo, String> colDescripcion;

    private CatalogoDAO catalogoDAO = new CatalogoDAO();

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new TreeItemPropertyValueFactory<>("codigo"));
        colValor.setCellValueFactory(new TreeItemPropertyValueFactory<>("valor"));
        colDescripcion.setCellValueFactory(new TreeItemPropertyValueFactory<>("descripcion"));

        cargarDatos();

        treeTableCatalogos.setRowFactory(tv -> {
            TreeTableRow<Catalogo> row = new TreeTableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Catalogo catalogo = row.getItem();
                    abrirFormularioEdicion(catalogo);
                }
            });

            ContextMenu menu = new ContextMenu();

            MenuItem editar = new MenuItem("Editar");
            editar.setOnAction(e -> abrirFormularioEdicion(row.getItem()));

            MenuItem eliminar = new MenuItem("Eliminar");
            eliminar.setOnAction(e -> eliminarCatalogo(row.getItem()));


            MenuItem crearHijo = new MenuItem("Crear Item");
            crearHijo.setOnAction(e -> crearHijo(row.getItem()));

            MenuItem crearHermano = new MenuItem("Crear Sub Catalogo");
            crearHermano.setOnAction(e -> crearHermano(row.getItem()));

            menu.getItems().addAll(editar, eliminar, crearHermano, crearHijo );
            row.setContextMenu(menu);

            return row;
        });
    }

    private void cargarDatos() {
        try {
            List<Catalogo> todos = catalogoDAO.obtenerTodos();
            Map<Integer, TreeItem<Catalogo>> mapa = new HashMap<>();

            TreeItem<Catalogo> root = new TreeItem<>(new Catalogo(0, null, "Raíz", "Raíz", "", 0));
            root.setExpanded(true);

            for (Catalogo cat : todos) {
                TreeItem<Catalogo> item = new TreeItem<>(cat);
                mapa.put(cat.getCatalogoId(), item);
            }

            for (Catalogo cat : todos) {
                TreeItem<Catalogo> item = mapa.get(cat.getCatalogoId());
                if (cat.getCatalogoSup() == null) {
                    root.getChildren().add(item);
                } else {
                    TreeItem<Catalogo> padre = mapa.get(cat.getCatalogoSup());
                    if (padre != null) {
                        padre.getChildren().add(item);
                    } else {
                        root.getChildren().add(item); // fallback si no encuentra padre
                    }
                }
            }

            treeTableCatalogos.setRoot(root);
            treeTableCatalogos.setShowRoot(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirFormularioEdicion(Catalogo catalogo) {
        try {
            int id = catalogo.getCatalogoId();
            Catalogo catalogEditar = catalogoDAO.getPorId(id);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogo(catalogEditar);
            controller.setCatalogoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Catálogo");
            stage.showAndWait();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void nuevoCatalogo() {
        try {
            Catalogo nuevo = new Catalogo();
            nuevo.setExpandible("S"); // siempre S para nuevos padres

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogo(nuevo);
            controller.setCatalogoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Catálogo Padre");
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void crearHijo(Catalogo padre) {
        try {
            if (!"S".equals(padre.getExpandible())) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("No permitido");
                alerta.setHeaderText(null);
                alerta.setContentText("Este nodo no permite crear hijos porque no es expandible.");
                alerta.showAndWait();
                return;
            }

            Catalogo hijo = new Catalogo();
            hijo.setCatalogoSup(padre.getCatalogoId());
            hijo.setExpandible("S"); // valor inicial

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogo(hijo);
            controller.setCatalogoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Hijo");
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void crearHermano(Catalogo seleccionado) {
        try {
            if (seleccionado.getCatalogoSup() == null) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("No permitido");
                alerta.setHeaderText(null);
                alerta.setContentText("No se puede crear hermano para un nodo raíz.");
                alerta.showAndWait();
                return;
            }

            Catalogo hermano = new Catalogo();
            hermano.setCatalogoSup(seleccionado.getCatalogoSup());
            hermano.setExpandible("S"); // valor inicial

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogo(hermano);
            controller.setCatalogoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Hermano");
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void eliminarCatalogo(Catalogo cat) {
        try {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmar eliminación");
            alerta.setHeaderText(null);
            alerta.setContentText("¿Está seguro de eliminar este catálogo?");

            if (alerta.showAndWait().get() == ButtonType.OK) {
                catalogoDAO.eliminar(cat.getCatalogoId());
                refrescar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refrescar() {
        cargarDatos();
    }
}
