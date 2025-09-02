package CatalogoGestion.MasterCatalogo.Controladores;


import CatalogoGestion.MasterCatalogo.Modelo.DetalleCatalogo;
import CatalogoGestion.MasterCatalogo.Modelo.MasterCatalogo;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;



public class DetalleCatalogoGestionController {

    @FXML private Label lblMasterInfo;
    @FXML private TableView<DetalleCatalogo> detalleTable;
    @FXML private TableColumn<DetalleCatalogo, Long> colDetalleId;
    @FXML private TableColumn<DetalleCatalogo, String> colDetalleCodigoItem;
    @FXML private TableColumn<DetalleCatalogo, String> colDetalleNombreItem;
    @FXML private TableColumn<DetalleCatalogo, String> colDetalleValorAdicional;

    private ObservableList<DetalleCatalogo> detalleData = FXCollections.observableArrayList();
    private MasterCatalogo masterCatalogo; // El maestro actualmente seleccionado

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla de detalles
        colDetalleId.setCellValueFactory(new PropertyValueFactory<>("detalleCatalogoId"));
        colDetalleCodigoItem.setCellValueFactory(new PropertyValueFactory<>("codigoItem"));
        colDetalleNombreItem.setCellValueFactory(new PropertyValueFactory<>("nombreItem"));
        colDetalleValorAdicional.setCellValueFactory(new PropertyValueFactory<>("valorAdicional"));

        detalleTable.setItems(detalleData);

        // Los detalles se cargarán una vez que se establezca el masterCatalogo
    }

    /**
     * Establece el MasterCatalogo para el cual se gestionarán los detalles.
     * Este método es llamado por el controlador de la lista de maestros.
     * @param masterCatalogo El objeto MasterCatalogo seleccionado.
     */
    public void setMasterCatalogo(MasterCatalogo masterCatalogo) {
        this.masterCatalogo = masterCatalogo;
        if (masterCatalogo != null) {
            lblMasterInfo.setText(masterCatalogo.getNombre() + " (Código: " + masterCatalogo.getCodigo() + ")");
            loadDetalles(masterCatalogo.getMasterCatalogoId()); // Cargar los detalles para este maestro
        } else {
            lblMasterInfo.setText("Ningún Maestro Seleccionado");
            detalleData.clear();
        }
    }

    /**
     * Carga los DetalleCatalogo asociados a un MasterCatalogo específico.
     * @param masterCatalogoId El ID del MasterCatalogo.
     */
    private void loadDetalles(long masterCatalogoId) {
        detalleData.clear();
        String sql = "SELECT DETALLE_CATALOGO_ID, CODIGO_ITEM, NOMBRE_ITEM, VALOR_ADICIONAL, MASTER_CATALOGO_ID FROM DETALLE_CATALOGO WHERE MASTER_CATALOGO_ID = ?";

        try
        {
            PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
            pstmt.setLong(1, masterCatalogoId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                detalleData.add(new DetalleCatalogo(
                        new SimpleLongProperty(rs.getLong("DETALLE_CATALOGO_ID")),
                        new SimpleLongProperty(rs.getLong("MASTER_CATALOGO_ID")),
                        new SimpleStringProperty(rs.getString("CODIGO_ITEM")),
                        new SimpleStringProperty(rs.getString("NOMBRE_ITEM")),
                        new SimpleStringProperty(rs.getString("VALOR_ADICIONAL"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de BD", "No se pudieron cargar los Detalles", e.getMessage());
        }
    }

    /**
     * Maneja el clic en el botón "Nuevo Detalle".
     * Abre un formulario de registro vacío para un nuevo detalle, vinculado al maestro actual.
     */
    @FXML
    private void handleNuevoDetalle() {
        if (masterCatalogo == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "Maestro No Seleccionado", "Por favor, regrese y seleccione un maestro antes de crear un nuevo detalle.");
            return;
        }
        openDetalleRegistroForm(null, masterCatalogo.getMasterCatalogoId());
    }

    /**
     * Maneja el clic en el botón "Editar Detalle".
     * Abre el formulario de registro con los datos del detalle seleccionado.
     */
    @FXML
    private void handleEditarDetalle() {
        DetalleCatalogo selectedDetalle = detalleTable.getSelectionModel().getSelectedItem();
        if (selectedDetalle != null && masterCatalogo != null) {
            openDetalleRegistroForm(selectedDetalle, masterCatalogo.getMasterCatalogoId());
        } else if (selectedDetalle == null) {
            showAlert(Alert.AlertType.WARNING, "Ninguna Selección", "No hay Detalle Seleccionado", "Por favor, seleccione un detalle en la tabla para editar.");
        } else if (masterCatalogo == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Maestro No Seleccionado", "No se pudo determinar el maestro asociado. Regrese y reintente.");
        }
    }

    /**
     * Abre el formulario de registro/edición de un DetalleCatalogo.
     * @param detalle El objeto DetalleCatalogo a editar, o null si es un nuevo registro.
     * @param masterId El ID del MasterCatalogo al que pertenece este detalle.
     */
    private void openDetalleRegistroForm(DetalleCatalogo detalle, long masterId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/MasterCatalogo/vistas/detalle_catalogo_registro.fxml"));
            Parent root = loader.load();

            DetalleCatalogoRegistroController controller = loader.getController();
            controller.setDetalleCatalogo(detalle, masterId); // Pasa el objeto detalle y el ID del maestro

            Stage stage = new Stage();
            controller.setDialogStage(stage); // Pasa la referencia del Stage al controlador del formulario de detalle
            stage.setTitle(detalle == null ? "Nuevo Detalle de Catálogo" : "Editar Detalle de Catálogo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recarga los detalles después de cerrar el formulario
            loadDetalles(masterId);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el formulario de Detalle", e.getMessage());
        }
    }

    /**
     * Maneja el clic en el botón "Eliminar Detalle".
     * Elimina el detalle seleccionado de la base de datos.
     */
    @FXML
    private void handleEliminarDetalle() {
        DetalleCatalogo selectedDetalle = detalleTable.getSelectionModel().getSelectedItem();
        if (selectedDetalle != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("Eliminar Detalle de Catálogo");
            alert.setContentText("¿Está seguro de que desea eliminar el detalle '" + selectedDetalle.getNombreItem() + "'?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "DELETE FROM DETALLE_CATALOGO WHERE DETALLE_CATALOGO_ID = ?";
                try
                {
                    PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
                    pstmt.setLong(1, selectedDetalle.getDetalleCatalogoId());
                    pstmt.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Detalle Eliminado", "Detalle eliminado correctamente.");
                    loadDetalles(masterCatalogo.getMasterCatalogoId()); // Recargar la tabla
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error de BD", "No se pudo eliminar el Detalle", e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Ninguna Selección", "No hay Detalle Seleccionado", "Por favor, seleccione un detalle en la tabla para eliminar.");
        }
    }

    /**
     * Maneja el clic en el botón "Volver a Maestros".
     * Cierra la ventana actual de gestión de detalles.
     */
    @FXML
    private void handleVolver() {
        // Obtener el escenario actual y cerrarlo
        Stage stage = (Stage) lblMasterInfo.getScene().getWindow();
        stage.close();
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