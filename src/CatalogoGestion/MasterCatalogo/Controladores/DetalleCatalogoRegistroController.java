package CatalogoGestion.MasterCatalogo.Controladores;

import CatalogoGestion.MasterCatalogo.Modelo.DetalleCatalogo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DetalleCatalogoRegistroController {

    @FXML private TextField txtDetalleId;
    @FXML private TextField txtDetalleCodigoItem;
    @FXML private TextField txtDetalleNombreItem;
    @FXML private TextField txtDetalleValorAdicional;

    private Stage dialogStage;
    private DetalleCatalogo detalleCatalogo;
    private long masterCatalogoId; // Para asociar el detalle al maestro correcto
    private boolean okClicked = false;

    /**
     * Establece el escenario para este diálogo.
     * @param dialogStage El escenario de diálogo.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Establece el detalle de catálogo a editar y el ID del maestro al que pertenece.
     * @param detalleCatalogo El objeto DetalleCatalogo o null para uno nuevo.
     * @param masterCatalogoId El ID del MasterCatalogo al que pertenece este detalle.
     */
    public void setDetalleCatalogo(DetalleCatalogo detalleCatalogo, long masterCatalogoId) {
        this.detalleCatalogo = detalleCatalogo;
        this.masterCatalogoId = masterCatalogoId; // Guarda el ID del maestro

        if (detalleCatalogo != null) {
            txtDetalleId.setText(String.valueOf(detalleCatalogo.getDetalleCatalogoId()));
            txtDetalleCodigoItem.setText(detalleCatalogo.getCodigoItem());
            txtDetalleNombreItem.setText(detalleCatalogo.getNombreItem());
            txtDetalleValorAdicional.setText(detalleCatalogo.getValorAdicional());
        } else {
            // Limpiar campos para un nuevo registro
            txtDetalleId.setText("");
            txtDetalleCodigoItem.setText("");
            txtDetalleNombreItem.setText("");
            txtDetalleValorAdicional.setText("");
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
     * Valida los datos y guarda (inserta o actualiza) el detalle en la base de datos.
     */
    @FXML
    private void handleGuardar() {
        if (isInputValid()) {
            String codigoItem = txtDetalleCodigoItem.getText();
            String nombreItem = txtDetalleNombreItem.getText();
            String valorAdicional = txtDetalleValorAdicional.getText();

            try
            {

                if (detalleCatalogo == null || detalleCatalogo.getDetalleCatalogoId() == 0) { // Nuevo registro
                    String sql = "INSERT INTO DETALLE_CATALOGO (MASTER_CATALOGO_ID, CODIGO_ITEM, NOMBRE_ITEM, VALOR_ADICIONAL) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                   // PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    pstmt.setLong(1, masterCatalogoId); // Usa el ID del maestro pasado
                    pstmt.setString(2, codigoItem);
                    pstmt.setString(3, nombreItem);
                    pstmt.setString(4, valorAdicional);
                    pstmt.executeUpdate();

                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        long newId = rs.getLong(1);

                       // detalleCatalogo = new DetalleCatalogo(newId, masterCatalogoId, codigoItem, nombreItem, valorAdicional);
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Detalle Guardado", "Nuevo detalle creado correctamente.");

                } else { // Actualizar registro existente
                    String sql = "UPDATE DETALLE_CATALOGO SET CODIGO_ITEM = ?, NOMBRE_ITEM = ?, VALOR_ADICIONAL = ? WHERE DETALLE_CATALOGO_ID = ?";
                    PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
                    pstmt.setString(1, codigoItem);
                    pstmt.setString(2, nombreItem);
                    pstmt.setString(3, valorAdicional);
                    pstmt.setLong(4, detalleCatalogo.getDetalleCatalogoId());
                    pstmt.executeUpdate();

                    detalleCatalogo.setCodigoItem(codigoItem);
                    detalleCatalogo.setNombreItem(nombreItem);
                    detalleCatalogo.setValorAdicional(valorAdicional);
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Detalle Actualizado", "Detalle actualizado correctamente.");
                }
                okClicked = true;
                dialogStage.close(); // Cerrar el diálogo
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error de BD", "No se pudo guardar el Detalle", e.getMessage());
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

        if (txtDetalleCodigoItem.getText() == null || txtDetalleCodigoItem.getText().trim().isEmpty()) {
            errorMessage += "El campo 'Código Item' no puede estar vacío.\n";
        }
        if (txtDetalleNombreItem.getText() == null || txtDetalleNombreItem.getText().trim().isEmpty()) {
            errorMessage += "El campo 'Nombre Item' no puede estar vacío.\n";
        }
        // Puedes agregar más validaciones aquí (ej. formato de valor adicional)

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