package Catalogo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    @FXML private TreeTableView<Catalogo> treeTableCatalogos;
    @FXML private TreeTableColumn<Catalogo, String> colCodigo;
    @FXML private TreeTableColumn<Catalogo, String> colValor;
    @FXML private TreeTableColumn<Catalogo, String> colDescripcion;
    @FXML private AnchorPane contentPane;

    private CatalogoDAO catalogoDAO = new CatalogoDAO();

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new TreeItemPropertyValueFactory<>("codigo"));
        colValor.setCellValueFactory(new TreeItemPropertyValueFactory<>("valor"));
        colDescripcion.setCellValueFactory(new TreeItemPropertyValueFactory<>("descripcion"));
        cargarDatos();
    }

    public void cargarDatos() {
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
                        root.getChildren().add(item);
                    }
                }
            }

            treeTableCatalogos.setRoot(root);
            treeTableCatalogos.setShowRoot(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        treeTableCatalogos.setRowFactory(tv -> {
            TreeTableRow<Catalogo> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    abrirFormularioEdicion(row.getItem());
                }
            });

            ContextMenu menu = new ContextMenu();
            MenuItem editar = new MenuItem("Editar");
            editar.setOnAction(e -> abrirFormularioEdicion(row.getItem()));
            menu.getItems().add(editar);
            row.setContextMenu(menu);

            return row;
        });
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
            stage.setTitle("Editar Catálogo");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void nuevoCatalogo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogoController(this); // para refrescar
            controller.setCatalogo(null); // nuevo

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Nuevo Catálogo");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refrescar() {
        cargarDatos();
    }
}
