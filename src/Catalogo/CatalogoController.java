package Catalogo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable; // Importar Initializable
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;      // Para las imágenes de los botones
import javafx.scene.image.ImageView;   // Para mostrar las imágenes
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;     // Para agrupar los botones en la celda
import javafx.geometry.Pos;          // Para alinear los botones en el HBox
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;         // Para el CellFactory de la columna de acción

import java.io.IOException;
import java.net.URL;         // Necesario para Initializable
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle; // Necesario para Initializable
// import java.util.stream.Collectors; // No se usa en el código proporcionado, se puede eliminar si no es necesario

public class CatalogoController implements Initializable { // Implementar Initializable

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
    private TreeTableColumn<Catalogo, Void> colAccion; // Columna de acción de tipo Void

    private CatalogoDAO catalogoDAO = new CatalogoDAO();

    @Override // Sobreescribir el método initialize de Initializable
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar las factorías de valor para las columnas
        colCodigo.setCellValueFactory(new TreeItemPropertyValueFactory<>("codigo"));
        colValor.setCellValueFactory(new TreeItemPropertyValueFactory<>("valor"));
        colDescripcion.setCellValueFactory(new TreeItemPropertyValueFactory<>("descripcion"));
        colExpandible.setCellValueFactory(new TreeItemPropertyValueFactory<>("expandible"));

        // 2. Ajustar el ancho de las columnas de forma proporcional al ancho de la tabla
        treeTableCatalogos.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue();
            // Resta un pequeño valor para evitar problemas con la barra de desplazamiento
            tableWidth -= 2;

