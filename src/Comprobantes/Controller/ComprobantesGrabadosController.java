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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ComprobantesGrabadosController {

    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TextField numeroComprobanteBusquedaField;
    @FXML private TableView<Comprobante> comprobantesTable;
    @FXML private TableColumn<Comprobante, LocalDate> fechaColumn;
    @FXML private TableColumn<Comprobante, String> numeroColumn;
    @FXML private TableColumn<Comprobante, String> conceptoColumn;
    @FXML private TableColumn<Comprobante, Double> totalDebitosColumn;
    @FXML private TableColumn<Comprobante, Double> totalCreditosColumn;

    private ComprobanteDAO comprobanteDAO = new ComprobanteDAO();
    private ObservableList<Comprobante> listaComprobantes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        numeroColumn.setCellValueFactory(new PropertyValueFactory<>("numeroComprobante"));
        conceptoColumn.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        totalDebitosColumn.setCellValueFactory(new PropertyValueFactory<>("totalDebitos"));
        totalCreditosColumn.setCellValueFactory(new PropertyValueFactory<>("totalCreditos"));

        // Formatear las columnas de moneda a 2 decimales
        totalDebitosColumn.setCellFactory(tc -> new TableCell<Comprobante, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });
        totalCreditosColumn.setCellFactory(tc -> new TableCell<Comprobante, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });


        comprobantesTable.setItems(listaComprobantes);

        // Cargar todos los comprobantes al inicio
        cargarComprobantes();

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Comprobantes/Vista/registro_comprobante.fxml")); // Ruta a tu FXML de registro
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

            } catch (IOException e) {
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
}