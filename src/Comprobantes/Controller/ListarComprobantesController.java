package Comprobantes.Controller;

import Comprobantes.modelo.Comprobante;
import Comprobantes.modelo.ComprobanteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ListarComprobantesController {

    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TextField numeroComprobanteBusquedaField;
    @FXML private TableView<Comprobante> comprobantesTable;
    @FXML private TableColumn<Comprobante, LocalDate> fechaColumn;
    @FXML private TableColumn<Comprobante, String> numeroColumn;
    @FXML private TableColumn<Comprobante, String> conceptoColumn;
    @FXML private TableColumn<Comprobante, BigDecimal> totalDebitosColumn;
    @FXML private TableColumn<Comprobante, BigDecimal> totalCreditosColumn;

    private ComprobanteDAO comprobanteDAO = new ComprobanteDAO();
    private ObservableList<Comprobante> listaComprobantes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        numeroColumn.setCellValueFactory(new PropertyValueFactory<>("numeroComprobante"));
        conceptoColumn.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        //totalDebitosColumn.setCellValueFactory(new PropertyValueFactory<>("totalDebitos"));
        //totalCreditosColumn.setCellValueFactory(new PropertyValueFactory<>("totalCreditos"));

        totalDebitosColumn.setCellValueFactory(cellData -> cellData.getValue().debitoProperty());
        totalCreditosColumn.setCellValueFactory(cellData -> cellData.getValue().creditoProperty());

        // Formatear las columnas de moneda a 2 decimales
        NumberFormat format = new DecimalFormat("#,##0.0000");
        totalDebitosColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(format.format(value));
                }
            }
        });

        totalCreditosColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(format.format(value));
                }
            }
        });




        // Cargar todos los comprobantes al inicio
        cargarComprobantes();

        comprobantesTable.setItems(listaComprobantes);


        // Opcional: Doble clic para editar
        comprobantesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && comprobantesTable.getSelectionModel().getSelectedItem() != null) {
                editarComprobante();
            }
        });
    }

    private void cargarComprobantes() {
        try {
            List<Comprobante> comprobantes = comprobanteDAO.obtenerComprobantes(
                    fechaInicioPicker.getValue(),
                    fechaFinPicker.getValue(),
                    numeroComprobanteBusquedaField.getText().trim()
            );
            listaComprobantes.setAll(comprobantes);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar los comprobantes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarComprobantes() {
        cargarComprobantes();
    }

    @FXML
    private void limpiarFiltros() {
        fechaInicioPicker.setValue(null);
        fechaFinPicker.setValue(null);
        numeroComprobanteBusquedaField.clear();
        cargarComprobantes(); // Recarga sin filtros
    }

    @FXML
    private void editarComprobante() {
        Comprobante seleccionado = comprobantesTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Comprobantes/Vista/RegistrarComprobante.fxml")); // Ruta a tu FXML de registro
                Parent root = loader.load();

                // Obtener el controlador de la ventana de registro
                RegistroComprobanteController registroController = loader.getController();

                // Pasar el comprobante seleccionado para editar
                registroController.cargarComprobanteParaEdicion(seleccionado); // Necesitarás crear este método en RegistroComprobanteController

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.setTitle("Editar Comprobante");
                stage.showAndWait();

                // Después de cerrar la ventana de edición, recargar la lista de comprobantes
                cargarComprobantes();

            } catch (IOException | SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir la ventana de edición: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Ningún Comprobante Seleccionado", "Por favor, selecciona un comprobante para editar.");
        }
    }

    @FXML
    private void eliminarComprobante() {
        Comprobante seleccionado = comprobantesTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmar Eliminación");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("¿Estás seguro de que quieres eliminar el comprobante " + seleccionado.getNumeroComprobante() + "? Esta acción no se puede deshacer.");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    comprobanteDAO.eliminarComprobante(seleccionado.getIdComprobante());
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Comprobante eliminado correctamente.");
                    cargarComprobantes(); // Recargar la tabla
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo eliminar el comprobante: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Ningún Comprobante Seleccionado", "Por favor, selecciona un comprobante para eliminar.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void AgregerComprobante() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Comprobantes/Vista/RegistrarComprobante.fxml"));
            Parent root = loader.load();

            RegistroComprobanteController registroController = loader.getController();

            // ✅ pasar callback
            registroController.setOnSaveCallback(this::cargarComprobantes);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Agregar Comprobante");
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir la ventana de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @FXML
    private void salir() {
        cargarComprobantes();
    }

}