            // Ajusta estos porcentajes según tus necesidades (la suma debe ser 1.0 para 100%)
            colCodigo.setPrefWidth(tableWidth * 0.15); // 15%
            colValor.setPrefWidth(tableWidth * 0.25); // 25%
            colDescripcion.setPrefWidth(tableWidth * 0.30); // 30%
            colExpandible.setPrefWidth(tableWidth * 0.10); // 10%
            colAccion.setPrefWidth(tableWidth * 0.20); // 20% (necesita suficiente espacio para 3 botones)
        });

        // 3. Alineación del contenido de las celdas (el texto)
        colCodigo.setStyle("-fx-alignment: CENTER-LEFT;");
        colValor.setStyle("-fx-alignment: CENTER-LEFT;");
        colDescripcion.setStyle("-fx-alignment: CENTER-LEFT;");
        colExpandible.setStyle("-fx-alignment: CENTER;"); // Comúnmente centrado para 'S'/'N'

        // 4. Configurar la columna de acción con botones
        setupActionColumn();

        // 5. Configurar el RowFactory para el menú contextual y doble clic
        setupRowFactory();

        // 6. Cargar los datos iniciales en la tabla
        refrescar(); // Se debe llamar aquí para que la tabla se llene al iniciar
    }

    /**
     * Configura la columna de acción para que muestre botones de Editar, Eliminar y Crear Sub-Registro.
     */
    private void setupActionColumn() {
        Callback<TreeTableColumn<Catalogo, Void>, TreeTableCell<Catalogo, Void>> cellFactory =
                new Callback<TreeTableColumn<Catalogo, Void>, TreeTableCell<Catalogo, Void>>() {
                    @Override
                    public TreeTableCell<Catalogo, Void> call(final TreeTableColumn<Catalogo, Void> param) {
                        final TreeTableCell<Catalogo, Void> cell = new TreeTableCell<Catalogo, Void>() {

                            // Se crean los botones una sola vez por celda para optimizar el rendimiento
                            private final Button btnEditar = createButton("/resources/images/edit.png", "Editar");
                            private final Button btnEliminar = createButton("/resources/images/exit.png", "Eliminar");
                            private final Button btnCrearSubRegistro = createButton("/resources/images/Tree.png", "Sub-Registro");

                            {
                                // Asignar las acciones (eventos) a cada botón
                                btnEditar.setOnAction(event -> {
                                    Catalogo data = getTreeTableRow().getItem(); // Obtener el objeto Catalogo de la fila
                                    if (data != null) {
                                        abrirFormularioEdicion(data);
                                    }
                                });

                                btnEliminar.setOnAction(event -> {
                                    Catalogo data = getTreeTableRow().getItem();
                                    if (data != null) {
                                        eliminarCatalogo(data);
                                    }
                                });

                                btnCrearSubRegistro.setOnAction(event -> {
                                    Catalogo data = getTreeTableRow().getItem();
                                    if (data != null) {
                                        crearSubRegistro(data); // Llama al método unificado de creación
                                    }
                                });
                            }

                            /**
                             * Método auxiliar para crear un botón con una imagen y un tooltip.
                             */
                            private Button createButton(String imagePath, String tooltipText) {
                                Button button = new Button();
                                try {
                                    // Asegúrate de que las imágenes estén en el classpath correcto, por ejemplo:
                                    // src/main/resources/resources/images/edit.png
                                    // src/main/resources/resources/images/delete.png
                                    // src/main/resources/resources/images/add.png
                                    ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
                                    imageView.setFitHeight(16); // Ajustar tamaño de la imagen
                                    imageView.setFitWidth(16);
                                    button.setGraphic(imageView);
                                } catch (Exception e) {
                                    System.err.println("No se pudo cargar la imagen: " + imagePath + ". Usando texto de respaldo.");
                                    button.setText(tooltipText.substring(0, Math.min(tooltipText.length(), 4))); // Usar las primeras 4 letras
                                }
                                button.setTooltip(new Tooltip(tooltipText)); // Texto que aparece al pasar el ratón
                                button.getStyleClass().add("action-button"); // Clase CSS para estilos comunes
                                return button;
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null); // Si la celda está vacía, no mostrar nada
                                } else {
                                    Catalogo currentCatalogo = getTreeTableRow().getItem(); // Obtener el objeto Catalogo de la fila actual
                                    HBox hbox = new HBox(5); // Contenedor horizontal para los botones, con 5px de espacio
                                    hbox.setAlignment(Pos.CENTER); // Centrar los botones dentro de la celda

                                    // Añadir siempre los botones Editar y Eliminar
                                    hbox.getChildren().addAll(btnEditar, btnEliminar);

                                    // Añadir el botón "Crear Sub-Registro" solo si el catálogo es expandible ("S")
                                    if (currentCatalogo != null && "S".equals(currentCatalogo.getExpandible())) {
                                        // Importante: Verifica si el botón ya está en el HBox para evitar duplicados
                                        if (!hbox.getChildren().contains(btnCrearSubRegistro)) {
                                            hbox.getChildren().add(btnCrearSubRegistro);
                                        }
                                    } else {
                                        // Si no es expandible, asegúrate de que el botón de sub-registro no esté presente
                                        hbox.getChildren().remove(btnCrearSubRegistro);
                                    }
                                    setGraphic(hbox); // Establecer el HBox con los botones como el gráfico de la celda
                                }
                            }
                        };
                        return cell; // ¡IMPORTANTE! Retorna la celda aquí
                    }
                };
        colAccion.setCellFactory(cellFactory); // Asignar el CellFactory a tu columna de acción
    }

    /**
     * Configura el TreeTableView sin manejar clics derechos (menú contextual) ni dobles clics.
     */
    private void setupRowFactory() {
        treeTableCatalogos.setRowFactory(tv -> {
            TreeTableRow<Catalogo> row = new TreeTableRow<>();
            // La lógica para onMouseClicked (doble clic) y itemProperty().addListener (menú contextual)
            // ha sido eliminada. Las filas seguirán comportándose como filas normales de TreeTableView
            // (puedes seleccionarlas, expandir/colapsar nodos, etc.), pero no habrá acciones personalizadas
            // asociadas a clics individuales o dobles clics en la fila.
            return row;
        });
    }

    /**
     * Carga los datos de los catálogos desde el DAO y los organiza en la estructura jerárquica
     * del TreeTableView.
     */
    private void cargarDatos() {
        try {
            List<Catalogo> allCatalogos = catalogoDAO.obtenerTodos(); // Usar catalogoDAO.obtenerTodos()
            // Mapea cada Catalogo a un TreeItem para construir el árbol
            Map<Integer, TreeItem<Catalogo>> itemMap = new HashMap<>();
            for (Catalogo cat : allCatalogos) {
                itemMap.put(cat.getCatalogoId(), new TreeItem<>(cat));
            }

            // Crea un nodo raíz "ficticio" que no se mostrará, pero sirve para organizar
            TreeItem<Catalogo> rootItem = new TreeItem<>(new Catalogo(0, "ROOT", "Root", "Catálogo Raíz", 0, null, "S"));
            rootItem.setExpanded(true); // Expande la raíz ficticia para ver sus hijos

            for (Catalogo catalogo : allCatalogos) {
                TreeItem<Catalogo> item = itemMap.get(catalogo.getCatalogoId());
                // Si el catálogo no tiene superior (o es 0/null), es un elemento raíz de tu estructura
                if (catalogo.getCatalogoSup() == null || catalogo.getCatalogoSup() == 0) {
                    rootItem.getChildren().add(item);
                } else {
                    // Si tiene superior, busca el TreeItem de su padre y añádelo como hijo
                    TreeItem<Catalogo> parentItem = itemMap.get(catalogo.getCatalogoSup());
                    if (parentItem != null) {
                        parentItem.getChildren().add(item);
                    } else {
                        // Fallback: Si no se encuentra el padre (datos inconsistentes), añádelo a la raíz
                        rootItem.getChildren().add(item);
                        System.err.println("Advertencia: Catálogo con ID " + catalogo.getCatalogoId() + " tiene un superior inválido (" + catalogo.getCatalogoSup() + "). Añadido a la raíz.");
                    }
                }
            }

            // Ordenar los hijos de cada nodo por su propiedad 'orden'
            rootItem.getChildren().forEach(this::sortTreeItemChildren);

            treeTableCatalogos.setRoot(rootItem);
            treeTableCatalogos.setShowRoot(false); // Ocultar el nodo raíz ficticio
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar una alerta al usuario en caso de error
            new Alert(Alert.AlertType.ERROR, "Error al cargar los datos del catálogo: " + e.getMessage()).showAndWait();
        }
    }

    // Método auxiliar recursivo para ordenar los hijos de un TreeItem por su propiedad 'orden'
    private void sortTreeItemChildren(TreeItem<Catalogo> item) {
        if (item != null && !item.getChildren().isEmpty()) {
            item.getChildren().sort((item1, item2) -> Integer.compare(item1.getValue().getOrden(), item2.getValue().getOrden()));
            item.getChildren().forEach(this::sortTreeItemChildren); // Llamada recursiva para los nietos, etc.
        }
    }

    /**
     * Abre el formulario de edición para un Catálogo específico.
     * @param catalogoAEditar El objeto Catalogo que se va a editar.
     */
    private void abrirFormularioEdicion(Catalogo catalogoAEditar) {
        try {
            // Obtener la última versión del catálogo de la base de datos por si ha cambiado
            // Usar .obtenerPorId() del DAO
            Catalogo latestCatalogo = catalogoDAO.getPorId(catalogoAEditar.getCatalogoId()); // Usar obtenerPorId()
            if (latestCatalogo == null) {
                new Alert(Alert.AlertType.WARNING, "El catálogo seleccionado no existe o fue eliminado.").showAndWait();
                refrescar(); // Refrescar la tabla por si el elemento ya no está
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogo(latestCatalogo); // Pasar el objeto actualizado
            controller.setCatalogoController(this); // Pasar una referencia a este controlador para que pueda refrescar

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL); // Hacer la ventana modal (bloquea la ventana principal)
            stage.setTitle("Editar Catálogo: " + latestCatalogo.getDescripcion());
            stage.showAndWait(); // Esperar a que la ventana se cierre
            // El refrescar se llama desde RegistroCatalogoController cuando se guarda, no es necesario aquí.
        } catch (IOException | SQLException e) { // Capturar SQLException también por si getPorId la lanza
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar el formulario de edición: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Maneja la acción para crear un nuevo Catálogo de nivel raíz (sin superior).
     */
    @FXML
    private void nuevoCatalogo() { // Renombrado a handleNewCatalogo en el FXML si usas ese nombre
        try {
            Catalogo nuevo = new Catalogo();
            nuevo.setCatalogoSup(null); // Explicitamente sin superior para un nuevo raíz
            nuevo.setExpandible("S"); // Los nuevos padres suelen ser expandibles por defecto

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogo(nuevo);
            controller.setCatalogoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Catálogo Raíz");
            stage.showAndWait();
            // El refrescar se llama desde RegistroCatalogoController al guardar.
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar el formulario para nuevo catálogo raíz: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Crea un nuevo sub-registro (hijo) bajo un catálogo padre específico.
     * Solo se permite si el catálogo padre es "expandible".
     * Este método consolida la lógica que tenías para crear hijos o hermanos.
     *
     * @param padre El catálogo bajo el cual se creará el nuevo sub-registro.
     */
    private void crearSubRegistro(Catalogo padre) {
        // Validación: Solo se puede crear un sub-registro si el padre es expandible.
        if (!"S".equals(padre.getExpandible())) {
            new Alert(Alert.AlertType.WARNING).showAndWait();
            return;
        }

        try {
            Catalogo nuevoHijo = new Catalogo();
            nuevoHijo.setCatalogoSup(padre.getCatalogoId()); // El ID del padre se asigna al nuevo hijo
            nuevoHijo.setExpandible("S"); // Los nuevos sub-registros suelen ser expandibles por defecto

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/registro_catalogo.fxml"));
            AnchorPane pane = loader.load();

            RegistroCatalogoController controller = loader.getController();
            controller.setCatalogo(nuevoHijo); // Pasa el objeto hijo pre-configurado
            controller.setCatalogoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Crear Nuevo Sub-Registro para: " + padre.getDescripcion());
            stage.showAndWait();
            // El refrescar se llama desde RegistroCatalogoController al guardar.
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar el formulario de sub-registro: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Elimina un catálogo y todos sus sub-registros (hijos) después de la confirmación del usuario.
     * @param catalogoAEliminar El objeto Catalogo a eliminar.
     */
    private void eliminarCatalogo(Catalogo catalogoAEliminar) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("Eliminar Catálogo y sus Hijos");
        alert.setContentText("¿Está seguro de que desea eliminar el catálogo '" + catalogoAEliminar.getDescripcion() + "' y todos sus sub-registros anidados?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Asumiendo que catalogoDAO.eliminar() se encarga de la eliminación recursiva.
                    catalogoDAO.eliminar(catalogoAEliminar.getCatalogoId());
                    refrescar(); // Actualiza la tabla después de la eliminación
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Error al eliminar el catálogo: " + e.getMessage()).showAndWait();
                }
            }
        });
    }

    /**
     * Refresca los datos en el TreeTableView volviendo a cargar desde el DAO.
     */
    public void refrescar() {
        cargarDatos();
    }
}