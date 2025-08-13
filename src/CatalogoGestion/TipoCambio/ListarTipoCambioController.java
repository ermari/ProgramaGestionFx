package CatalogoGestion.TipoCambio;

import Home.HomeController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.List;

public class ListarTipoCambioController {

    @FXML private TableView<TipoCambio> tablaTiposDeCambio;
    @FXML private TableColumn<TipoCambio, Integer> colId;
    @FXML private TableColumn<TipoCambio, LocalDate> colFechaInicio;
    @FXML private TableColumn<TipoCambio, LocalDate> colFechaFin;
    @FXML private TableColumn<TipoCambio, BigDecimal> colValor;
    @FXML private Button btnSalir;

    private ObservableList<TipoCambio> tiposCambioList = FXCollections.observableArrayList();
    private TipoCambioDAO tipoCambioDAO = new TipoCambioDAO();

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
        // Enlazar las columnas a las propiedades del modelo
        colId.setCellValueFactory(new PropertyValueFactory<>("tipoCambioId"));

        // Usar CellFactory para formatear las fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        colFechaInicio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaInicio()));
        colFechaInicio.setCellFactory(column -> new TableCell<TipoCambio, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        colFechaFin.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaFin()));
        colFechaFin.setCellFactory(column -> new TableCell<TipoCambio, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // Usar CellFactory para formatear el valor BigDecimal con 4 decimales
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colValor.setCellFactory(column -> new TableCell<TipoCambio, BigDecimal>() {
            private final DecimalFormat df = new DecimalFormat("#,##0.0000");

            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                }
            }
        });

        // Cargar datos al iniciar el controlador
        cargarTiposDeCambio();
        tablaTiposDeCambio.setItems(tiposCambioList);
    }

    private void cargarTiposDeCambio() {
        tiposCambioList.clear();
        List<TipoCambio> listaDesdeDB = tipoCambioDAO.listar();
        tiposCambioList.addAll(listaDesdeDB);
    }

    @FXML
    private void nuevoTipoCambio() {
        mostrarFormulario(null);
    }

    @FXML
    private void editarTipoCambio() {
        TipoCambio seleccionado = tablaTiposDeCambio.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarFormulario(seleccionado);
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Debe seleccionar un tipo de cambio para editar.");
        }
    }

    @FXML
    private void eliminarTipoCambio() {
        TipoCambio seleccionado = tablaTiposDeCambio.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("Eliminar tipo de cambio");
            alert.setContentText("¿Está seguro de que desea eliminar el tipo de cambio de fecha: " + seleccionado.getFechaInicio() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                tipoCambioDAO.eliminar(seleccionado.getTipoCambioId());
                cargarTiposDeCambio(); // Refrescar la tabla
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Debe seleccionar un tipo de cambio para eliminar.");
        }
    }

    private void mostrarFormulario(TipoCambio tipoCambio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/TipoCambio/FormularioTipoCambio.fxml"));
            Parent root = loader.load();

            FormularioTipoCambioController controller = loader.getController();
            controller.setTipoCambio(tipoCambio);
            controller.setOnGuardar(this::cargarTiposDeCambio); // Al guardar, refresca la tabla

            Stage stage = new Stage();
            stage.setTitle(tipoCambio == null ? "Nuevo Tipo de Cambio" : "Editar Tipo de Cambio");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}