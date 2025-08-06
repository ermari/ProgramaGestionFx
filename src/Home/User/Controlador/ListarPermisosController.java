package Home.User.Controlador;

import Home.HomeController;
import Home.User.Modelo.Permiso;
import Home.User.Modelo.PermisoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ListarPermisosController {

    @FXML private TableView<Permiso> tablePermisos;
    @FXML private TableColumn<Permiso, String> colNombre;
    @FXML private TableColumn<Permiso, String> colDescripcion;
    @FXML private TableColumn<Permiso, Void> colAcciones;
    @FXML private TextField txtBuscarPermiso;

    private final PermisoDAO permisoDAO = new PermisoDAO();

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox contenedor = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(e -> abrirFormularioPermiso(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> eliminarPermiso(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });

        cargarPermisos();
    }

    private void cargarPermisos() {
        try {
            ObservableList<Permiso> permisos = FXCollections.observableArrayList(permisoDAO.listarPermisos());
            tablePermisos.setItems(permisos);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar los permisos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void abrirFormularioAgregar() {
        abrirFormularioPermiso(null);
    }

    @FXML
    private void abrirFormularioPermiso(Permiso permiso) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/User/Vista/RegistrarPermiso.fxml"));
            Parent root = loader.load();

            RegistrarPermisoController controller = loader.getController();
            if (permiso != null) {
                controller.setPermiso(permiso); // modo edición
            }

            Stage stage = new Stage();
            stage.setTitle(permiso == null ? "Registrar Permiso" : "Editar Permiso");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // 🔁 IMPORTANTE: Refrescar tabla después de cerrar
            cargarPermisos();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario.", Alert.AlertType.ERROR);
        }
    }



    private void eliminarPermiso(Permiso permiso) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setContentText("¿Deseas eliminar este permiso?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                permisoDAO.eliminar(permiso.getPermisoId());
                cargarPermisos();
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo eliminar.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void buscarPermiso() {
        String termino = txtBuscarPermiso.getText().trim();
        if (termino.isEmpty()) {
            cargarPermisos();
            return;
        }

        try {
            ObservableList<Permiso> resultados = FXCollections.observableArrayList(
                    permisoDAO.buscarPorNombre(termino));
            tablePermisos.setItems(resultados);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar permisos.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {

    }

    public void setHomeController(HomeController controller) {
    }
}
