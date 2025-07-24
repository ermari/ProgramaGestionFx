package Comprobantes.Controller;

import Catalogo.Catalogo;
import Catalogo.CatalogoDAO;
import Comprobantes.modelo.Comprobante;
import Comprobantes.modelo.ComprobanteDAO;
import Comprobantes.modelo.DetalleComprobante;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RegistroComprobanteController {

    @FXML private DatePicker fechaComprobantePicker;
    @FXML private TextField numeroComprobanteField;
    @FXML private TextArea conceptoArea;
    @FXML private TableView<DetalleComprobante> partidasTable;

    @FXML private TableColumn<DetalleComprobante, Void> selectCta;
    @FXML private TableColumn<DetalleComprobante, Integer> cuentaColumn;
    @FXML private TableColumn<DetalleComprobante, String> nombreCuentaColumn;
    @FXML private TableColumn<DetalleComprobante, Double> debitoColumn;
    @FXML private TableColumn<DetalleComprobante, Double> creditoColumn;
    @FXML private TableColumn<DetalleComprobante, String> descripcionColumn;

    // --- **NUEVA VARIABLE:** Para almacenar el comprobante que se está editando ---
    private Comprobante comprobanteActual;

    @FXML private Label totalDebitosLabel;
    @FXML private Label totalCreditosLabel;

    private ObservableList<DetalleComprobante> listaDetalles = FXCollections.observableArrayList();
    private ObservableList<Catalogo> catalogoCuentasObservable;

    private CatalogoDAO cuentaDAO = new CatalogoDAO();
    private ComprobanteDAO comprobanteDAO = new ComprobanteDAO();

    @FXML
    public void initialize() {
        try {
            List<Catalogo> todasLasCuentas = cuentaDAO.obtenerTodos();
            catalogoCuentasObservable = FXCollections.observableArrayList(todasLasCuentas);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error de DB", "No se pudo cargar el catálogo de cuentas: " + e.getMessage());
            catalogoCuentasObservable = FXCollections.observableArrayList();
        }

        partidasTable.setEditable(true);
        partidasTable.setItems(listaDetalles);


        // Botón de selección de cuenta
        selectCta.setCellFactory(column -> new TableCell<>() {

            private final Button btn = new Button("");
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/resources/images/check32.png")));
            {
                icon.setFitWidth(16);
                icon.setFitHeight(16);
                btn.setGraphic(icon);
                btn.setContentDisplay(ContentDisplay.LEFT);

                btn.setOnAction(event -> {
                    DetalleComprobante detalle = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Comprobantes/Vista/busqueda_catalogo.fxml"));
                        Parent root = loader.load();

                        BusquedaCatalogoController controller = loader.getController();
                        controller.setOnCuentaSeleccionada(cuenta -> {
                            if (cuenta != null) {
                                detalle.setContableCuenta(cuenta);
                                partidasTable.refresh();
                            }
                        });

                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.setTitle("Buscar Cuenta");
                        stage.showAndWait();

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana de búsqueda.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Mostrar ID de cuenta
        cuentaColumn.setVisible(false);
        cuentaColumn.setCellValueFactory(cellData -> {
            Catalogo cuenta = cellData.getValue().getContableCuenta();
            if (cuenta != null) {
                return new SimpleIntegerProperty(cuenta.getCatalogoId()).asObject();
            }
            return null;
        });

        // Mostrar nombre de cuenta
        nombreCuentaColumn.setCellValueFactory(cellData -> {
            Catalogo cuenta = cellData.getValue().getContableCuenta();
            if (cuenta != null) {
                return new SimpleStringProperty(cuenta.getCodigo() + ' '  + cuenta.getValor());
            }
            return null;
        });



        // Configurar columnas de débito/crédito
        debitoColumn.setCellValueFactory(new PropertyValueFactory<>("debito"));
        debitoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        debitoColumn.setOnEditCommit(event -> {

            DetalleComprobante detalle = event.getRowValue();
            double nuevoValor = Math.max(0.0, event.getNewValue());
            if (nuevoValor > 0) {
                detalle.setDebito(nuevoValor);
                detalle.setCredito(0.0);
            } else {
                detalle.setDebito(0.0);
            }
            partidasTable.refresh();
            calcularTotales();
        });

        creditoColumn.setCellValueFactory(new PropertyValueFactory<>("credito"));
        creditoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        creditoColumn.setOnEditCommit(event -> {
            DetalleComprobante detalle = event.getRowValue();
            double nuevoValor = Math.max(0.0, event.getNewValue());
            if (nuevoValor > 0) {
                detalle.setCredito(nuevoValor);
                detalle.setDebito(0.0);
            } else {
                detalle.setCredito(0.0);
            }
            partidasTable.refresh();
            calcularTotales();
        });

        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        descripcionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        descripcionColumn.setOnEditCommit(event -> {
            DetalleComprobante detalle = event.getRowValue();
            String nuevoValor = event.getNewValue();
            detalle.setDescripcion(nuevoValor);  // 👈 Esto es LO QUE GUARDARÁ el nuevo valor
        });


        listaDetalles.addListener((javafx.collections.ListChangeListener<DetalleComprobante>) change -> calcularTotales());

        agregarLinea();
        fechaComprobantePicker.setValue(LocalDate.now());
    }

    @FXML
    private void agregarLinea() {
        listaDetalles.add(new DetalleComprobante(null, 0.0, 0.0 ,""));
        partidasTable.getSelectionModel().selectLast();
        partidasTable.scrollTo(listaDetalles.size() - 1);
    }

    @FXML
    private void eliminarLinea() {
        DetalleComprobante seleccionado = partidasTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Eliminación");
            confirm.setContentText("¿Deseas eliminar esta línea?");
            if (confirm.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
                listaDetalles.remove(seleccionado);
                calcularTotales();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Selección requerida", "Selecciona una línea para eliminar.");
        }
    }

    private void calcularTotales() {
        double debitos = listaDetalles.stream().mapToDouble(DetalleComprobante::getDebito).sum();
        double creditos = listaDetalles.stream().mapToDouble(DetalleComprobante::getCredito).sum();
        totalDebitosLabel.setText(String.format("%.2f", debitos));
        totalCreditosLabel.setText(String.format("%.2f", creditos));
    }

    @FXML
    private void guardarComprobante() {
        if (fechaComprobantePicker.getValue() == null || conceptoArea.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Datos faltantes", "Completa fecha y concepto del comprobante.");
            return;
        }

        if (listaDetalles.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Sin detalles", "Agrega al menos una partida.");
            return;
        }

        for (DetalleComprobante d : listaDetalles) {
            if (d.getContableCuenta() == null) {
                showAlert(Alert.AlertType.ERROR, "Cuenta faltante", "Cada línea debe tener una cuenta.");
                return;
            }
            if ((d.getDebito() > 0 && d.getCredito() > 0) || (d.getDebito() == 0 && d.getCredito() == 0)) {
                showAlert(Alert.AlertType.ERROR, "Montos inválidos", "Cada línea debe tener solo débito o solo crédito.");
                return;
            }
        }

        double debitos = Double.parseDouble(totalDebitosLabel.getText().replace(",", ""));
        double creditos = Double.parseDouble(totalCreditosLabel.getText().replace(",", ""));
        if (Math.abs(debitos - creditos) > 0.01) {
            showAlert(Alert.AlertType.ERROR, "Error de partida doble", "Débitos y créditos no coinciden.");
            return;
        }

        Comprobante comprobante = new Comprobante(
                fechaComprobantePicker.getValue(),
                generarNumeroComprobante(),
                conceptoArea.getText().trim()
        );

        listaDetalles.forEach(comprobante::addDetalle);

        try {
            comprobanteDAO.saveComprobante(comprobante);
            showAlert(Alert.AlertType.INFORMATION, "Guardado", "Comprobante guardado exitosamente.");
            limpiarFormulario();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    private String generarNumeroComprobante() {
        return "COMP-" + LocalDate.now().getYear() + "-" + (System.currentTimeMillis() % 100000);
    }

    @FXML
    private void limpiarFormulario() {
        fechaComprobantePicker.setValue(LocalDate.now());
        numeroComprobanteField.clear();
        conceptoArea.clear();
        listaDetalles.clear();
        agregarLinea();
        calcularTotales();
    }

    @FXML
    private void cancelar() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar");
        confirm.setContentText("¿Deseas cancelar y limpiar el formulario?");
        if (confirm.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
            limpiarFormulario();
        }
    }

    private void showAlert(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // --- **EL MÉTODO QUE NECESITAS** ---
    public void cargarComprobanteParaEdicion(Comprobante comprobante) {
        this.comprobanteActual = comprobante; // Guarda el comprobante que estamos editando

        // 1. Cargar los datos del encabezado del comprobante en los campos
        fechaComprobantePicker.setValue(comprobante.getFecha());
        numeroComprobanteField.setText(comprobante.getNumeroComprobante());
        conceptoArea.setText(comprobante.getConcepto());

        // 2. Cargar los detalles del comprobante en la tabla
        // Es CRÍTICO que hagas una copia de la lista de detalles si DetalleComprobante
        // no es inmutable y no quieres modificar el objeto original en memoria
        // hasta que el usuario guarde los cambios.
        listaDetalles.setAll(FXCollections.observableArrayList(comprobante.getDetalles()));

        // 3. Recalcular los totales para asegurar que se muestren correctamente
        calcularTotales();

        // Opcional: Deshabilitar la edición del número de comprobante
        // si no permites que se cambie al editar.
        numeroComprobanteField.setEditable(false);
    }
}
