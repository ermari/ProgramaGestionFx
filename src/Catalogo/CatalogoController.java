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
import java.util.Optional;

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

        colCodigo.setPrefWidth(250);
        colValor.setPrefWidth(300);
        colDescripcion.setPrefWidth(300);


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

            MenuItem crearHijo = new MenuItem("Crear hijo");
            crearHijo.setOnAction(e -> abrirFormularioNuevo(row.getItem(), true)); // hijo

            MenuItem crearHermano = new MenuItem("Crear hermano");
            crearHermano.setOnAction(e -> abrirFormularioNuevo(row.getItem(), false)); // hermano

            MenuItem eliminar = new MenuItem("Eliminar");
            eliminar.setOnAction(e -> eliminarCatalogo(row.getItem()));

            menu.getItems().addAll(editar, crearHijo, crearHermano,eliminar);
            row.setContextMenu(menu);

            return row;
        });
    }


    private void abrirFormularioNuevo(Catalogo base, boolean esHijo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogoController(this);

            // Crear objeto nuevo con superior definido
            Catalogo nuevo = new Catalogo();
            if (esHijo) {
                nuevo.setCatalogoSup(base.getCatalogoId());
            } else {
                nuevo.setCatalogoSup(base.getCatalogoSup()); // mismo padre que el hermano
            }

            controller.setCatalogo(nuevo);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle(esHijo ? "Nuevo Hijo de " + base.getValor() : "Nuevo Hermano de " + base.getValor());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eliminarCatalogo(Catalogo catalogo) {
        try {
            // Validar hijos
            boolean tieneHijos = false;
            for (TreeItem<Catalogo> item : treeTableCatalogos.getRoot().getChildren()) {
                if (tieneHijos(item, catalogo.getCatalogoId())) {
                    tieneHijos = true;
                    break;
                }
            }

            if (tieneHijos) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("No se puede eliminar");
                alerta.setHeaderText("Este catálogo tiene elementos hijos.");
                alerta.setContentText("Elimine o mueva sus hijos antes de eliminar este nodo.");
                alerta.showAndWait();
                return;
            }

            // Confirmación
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminación");
            confirm.setHeaderText("¿Eliminar catálogo '" + catalogo.getValor() + "'?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                catalogoDAO.eliminar(catalogo.getCatalogoId());
                refrescar();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  //verificar si tiene hijos al elminar
    private boolean tieneHijos(TreeItem<Catalogo> padre, int id) {
        if (padre.getValue().getCatalogoSup() != null && padre.getValue().getCatalogoSup() == id) {
            return true;
        }
        for (TreeItem<Catalogo> hijo : padre.getChildren()) {
            if (tieneHijos(hijo, id)) {
                return true;
            }
        }
        return false;
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
            controller.setCatalogoController(this);

            Catalogo nuevoPadre = new Catalogo();
            nuevoPadre.setCatalogoSup(null); // sin padre → nodo raíz
            controller.setCatalogo(nuevoPadre);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Nuevo Catálogo Padre");
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
