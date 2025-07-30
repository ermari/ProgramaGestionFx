package CatalogoGestion.MasterCatalogo.Controladores;


import CatalogoGestion.MasterCatalogo.Modelo.MasterCatalogo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MasterCatalogoRegistroController {

    @FXML private TextField txtMasterId;
    @FXML private TextField txtMasterCodigo;
    @FXML private TextField txtMasterNombre;
    @FXML private TextArea txtMasterDescripcion;

    private Stage dialogStage;
    private MasterCatalogo masterCatalogo;
    private boolean okClicked = false; // Para saber si se guardó o se canceló

    /**
     * Establece el escenario para este diálogo.
     * @param dialogStage El escenario de diálogo.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Establece el maestro de catálogo a editar en el formulario.
     * Si el maestro es nulo, se entiende que es un nuevo registro.
     * @param masterCatalogo El objeto MasterCatalogo o null para uno nuevo.
     */
    public void setMasterCatalogo(MasterCatalogo masterCatalogo) {
        this.masterCatalogo = masterCatalogo;

        if (masterCatalogo != null) {
            txtMasterId.setText(String.valueOf(masterCatalogo.getMasterCatalogoId()));
            txtMasterCodigo.setText(masterCatalogo.getCodigo());
            txtMasterNombre.setText(masterCatalogo.getNombre());
            txtMasterDescripcion.setText(masterCatalogo.getDescripcion());
        } else {
            // Limpiar campos para un nuevo registro
            txtMasterId.setText("");
            txtMasterCodigo.setText("");
            txtMasterNombre.setText("");
            txtMasterDescripcion.setText("");
        }
    }

    /**
     * Retorna si el botón "Guardar" fue clicado.
     * @return true si "Guardar" fue clicado, false en caso contrario.
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Maneja el clic en el botón "Guardar".
     * Valida los datos y guarda (inserta o actualiza) el maestro en la base de datos.
     */
    @FXML
    private void handleGuardar() {
        if (isInputValid()) {
            String codigo = txtMasterCodigo.getText();
            String nombre = txtMasterNombre.getText();
            String descripcion = txtMasterDescripcion.getText();

            try {



                if (masterCatalogo == null || masterCatalogo.getMasterCatalogoId() == 0) { // Nuevo registro
                    String sql = "INSERT INTO MASTER_CATALOGO (CODIGO, NOMBRE, DESCRIPCION) VALUES (?, ?, ?)";

                    PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, codigo);
                    pstmt.setString(2, nombre);
                    pstmt.setString(3, descripcion);
                    pstmt.executeUpdate();

                    // Obtener el ID generado automáticamente
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        long newId = rs.getLong(1);
                        masterCatalogo = new MasterCatalogo(newId, codigo, nombre, descripcion); // Crear el objeto
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Maestro Guardado", "Nuevo maestro creado correctamente.");

                } else { // Actualizar registro existente
                    String sql = "UPDATE MASTER_CATALOGO SET CODIGO = ?, NOMBRE = ?, DESCRIPCION = ? WHERE MASTER_CATALOGO_ID = ?";
                    PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
                    pstmt.setString(1, codigo);
                    pstmt.setString(2, nombre);
                    pstmt.setString(3, descripcion);
                    pstmt.setLong(4, masterCatalogo.getMasterCatalogoId());
                    pstmt.executeUpdate();

                    // Actualizar el objeto masterCatalogo con los nuevos datos
                    masterCatalogo.setCodigo(codigo);
                    masterCatalogo.setNombre(nombre);
                    masterCatalogo.setDescripcion(descripcion);
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Maestro Actualizado", "Maestro actualizado correctamente.");
                }
                okClicked = true;
                dialogStage.close(); // Cerrar el diálogo
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error de BD", "No se pudo guardar el Maestro", e.getMessage());
            }
        }
    }

    /**
     * Maneja el clic en el botón "Cancelar".
     * Cierra el diálogo sin guardar.
     */
    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    /**
     * Valida la entrada del usuario en los campos de texto.
     * @return true si la entrada es válida, false en caso contrario.
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (txtMasterCodigo.getText() == null || txtMasterCodigo.getText().trim().isEmpty()) {
            errorMessage += "El campo 'Código' no puede estar vacío.\n";
        }
        if (txtMasterNombre.getText() == null || txtMasterNombre.getText().trim().isEmpty()) {
            errorMessage += "El campo 'Nombre' no puede estar vacío.\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "Por favor, corrija los campos inválidos", errorMessage);
            return false;
        }
    }

    /**
     * Muestra una alerta en la interfaz.
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}