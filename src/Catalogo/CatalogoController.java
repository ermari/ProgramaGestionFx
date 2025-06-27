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

    @FXML
    private TreeTableColumn<Catalogo, String> colExpandible;

    @FXML
    private TreeTableColumn<Catalogo, Void> colAccion; // Columna de acción

    private CatalogoDAO catalogoDAO = new CatalogoDAO();

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new TreeItemPropertyValueFactory<>("codigo"));
        colValor.setCellValueFactory(new TreeItemPropertyValueFactory<>("valor"));
        colDescripcion.setCellValueFactory(new TreeItemPropertyValueFactory<>("descripcion"));
        colExpandible.setCellValueFactory(new TreeItemPropertyValueFactory<>("expandible"));
     treeTableCatalogos.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue();

            // Ajusta estos porcentajes según tus necesidades (deben sumar aproximadamente 1.0 o 100%)
            colCodigo.setPrefWidth(tableWidth * 0.20); // 10% del ancho total
            colValor.setPrefWidth(tableWidth * 0.35); // 15%
            colDescripcion.setPrefWidth(tableWidth * 0.35); // 45%
            colExpandible.setPrefWidth(tableWidth * 0.10); // 15%  (Asegúrate de que la suma sea 1.0)

        });

        cargarDatos();

        treeTableCatalogos.setRowFactory(tv -> {
            TreeTableRow<Catalogo> row = new TreeTableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                row.setContextMenu(null); // Limpiar menú existente
                if (newItem != null && !row.isEmpty()) {
                    ContextMenu menu = new ContextMenu();

                    // Opciones estáticas que siempre se muestran
                    MenuItem editar = new MenuItem("Editar");
                    editar.setOnAction(e -> abrirFormularioEdicion(newItem));

                    MenuItem eliminar = new MenuItem("Eliminar");
                    eliminar.setOnAction(e -> eliminarCatalogo(newItem));

                    // Lógica para el sub-registro: solo si el ítem seleccionado es expandible ("S")
                    if ("S".equals(newItem.getExpandible())) {
                        MenuItem crearSubRegistro = new MenuItem("Crear Sub-Registro");
                        crearSubRegistro.setOnAction(e -> crearSubRegistro(newItem)); // Llama al nuevo método
                        menu.getItems().add(crearSubRegistro);
                    }

                    // Siempre añadir Editar y Eliminar
                    menu.getItems().addAll(editar, eliminar);

                    row.setContextMenu(menu);
                }
            });

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


    /**
     * Abre el formulario para crear un nuevo sub-registro (hijo) bajo el catálogo padre especificado.
     * Solo debe ser llamado para catálogos expandibles.
     * @param padre El catálogo bajo el cual se creará el nuevo sub-registro.
     */
    private void crearSubRegistro(Catalogo padre) {
        // Validación extra: Asegurarse de que el padre sea expandible.
        // Aunque el menú contextual ya filtrará esto, es una buena práctica.
        if (!"S".equals(padre.getExpandible())) {
            System.err.println("Error: No se puede crear un sub-registro en un catálogo no expandible.");
            // Opcional: Mostrar una alerta al usuario si esto ocurre
            // MensajeUtil.mostrarAlerta(Alert.AlertType.WARNING, "Operación no permitida", null, "Solo se pueden crear sub-registros bajo categorías expandibles.");
            return;
        }

        try {
            Catalogo nuevoHijo = new Catalogo();
            // El nuevo sub-registro tendrá como superior al catálogo 'padre'
            nuevoHijo.setCatalogoSup(padre.getCatalogoId());
            nuevoHijo.setExpandible("S"); // Valor inicial para el nuevo sub-registro (se puede cambiar en el form)
            // Aquí puedes inicializar otras propiedades por defecto si es necesario para un nuevo sub-registro

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogo(nuevoHijo); // Pasa el objeto nuevo inicializado con el superior correcto
            controller.setCatalogoController(this); // Pasa la referencia de este controlador para refrescar

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL); // Hace que la ventana sea modal
            stage.setTitle("Crear Nuevo Sub-Registro");
            stage.showAndWait(); // Espera a que se cierre la ventana del formulario

            refrescar(); // Refresca la TreeTableView después de cerrar el formulario
        } catch (IOException e) {
            e.printStackTrace();
            // MensajeUtil.mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir formulario", null, "No se pudo cargar el formulario de registro.");
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

    /*
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
    }*/

    /*
    private void crearSubCatalogo(Catalogo seleccionado) {
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
*/
    /*
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
*/


/*
        colCodigo.setPrefWidth(20); // Ancho preferido para el código
        colValor.setPrefWidth(350); // Ancho preferido para el valor
        colDescripcion.setPrefWidth(350); // Ancho preferido para la descripción
        colExpandible.setPrefWidth(100); // Ancho preferido para expandible

*/




}
