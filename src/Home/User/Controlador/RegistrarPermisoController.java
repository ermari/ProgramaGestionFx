package Home.User.Controlador;

import Home.HomeController;
import Home.User.Modelo.Permiso;
import Home.User.Modelo.PermisoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistrarPermisoController {

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;

    private Permiso permiso;
    private final PermisoDAO permisoDAO = new PermisoDAO();


    // Método para recibir el permiso en modo edición (puede ser null para nuevo)
    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
        if (permiso != null) {
            txtNombre.setText(permiso.getNombre());
            txtDescripcion.setText(permiso.getDescripcion());
        }
    }

    @FXML
    private void guardarPermiso() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Validación", "El nombre del permiso es obligatorio.");
            return;
        }

        try {
            if (permiso == null) {
                // Modo nuevo
                Permiso nuevoPermiso = new Permiso(nombre, descripcion);
                permisoDAO.insertar(nuevoPermiso);
            } else {
                // Modo edición
                permiso.setNombre(nombre);
                permiso.setDescripcion(descripcion);
                permisoDAO.actualizar(permiso);
            }

            cerrarVentana();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al guardar el permiso.");
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
