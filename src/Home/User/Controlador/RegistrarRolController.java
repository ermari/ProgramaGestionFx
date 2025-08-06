package Home.User.Controlador;

import Home.User.Modelo.Permiso;
import Home.User.Modelo.Rol;
import Home.User.Modelo.RolDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RegistrarRolController {

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private ListView<CheckBox> listViewPermisos;

    @FXML
    private ListView<CheckBox> listViewRoles;
    private List<Rol> rolesDisponibles; // Lista real
    private Map<Integer, CheckBox> mapaCheckRoles = new HashMap<>();

    private final RolDAO rolDAO = new RolDAO();
    private Rol rol;

    @FXML
    public void initialize() {



        cargarPermisos();
    }

    private void cargarPermisos() {
        try {
            List<Permiso> permisos = rolDAO.listarPermisos(); // Método para traer todos los permisos disponibles
            ObservableList<CheckBox> checkboxes = FXCollections.observableArrayList();

            for (Permiso p : permisos) {
                CheckBox cb = new CheckBox(p.getNombre() + " - " + p.getDescripcion());
                cb.setUserData(p); // Guarda el permiso asociado
                checkboxes.add(cb);
            }

            listViewPermisos.setItems(checkboxes);

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los permisos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void setRol(Rol rol) {
        this.rol = rol;

        txtNombre.setText(String.valueOf(rol.getNombre()));
        txtDescripcion.setText(rol.getDescripcion());

        try {
            if (rol.getRolId() > 0) {
                List<Permiso> asignados = rolDAO.obtenerPermisosDelRol(rol.getRolId());

                for (CheckBox cb : listViewPermisos.getItems()) {
                    Permiso permiso = (Permiso) cb.getUserData();
                    if (asignados.stream().anyMatch(p -> p.getPermisoId() == permiso.getPermisoId())) {
                        cb.setSelected(true);
                    }
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los permisos asignados.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarRol() {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();

        if (nombre.isBlank()) {
            mostrarAlerta("Validación", "El nombre no puede estar vacío.", Alert.AlertType.WARNING);
            return;
        }

        List<Permiso> permisosSeleccionados = new ArrayList<>();
        for (CheckBox cb : listViewPermisos.getItems()) {
            if (cb.isSelected()) {
                permisosSeleccionados.add((Permiso) cb.getUserData());
            }
        }

        try {
            if (rol == null || rol.getRolId() == 0) {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombre(nombre);
                nuevoRol.setDescripcion(descripcion);
                rolDAO.insertarConPermisos(nuevoRol, permisosSeleccionados);
            } else {
                rol.setNombre(nombre);
                rol.setDescripcion(descripcion);
                rolDAO.actualizarConPermisos(rol, permisosSeleccionados);
            }

            cerrarVentana();

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo guardar el rol.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
