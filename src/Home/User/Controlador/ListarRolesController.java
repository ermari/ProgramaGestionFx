package Home.User.Controlador;

import Home.HomeController;
import Home.User.Modelo.Rol;
import Home.User.Modelo.RolDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ListarRolesController {

    @FXML private TableView<Rol> tableRoles;
    @FXML private TableColumn<Rol, String> colNombre;
    @FXML private TableColumn<Rol, String> colDescripcion;
    @FXML private TableColumn<Rol, Void> colAcciones;
    @FXML private TextField txtBuscarRol;
    @FXML private Button btnAgregarRol;

    private final RolDAO rolDAO = new RolDAO();
    private ObservableList<Rol> listaRoles;

    //------------------------------------------------------------------------
    private HomeController homeController;
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
    //-------------------------------------------------------------------------
    @FXML
    private void salir(ActionEvent event) {
        if (homeController != null) {
            homeController.setForm("Dashboard.fxml");
        }
    }
//-----------------------------------------------------------------------------


    @FXML
    public void initialize() {
        configurarTabla();
        cargarRoles();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox pane = new HBox(10, btnEditar, btnEliminar);

            {
                btnEditar.setTooltip(new Tooltip("Editar rol"));
                btnEliminar.setTooltip(new Tooltip("Eliminar rol"));

                btnEditar.setOnAction(event -> {
                    Rol rol = getTableView().getItems().get(getIndex());
                    editarRol(rol);
                });

                btnEliminar.setOnAction(event -> {
                    Rol rol = getTableView().getItems().get(getIndex());
                    eliminarRol(rol);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void cargarRoles() {
        try {
            listaRoles = FXCollections.observableArrayList(rolDAO.listarRoles());
            tableRoles.setItems(listaRoles);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la lista de roles.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void abrirFormularioAgregar(ActionEvent event) {
        abrirFormularioRol(null);
    }

    @FXML
    private void buscarRol(ActionEvent event) {
        String filtro = txtBuscarRol.getText().trim();
        try {
            listaRoles = FXCollections.observableArrayList(rolDAO.listarRoles());

            if (!filtro.isEmpty()) {
                listaRoles.removeIf(rol ->
                        !rol.getNombre().toLowerCase().contains(filtro.toLowerCase())
                );
            }

            tableRoles.setItems(listaRoles);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo realizar la búsqueda.", Alert.AlertType.ERROR);
        }
    }

    private void editarRol(Rol rol) {
        abrirFormularioRol(rol);
    }

    private void eliminarRol(Rol rol) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Está seguro que desea eliminar el rol \"" + rol.getNombre() + "\"?");

        Optional<ButtonType> result = alerta.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                rolDAO.eliminar(rol.getRolId());
                listaRoles.remove(rol);
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo eliminar el rol.", Alert.AlertType.ERROR);
            }
        }
    }

    private void abrirFormularioRol(Rol rol) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/User/Vista/RegistrarRol.fxml"));
            Parent root = loader.load();

            RegistrarRolController controller = loader.getController();
            if (rol != null) {
                controller.setRol(rol); // modo edición
            }

            Stage stage = new Stage();
            stage.setTitle(rol == null ? "Registrar Rol" : "Editar Rol");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarRoles(); // refresca después de cerrar
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
