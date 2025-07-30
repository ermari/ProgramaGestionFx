package Home.User.Controlador;

import CatalogoGestion.Empresas.Modelo.Sucursal;
import Constantes.constantes;
import Home.HomeController;
import Home.User.Modelo.Usuario;
import Home.User.Modelo.UsuarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.MensajeUtil;
import util.UtilControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class UsuariosController implements Initializable {

    @FXML private TextField filterField;
    @FXML private ComboBox<String> filterCombo;
    @FXML private TableView<Usuario> tableview;
    @FXML private TableColumn<Usuario, String> nombreUsuario;
    @FXML private TableColumn<Usuario, String> email;
    @FXML private TableColumn<Usuario, String> usuario;
    @FXML private TableColumn<Usuario, String> password;
    @FXML private TableColumn<Usuario, String> colSucursal;
    @FXML private TableColumn<Usuario, Void> actionsColumn;
    @FXML private AnchorPane rootEmpleado;
    @FXML private Pagination pagination;

    private static final int ITEMS_PER_PAGE = 15;
    private HomeController homeController;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObservableList<Usuario> masterData = FXCollections.observableArrayList();

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Map<String, String> filtroMap = new LinkedHashMap<>();
        filtroMap.put("Todos Los Usuarios", "All");
        filtroMap.put("Nombre", "nombreUsuario");
        filtroMap.put("Correo", "email");
        filtroMap.put("Sucursal", "sucursal");
        filtroMap.put("Usuario", "usuario");

        filterCombo.setItems(FXCollections.observableArrayList(filtroMap.keySet()));
        filterCombo.getSelectionModel().select("Todos Los Usuarios");

        colSucursal.setCellValueFactory(cellData -> {
            Sucursal suc = cellData.getValue().getSucursal();
            return new SimpleStringProperty(suc != null ? suc.getNombre() : "");
        });

        nombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        usuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        actionsColumn.setStyle("-fx-alignment: CENTER;");
        addActionButtonsToTable();
        cargarUsuario(null, "All");
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Usuario, Void>, TableCell<Usuario, Void>> cellFactory = param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Button menuButton = new Button();
                    ContextMenu contextMenu = new ContextMenu();

                    ImageView kebabIcon = new ImageView(safeLoadImage("/resources/images/menu8.png"));
                    kebabIcon.setFitWidth(18);
                    kebabIcon.setFitHeight(18);
                    menuButton.setGraphic(kebabIcon);
                    menuButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                    MenuItem editItem = new MenuItem("Modificar");
                    editItem.setGraphic(resizeIcon("/resources/icons/edit.png"));
                    editItem.setOnAction(e -> {
                        Usuario usuario = getTableView().getItems().get(getIndex());
                        if (usuario != null) {
                            try {
                                modificarUsuario(usuario);
                            } catch (IOException ex) {
                                UtilControllers.mostrarError("Error al cargar el formulario de modificación.", ex);
                            }
                        }
                    });

                    MenuItem deleteItem = new MenuItem("Eliminar");
                    deleteItem.setGraphic(resizeIcon("/resources/icons/delete.png"));
                    deleteItem.setOnAction(e -> {
                        Usuario usuario = getTableView().getItems().get(getIndex());
                        if (usuario != null) {
                            eliminarUsuario(usuario);
                        }
                    });

                    contextMenu.getItems().addAll(editItem, deleteItem);
                    menuButton.setOnAction(event -> {
                        if (!isEmpty()) contextMenu.show(menuButton, Side.BOTTOM, 0, 0);
                    });

                    setGraphic(menuButton);
                    setText(null);
                }
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    private Image safeLoadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            return new Image("https://via.placeholder.com/18.png");
        }
    }

    private ImageView resizeIcon(String path) {
        ImageView icon = new ImageView(safeLoadImage(path));
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        return icon;
    }

    @FXML
    private void refrescarTabla() {
        cargarUsuario(filterField.getText(), filterCombo.getValue());
        pagination.setCurrentPageIndex(0);
        filterCombo.getSelectionModel().select("Todos Los Usuarios");
    }

    public void cargarUsuario(String searchTerm, String filter) {
        try {
            if (filter == null || filter.equals("Todos Los Usuarios")) {
                masterData.setAll(usuarioDAO.listarTodos());
            } else {
                masterData.setAll(usuarioDAO.listarUsuarioFiltro(searchTerm, filter));
            }
            updatePagination();
            if (pagination.getCurrentPageIndex() >= pagination.getPageCount()) {
                pagination.setCurrentPageIndex(pagination.getPageCount() - 1);
            }
            tableview.setItems(masterData);
        } catch (SQLException e) {
            e.printStackTrace();
            UtilControllers.mostrarError("Error de base de datos al cargar Usuarios.", e);
            masterData.clear();
            updatePagination();
        }
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, masterData.size());
        if (fromIndex > toIndex || fromIndex < 0) {
            tableview.setItems(FXCollections.observableArrayList());
            return new VBox();
        }
        ObservableList<Usuario> pageItems = FXCollections.observableArrayList(masterData.subList(fromIndex, toIndex));
        tableview.setItems(pageItems);
        return new VBox();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) masterData.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
    }

    @FXML
    private void salir(ActionEvent event) {
        if (homeController != null) {
            homeController.setForm("Dashboard.fxml");
        }
    }

    @FXML
    private void agregarUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/User/Vista/RegistrarUsuario.fxml"));
            Parent root = loader.load();
            RegistrarUsuarioController controller = loader.getController();
            controller.setUsuario(new Usuario());
            controller.setUsuarioController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Agregar Empleado");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarUsuario(filterField.getText(), filterCombo.getValue());

        } catch (IOException e) {
            e.printStackTrace();
            UtilControllers.mostrarError("No se pudo cargar el formulario para agregar usuario.", e);
        }
    }

    private void modificarUsuario(Usuario usuario) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/User/Vista/RegistrarUsuario.fxml"));
        Parent root = loader.load();

        RegistrarUsuarioController controller = loader.getController();
        controller.setUsuario(usuario);
        controller.setUsuarioController(this);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Editar Usuario: " + usuario.getNombreUsuario());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        cargarUsuario(filterField.getText(), filterCombo.getValue());
    }

    private void eliminarUsuario(Usuario usuario) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("Eliminar");
        confirmAlert.setContentText("¿Está seguro de que desea eliminar a " + usuario.getNombreUsuario() + " (ID: " + usuario.getUsuarioId() + ")?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                usuarioDAO.eliminar(usuario);
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", constantes.MENSAJE_BORRADO, null);
                cargarUsuario(filterField.getText(), filterCombo.getValue());
            }
        });
    }
}
