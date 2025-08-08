// Archivo: src/main/java/com/yourcompany/yourapp/controller/MasterCatalogoListaController.java
package CatalogoGestion.MasterCatalogo.Controladores;


import CatalogoGestion.MasterCatalogo.Modelo.MasterCatalogo;
import Home.HomeController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

// Clase de utilidad para la conexión a la base de datos (tendrás que crearla)
// Suponiendo que tienes una clase así

public class MasterCatalogoListaController {

    @FXML private TableView<MasterCatalogo> masterTable;
    @FXML private TableColumn<MasterCatalogo, Long> colMasterId;
    @FXML private TableColumn<MasterCatalogo, String> colMasterCodigo;
    @FXML private TableColumn<MasterCatalogo, String> colMasterNombre;
    @FXML private TableColumn<MasterCatalogo, String> colMasterDescripcion;

    private ObservableList<MasterCatalogo> masterData = FXCollections.observableArrayList();

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
        // Configurar las columnas de la tabla
        colMasterId.setCellValueFactory(new PropertyValueFactory<>("masterCatalogoId"));
        colMasterCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colMasterNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMasterDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        masterTable.setItems(masterData);

        // Cargar los datos de los maestros al inicializar el controlador
        loadMasters();
    }

    /**
     * Carga todos los MasterCatalogo de la base de datos en la tabla.
     */
    private void loadMasters() {
        masterData.clear();
        String sql = "SELECT MASTER_CATALOGO_ID, CODIGO, NOMBRE, DESCRIPCION FROM MASTER_CATALOGO";

        try (// Obtener conexión (implementa esta clase/método)

             PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                masterData.add(new MasterCatalogo(
                        rs.getLong("MASTER_CATALOGO_ID"),
                        rs.getString("CODIGO"),
                        rs.getString("NOMBRE"),
                        rs.getString("DESCRIPCION")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de BD", "No se pudieron cargar los Maestros", e.getMessage());
        }
    }

    /**
     * Maneja el clic en el botón "Nuevo Maestro".
     * Abre un formulario de registro vacío para un nuevo maestro.
     */
    @FXML
    private void handleNuevoMaster() {
        openMasterRegistroForm(null); // Pasa null para indicar nuevo registro
    }

    /**
     * Maneja el clic en el botón "Editar Maestro".
     * Abre el formulario de registro con los datos del maestro seleccionado.
     */
    @FXML
    private void handleEditarMaster() {
        MasterCatalogo selectedMaster = masterTable.getSelectionModel().getSelectedItem();
        if (selectedMaster != null) {
            openMasterRegistroForm(selectedMaster);
        } else {
            showAlert(Alert.AlertType.WARNING, "Ninguna Selección", "No hay Maestro Seleccionado", "Por favor, seleccione un maestro en la tabla para editar.");
        }
    }

    /**
     * Abre el formulario de registro/edición de un MasterCatalogo.
     * @param master El objeto MasterCatalogo a editar, o null si es un nuevo registro.
     */
    private void openMasterRegistroForm(MasterCatalogo master) {
        try {
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/Comprobantes/Vista/busqueda_catalogo.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/MasterCatalogo/vistas/master_catalogo_registro.fxml"));
            Parent root = loader.load();

            MasterCatalogoRegistroController controller = loader.getController();
            controller.setMasterCatalogo(master); // Pasa el objeto maestro al controlador del formulario de registro

            Stage stage = new Stage();
            controller.setDialogStage(stage); // Pasa la referencia del Stage al controlador del formulario
            stage.setTitle(master == null ? "Nuevo Maestro de Catálogo" : "Editar Maestro de Catálogo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana padre
            stage.showAndWait(); // Espera a que se cierre el formulario de registro

            loadMasters(); // Recarga los maestros después de cerrar el formulario
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el formulario de Maestro", e.getMessage());
        }
    }

    /**
     * Maneja el clic en el botón "Eliminar Maestro".
     * Elimina el maestro seleccionado de la base de datos.
     */
    @FXML
    private void handleEliminarMaster() {
        MasterCatalogo selectedMaster = masterTable.getSelectionModel().getSelectedItem();
        if (selectedMaster != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("Eliminar Maestro de Catálogo");
            alert.setContentText("¿Está seguro de que desea eliminar el maestro '" + selectedMaster.getNombre() + "'?\n" +
                    "¡Esto también eliminará todos sus detalles asociados!");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "DELETE FROM MASTER_CATALOGO WHERE MASTER_CATALOGO_ID = ?";
                try
                {  PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);

                    pstmt.setLong(1, selectedMaster.getMasterCatalogoId());
                    pstmt.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Maestro Eliminado", "Maestro eliminado correctamente.");
                    loadMasters(); // Recargar la tabla después de eliminar
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error de BD", "No se pudo eliminar el Maestro", e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Ninguna Selección", "No hay Maestro Seleccionado", "Por favor, seleccione un maestro en la tabla para eliminar.");
        }
    }

    /**
     * Maneja el clic en el botón "Ver Detalles".
     * Abre la pantalla de gestión de detalles para el maestro seleccionado.
     */
    @FXML
    private void handleVerDetalles() {
        MasterCatalogo selectedMaster = masterTable.getSelectionModel().getSelectedItem();
        if (selectedMaster != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/MasterCatalogo/vistas/detalle_catalogo_gestion.fxml"));
              //  FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/Vistas/detalle_catalogo_gestion.fxml"));
                Parent root = loader.load();

                DetalleCatalogoGestionController controller = loader.getController();
                // Pasa el maestro seleccionado al controlador de gestión de detalles
                controller.setMasterCatalogo(selectedMaster);

                Stage stage = new Stage();
                stage.setTitle("Gestión de Detalles para: " + selectedMaster.getNombre());
                // Agregar ícono al stage
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/link_icon-32.png")));



                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL); // Opcional: Bloquea la ventana padre
                stage.showAndWait();

                // Si necesitas hacer algo al regresar de la pantalla de detalles, hazlo aquí
                // Por ejemplo, volver a cargar los maestros si se permitió algún cambio que los afecte.

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir la gestión de Detalles", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Ninguna Selección", "No hay Maestro Seleccionado", "Por favor, seleccione un maestro para ver sus detalles.");
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