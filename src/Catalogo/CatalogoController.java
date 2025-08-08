package Catalogo;

import Home.HomeController;
import javafx.event.ActionEvent;
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
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;         // Necesario para Initializable
import java.sql.Connection;
import java.sql.DriverManager;
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
    private TreeTableColumn<Catalogo, String> colCodigoPadre;
    @FXML
    private TreeTableColumn<Catalogo, Void> colAccion;

    @FXML private TextField filterField;

    // Columna de acción de tipo Void
    private CatalogoDAO catalogoDAO = new CatalogoDAO();
    private HomeController homeController;


    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @Override // Sobreescribir el método initialize de Initializable
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar las factorías de valor para las columnas
        colCodigo.setCellValueFactory(new TreeItemPropertyValueFactory<>("codigo"));
        colValor.setCellValueFactory(new TreeItemPropertyValueFactory<>("valor"));
        colCodigoPadre.setCellValueFactory(new TreeItemPropertyValueFactory<>("codigoPadre"));
        colDescripcion.setCellValueFactory(new TreeItemPropertyValueFactory<>("descripcion"));
        colExpandible.setCellValueFactory(new TreeItemPropertyValueFactory<>("expandible"));
        // 2. Ajustar el ancho de las columnas de forma proporcional al ancho de la tabla
        treeTableCatalogos.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue();
            // Resta un pequeño valor para evitar problemas con la barra de desplazamiento
            tableWidth -= 2;

            // Ajusta estos porcentajes según tus necesidades (la suma debe ser 1.0 para 100%)
            colCodigo.setPrefWidth(tableWidth * 0.15); // 15%
            colValor.setPrefWidth(tableWidth * 0.20); // 25%
            colDescripcion.setPrefWidth(tableWidth * .20); // 30%
            colExpandible.setPrefWidth(tableWidth * 0.15); // 10%
            colCodigoPadre.setPrefWidth(tableWidth * 0.10); // 15%
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
        //refrescar(); // Se debe llamar aquí para que la tabla se llene al iniciar

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                cargarEnTreeTable(newValue.trim().toLowerCase());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            cargarEnTreeTable("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cargarEnTreeTable(String filtro) throws SQLException {
        treeTableCatalogos.setRoot(null);

        TreeItem<Catalogo> root = new TreeItem<>(new Catalogo(0, null, "", "Raíz",", ", 0, false));
        Map<Integer, TreeItem<Catalogo>> mapa = new HashMap<>();
        mapa.put(0, root);

        List<Catalogo> listaCatalogo = catalogoDAO.obtenerTodos();

        for (Catalogo item : listaCatalogo) {
            boolean coincideFiltro = filtro == null || filtro.isEmpty()
                    || item.getCodigo().toLowerCase().contains(filtro)
                    || item.getValor().toLowerCase().contains(filtro)
                   ;

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
     // Configura la columna de acción para que muestre botones de Editar, Eliminar y Crear Sub-Registro.
    private void setupActionColumn() {
        Callback<TreeTableColumn<Catalogo, Void>, TreeTableCell<Catalogo, Void>> cellFactory =
                new Callback<TreeTableColumn<Catalogo, Void>, TreeTableCell<Catalogo, Void>>() {
                    @Override
                    public TreeTableCell<Catalogo, Void> call(final TreeTableColumn<Catalogo, Void> param) {
                        final TreeTableCell<Catalogo, Void> cell = new TreeTableCell<Catalogo, Void>() {

                            // Los botones se pueden crear aquí o dentro de updateItem.
                            // Para evitar recrearlos constantemente, los definimos aquí como miembros de la celda.
                            private final Button btnEditar = createButton("/resources/images/edit.png", "Editar");
                            private final Button btnEliminar = createButton("/resources/images/exit.png", "Eliminar");
                            private final Button btnCrearSubRegistro = createButton("/resources/images/Tree.png", "Sub-Registro");

                            {
                                // Asignar las acciones (eventos) a cada botón UNA VEZ
                                btnEditar.setOnAction(event -> {
                                    // Asegúrate de que el item no sea null para evitar NPE si la celda se recicla inesperadamente
                                    Catalogo data = getTreeTableRow().getItem();
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
                                        crearSubRegistro(data);
                                    }
                                });
                            }

                            /**
                             * Método auxiliar para crear un botón con una imagen y un tooltip.
                             */
                            private Button createButton(String imagePath, String tooltipText) {
                                Button button = new Button();
                                try {
                                    ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
                                    imageView.setFitHeight(16);
                                    imageView.setFitWidth(16);
                                    button.setGraphic(imageView);
                                } catch (Exception e) {
                                    System.err.println("No se pudo cargar la imagen: " + imagePath + ". Usando texto de respaldo.");
                                    button.setText(tooltipText.substring(0, Math.min(tooltipText.length(), 4)));
                                }
                                button.setTooltip(new Tooltip(tooltipText));
                                button.getStyleClass().add("action-button");
                                return button;
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty); // Llama al método de la clase padre

                                if (empty) {
                                    // Si la celda está vacía (no hay datos de Catalogo para esta fila),
                                    // asegúrate de que no se muestre ningún gráfico.
                                    setGraphic(null);
                                } else {
                                    // Si la celda contiene datos (no está vacía),
                                    // obtenemos el objeto Catalogo asociado a esta fila.
                                    Catalogo currentCatalogo = getTreeTableRow().getItem();

                                    // Es crucial que el 'currentCatalogo' no sea null aquí si 'empty' es false.
                                    // Si por alguna razón lo es, podríamos mostrar un HBox vacío o manejar el caso.
                                    if (currentCatalogo != null) {
                                        // Creamos un nuevo HBox en CADA LLAMADA a updateItem (cuando no es empty).
                                        // Esto asegura que siempre empezamos con un contenedor limpio para los botones.
                                        HBox hbox = new HBox(5); // Contenedor horizontal para los botones, con 5px de espacio
                                        hbox.setAlignment(Pos.CENTER); // Centrar los botones dentro de la celda

                                        // Añadir siempre los botones Editar y Eliminar
                                        hbox.getChildren().addAll(btnEditar, btnEliminar);

                                        // Añadir el botón "Crear Sub-Registro" solo si el catálogo es expandible ("S")
                                        if ("S".equals(currentCatalogo.getExpandible())) {
                                            hbox.getChildren().add(btnCrearSubRegistro);
                                        }

                                        // Establecer el HBox recién configurado como el gráfico de la celda.
                                        setGraphic(hbox);
                                    } else {
                                        // En caso de que currentCatalogo sea null inesperadamente cuando empty es false
                                        setGraphic(null);
                                    }
                                }
                            }
                        };
                        return cell; // ¡IMPORTANTE! Retorna la celda creada
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
            List<Catalogo> allCatalogos = catalogoDAO.obtenerTodos();
            Map<Integer, TreeItem<Catalogo>> itemMap = new HashMap<>();
            for (Catalogo cat : allCatalogos) {
                itemMap.put(cat.getCatalogoId(), new TreeItem<>(cat));
            }

            TreeItem<Catalogo> rootItem = new TreeItem<>(new Catalogo(0, "ROOT", "Root", "Catálogo Raíz", 0, null, "S"));
            rootItem.setExpanded(true);

            for (Catalogo catalogo : allCatalogos) {
                TreeItem<Catalogo> item = itemMap.get(catalogo.getCatalogoId());
                if (catalogo.getCatalogoSup() == null || catalogo.getCatalogoSup() == 0) {
                    rootItem.getChildren().add(item);
                } else {
                    TreeItem<Catalogo> parentItem = itemMap.get(catalogo.getCatalogoSup());
                    if (parentItem != null) {
                        parentItem.getChildren().add(item);
                    } else {
                        rootItem.getChildren().add(item);
                        System.err.println("Advertencia: Catálogo con ID " + catalogo.getCatalogoId() + " tiene un superior inválido (" + catalogo.getCatalogoSup() + "). Añadido a la raíz.");
                    }
                }
            }

            // Ordenar los hijos de cada nodo por su propiedad 'orden'
            rootItem.getChildren().forEach(this::sortTreeItemChildren);

            // --- ¡AQUÍ ESTÁ EL CAMBIO CLAVE! ---
            // Este bucle recorre CADA UNO de los elementos hijos directos de tu 'rootItem' ficticio.
            // Como 'rootItem' es tu nodo raíz oculto, sus hijos son los nodos de PRIMER NIVEL
            // que sí se muestran en tu TreeTable.
            for (TreeItem<Catalogo> nodoNivel1 : rootItem.getChildren()) {
                // Para cada uno de esos nodos de primer nivel, le decimos que se expanda.
                nodoNivel1.setExpanded(true);
            }
            // ------------------------------------

            treeTableCatalogos.setRoot(rootItem);
            treeTableCatalogos.setShowRoot(false); // Ocultar el nodo raíz ficticio
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar los datos del catálogo: " + e.getMessage()).showAndWait();
        }
    }
    // Método auxiliar recursivo para ordenar los hijos de un TreeItem por su propiedad 'orden'
    private void sortTreeItemChildren(TreeItem<Catalogo> item) {
        if (item != null && !item.getChildren().isEmpty()) {
            item.getChildren().sort((item1, item2) -> Integer.compare(item1.getValue().getCatalogoId() , item2.getValue().getCatalogoId()));
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

    @FXML
    private void btnGenerarReporte(ActionEvent event) {

        Connection conn = null;
        try {
            // 1️⃣ Configuración de conexión a MySQL
            String url = "jdbc:mysql://localhost:3306/datasoft";
            String user = "root";           // tu usuario
            String pass = "123465";    // tu contraseña

            // Conectar
            conn = DriverManager.getConnection(url, user, pass);

            // 2️⃣ Ruta al JRXML
            String jrxml = "src/Reportes/reporte_simple.jrxml";

            HashMap<String, Object> parameters = new HashMap<>();

            // 💡 Agregar imagen relativa
            String imagePath = getClass().getResource("/resources/images/Tree.png").toExternalForm();
            parameters.put("imagen1", imagePath);

            // 3️⃣ Compilar
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);

            // 5️⃣ Llenar el reporte usando la conexión real
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            // 6️⃣ Mostrar visor
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7️⃣ Cerrar conexión
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void salir(ActionEvent event) {
        if (homeController != null) {
            homeController.setForm("Dashboard.fxml");
        }
    }

}