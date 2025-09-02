package Comprobantes.Controller;

import Catalogo.Catalogo;
import Catalogo.CatalogoDAO;
import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.MasterCatalogo.Modelo.DetalleCatalogo;
import CatalogoGestion.MasterCatalogo.Modelo.DetalleCatalogoDAO;
import CatalogoGestion.Periodo.Periodo;
import CatalogoGestion.Periodo.PeriodoDAO;
import Comprobantes.modelo.Comprobante;
import Comprobantes.modelo.ComprobanteDAO;
import Comprobantes.modelo.DetalleComprobante;
import Home.User.Modelo.Usuario;
import Login.model.Sesion;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RegistroComprobanteController {

    @FXML private DatePicker fechaComprobantePicker;
    @FXML private TextField numeroComprobanteField;
    @FXML private TextArea conceptoArea;
    @FXML private ComboBox<DetalleCatalogo> cbTipoComprobante;
    @FXML private ComboBox<Periodo> cbPeriodo;
    @FXML private TableView<DetalleComprobante> partidasTable;

    @FXML private TableColumn<DetalleComprobante, Void> selectCta;
    @FXML private TableColumn<DetalleComprobante, Integer> cuentaColumn;
    @FXML private TableColumn<DetalleComprobante, String> nombreCuentaColumn;
    @FXML private TableColumn<DetalleComprobante, BigDecimal> debitoColumn;
    @FXML private TableColumn<DetalleComprobante, BigDecimal> creditoColumn;
    @FXML private TableColumn<DetalleComprobante, String> descripcionColumn;

    @FXML private Label totalDebitosLabel;
    @FXML private Label totalCreditosLabel;
    // Propiedades observables
    private final ObjectProperty<BigDecimal> totalDebitos = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> totalCreditos = new SimpleObjectProperty<>(BigDecimal.ZERO);


    private Comprobante comprobanteActual;
    private final ObservableList<DetalleComprobante> listaDetalles = FXCollections.observableArrayList();
    private ObservableList<Catalogo> catalogoCuentasObservable;

    private final CatalogoDAO cuentaDAO = new CatalogoDAO();
    private final ComprobanteDAO comprobanteDAO = new ComprobanteDAO();
    private final DetalleCatalogoDAO detalleCatalogoDAO = new DetalleCatalogoDAO();
    private final PeriodoDAO periodoDAO = new PeriodoDAO();
    //user
    Usuario usuario = Sesion.getUsuarioActual();
    Sucursal sucursal=Sesion.getSucursalSeleccionada();
    Empresa empresa=Sesion.getEmpresaSeleccionada();


    @FXML
    public void initialize() {
        try {
            // Cargar catálogo de cuentas
            List<Catalogo> todasLasCuentas = cuentaDAO.obtenerTodos();
            catalogoCuentasObservable = FXCollections.observableArrayList(todasLasCuentas);

            // Cargar lista de tipos de comprobante
            List<DetalleCatalogo> listaTipoComprobante =
                    detalleCatalogoDAO.obtenerPorCodigoMaster("TIPO_COMPROBANTE");
            // Convertir lista a ObservableList
            ObservableList<DetalleCatalogo> tipoComprobanteObservable =
                    FXCollections.observableArrayList(listaTipoComprobante);

            // Asignar al ComboBox
            cbTipoComprobante.setItems(tipoComprobanteObservable);

            // Mostrar nombre en la lista
            cbTipoComprobante.setConverter(new StringConverter<>() {
                @Override
                public String toString(DetalleCatalogo detalle) {
                    return (detalle != null) ? detalle.getNombreItem() : "";
                }

                @Override
                public DetalleCatalogo fromString(String string) {
                    // Se busca por nombre en la lista (poco usado, pero necesario para autocompletar)
                    return tipoComprobanteObservable.stream()
                            .filter(d -> d.getNombreItem().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });
// Y esto para que también se muestre el seleccionado arriba
            cbTipoComprobante.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(DetalleCatalogo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombreItem());
                }
            });


            ObservableList<Periodo> lista = FXCollections.observableArrayList(periodoDAO.obtenerPeriodosActivos());
            cbPeriodo.setItems(lista);
            cbPeriodo.setConverter(new StringConverter<>() {
                @Override
                public String toString(Periodo p) {
                    return p != null ? p.getDescripcion() : "";
                }

                @Override
                public Periodo fromString(String string) {
                    return lista.stream().filter(p -> p.getDescripcion().equals(string)).findFirst().orElse(null);
                }
            });

            cbPeriodo.setOnAction(e -> {
                Periodo seleccionado = cbPeriodo.getSelectionModel().getSelectedItem();
                configurarFechaComprobante(seleccionado);
            });


        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error de DB", "No se pudo cargar el catálogo: " + e.getMessage());
            catalogoCuentasObservable = FXCollections.observableArrayList();
        }

        partidasTable.setEditable(true);
        partidasTable.setItems(listaDetalles);

        setupTableColumns();

        // Vincular labels
        totalDebitosLabel.textProperty().bind(Bindings.createStringBinding(
                () -> totalDebitos.get().toString(), totalDebitos));
        totalCreditosLabel.textProperty().bind(Bindings.createStringBinding(
                () -> totalCreditos.get().toString(), totalCreditos));

        fechaComprobantePicker.setValue(LocalDate.now());
        agregarLinea();
    }


    // Método para configurar el DatePicker según un Periodo
    private void configurarFechaComprobante(Periodo periodo) {
        if (periodo == null) return;

        LocalDate fechaInicio = periodo.getFechaInicio();
        LocalDate fechaFin = periodo.getFechaFin();

        fechaComprobantePicker.setValue(fechaInicio); // establecer valor inicial

        fechaComprobantePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // deshabilitar fechas fuera del rango
                if (item.isBefore(fechaInicio) || item.isAfter(fechaFin)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // opcional: resaltar fechas deshabilitadas
                }
            }
        });
    }

    // Método para recalcular totales
    private void recalcularTotales() {
        BigDecimal sumaDebitos = listaDetalles.stream()
                .map(DetalleComprobante::getDebito)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumaCreditos = listaDetalles.stream()
                .map(DetalleComprobante::getCredito)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Actualiza las propiedades (los labels se refrescan solos)
        totalDebitos.set(sumaDebitos);
        totalCreditos.set(sumaCreditos);

    }

    private void calcularTotales() {
        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;

        for (DetalleComprobante fila : partidasTable.getItems()) {
            if (fila.getDebito() != null) {
                totalDebitos = totalDebitos.add(fila.getDebito());
            }
            if (fila.getCredito() != null) {
                totalCreditos = totalCreditos.add(fila.getCredito());
            }
        }

        totalDebitosLabel.setText(totalDebitos.setScale(4, RoundingMode.HALF_UP).toPlainString());
        totalCreditosLabel.setText(totalCreditos.setScale(4, RoundingMode.HALF_UP).toPlainString());
    }

    private void setupTableColumns() {

        // Botón de selección de cuenta
        selectCta.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("");
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/resources/icons/check.png")));

            {
                icon.setFitWidth(16);
                icon.setFitHeight(16);
                btn.setGraphic(icon);
                btn.setContentDisplay(ContentDisplay.RIGHT);
                btn.setMaxSize(5,5);
                btn.setOnAction(event -> {
                    DetalleComprobante detalle = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Comprobantes/Vista/busqueda_catalogo.fxml"));
                        Parent root = loader.load();
                        BusquedaCatalogoController controller = loader.getController();
                        controller.setOnCuentaSeleccionada(cuenta -> {
                            if (cuenta != null) {
                                detalle.setContableCuenta(cuenta);
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

        // Columnas básicas
        cuentaColumn.setCellValueFactory(cellData -> {
            Catalogo cuenta = cellData.getValue().getContableCuenta();
            return (cuenta != null)
                    ? new SimpleIntegerProperty(cuenta.getCatalogoId()).asObject()
                    : new SimpleIntegerProperty(0).asObject();
        });

        nombreCuentaColumn.setCellValueFactory(cellData -> Bindings.createStringBinding(
                () -> {
                    Catalogo cuenta = cellData.getValue().getContableCuenta();
                    return cuenta != null ? cuenta.getCodigo() + " " + cuenta.getValor() : "";
                },
                cellData.getValue().contableCuentaProperty()
        ));

        // Conversor BigDecimal con 4 decimales
        StringConverter<BigDecimal> bigDecimalConverter = new StringConverter<>() {
            @Override
            public String toString(BigDecimal object) {
                if (object == null) return "";
                return object.setScale(4, RoundingMode.HALF_UP).toPlainString();
            }

            @Override
            public BigDecimal fromString(String string) {
                if (string == null || string.isBlank()) return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
                try {
                    return new BigDecimal(string).setScale(4, RoundingMode.HALF_UP);
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
                }
            }
        };

        debitoColumn.setCellValueFactory(new PropertyValueFactory<>("debito"));

        debitoColumn.setCellFactory(col -> new BigDecimalEditingCell(bigDecimalConverter));
        debitoColumn.setOnEditCommit(event -> {
            DetalleComprobante detalle = event.getRowValue();
            BigDecimal nuevoValor = event.getNewValue().max(BigDecimal.ZERO);
            detalle.setDebito(nuevoValor);
            if (nuevoValor.compareTo(BigDecimal.ZERO) > 0) {
                detalle.setCredito(BigDecimal.ZERO);
            }
           partidasTable.refresh();
            recalcularTotales(); // recalcula cada vez que se edite
        });

        //debitoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        creditoColumn.setCellValueFactory(new PropertyValueFactory<>("credito"));
        creditoColumn.setCellFactory(col -> new BigDecimalEditingCell(bigDecimalConverter));
        creditoColumn.setOnEditCommit(event -> {
            DetalleComprobante detalle = event.getRowValue();
            BigDecimal nuevoValor = event.getNewValue().max(BigDecimal.ZERO);
            detalle.setCredito(nuevoValor);
            if (nuevoValor.compareTo(BigDecimal.ZERO) > 0) {
                detalle.setDebito(BigDecimal.ZERO);
            }
            partidasTable.refresh();
            recalcularTotales(); // recalcula cada vez que se edite
        });


        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        descripcionColumn.setCellFactory(column -> {
            TextFieldTableCell<DetalleComprobante, String> cell = new TextFieldTableCell<>(new DefaultStringConverter()) {
                private TextField textField;

                @Override
                public void startEdit() {
                    super.startEdit();
                    if (isEditing() && getGraphic() instanceof TextField tf) {
                        textField = tf;
                        // Listener para guardar al perder el foco
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal) { // perdió el foco
                                commitEdit(textField.getText());
                            }
                        });
                    }
                }
            };
            return cell;
        });

// Ahora ya no dependes de ENTER, se actualiza al perder foco
        descripcionColumn.setOnEditCommit(event -> {
            event.getRowValue().setDescripcion(event.getNewValue());

        });
    }

    @FXML
    private void agregarLinea() {
        listaDetalles.add(new DetalleComprobante());
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
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Selección requerida", "Selecciona una línea para eliminar.");
        }
    }

    @FXML
    private void guardarComprobante() {
        if (!validarFormulario()) return;

        //Construir el objeto de comprobante Para enviar.
        Comprobante comprobante = new Comprobante(
                fechaComprobantePicker.getValue(),
                numeroComprobanteField.getText().trim(),
                conceptoArea.getText().trim(),
                usuario,
                cbTipoComprobante.getSelectionModel().getSelectedItem(),
                sucursal,
                cbPeriodo.getSelectionModel().getSelectedItem()
        );

        //detalles
        listaDetalles.forEach(comprobante::addDetalle);

        try {
            comprobanteDAO.saveComprobante(comprobante);
            showAlert(Alert.AlertType.INFORMATION, "Guardado", "Comprobante guardado exitosamente.");
            limpiarFormulario();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }



        showAlert(Alert.AlertType.INFORMATION, "Guardado", "Comprobante guardado exitosamente.");
        limpiarFormulario();
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

    private boolean validarFormulario() {
        if (fechaComprobantePicker.getValue() == null || conceptoArea.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Datos faltantes", "Completa fecha y concepto del comprobante.");
            return false;
        }

        if (listaDetalles.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Sin detalles", "Agrega al menos una partida.");
            return false;
        }

        for (DetalleComprobante d : listaDetalles) {
            if (d.getContableCuenta() == null) {
                showAlert(Alert.AlertType.ERROR, "Cuenta faltante", "Cada línea debe tener una cuenta.");
                return false;
            }
            if ((d.getDebito().compareTo(BigDecimal.ZERO) > 0 && d.getCredito().compareTo(BigDecimal.ZERO) > 0)
                    || (d.getDebito().compareTo(BigDecimal.ZERO) == 0 && d.getCredito().compareTo(BigDecimal.ZERO) == 0)) {
                showAlert(Alert.AlertType.ERROR, "Montos inválidos", "Cada línea debe tener solo débito o solo crédito.");
                return false;
            }
        }

        BigDecimal debitos = listaDetalles.stream()
                .map(d -> d.getDebito() == null ? BigDecimal.ZERO : d.getDebito())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditos = listaDetalles.stream()
                .map(d -> d.getCredito() == null ? BigDecimal.ZERO : d.getCredito())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (debitos.subtract(creditos).abs().compareTo(new BigDecimal("0.0001")) > 0) {
            showAlert(Alert.AlertType.ERROR, "Error de partida doble", "Débitos y créditos no coinciden.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private String formatTotal(BigDecimal valor) {
        return valor.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    public void cargarComprobanteParaEdicion(Comprobante comprobante) {
        this.comprobanteActual = comprobante;
        fechaComprobantePicker.setValue(comprobante.getFecha());
        numeroComprobanteField.setText(comprobante.getNumeroComprobante());
        conceptoArea.setText(comprobante.getConcepto());
        listaDetalles.setAll(comprobante.getDetalles());
    }


    public class BigDecimalEditingCell extends TableCell<DetalleComprobante, BigDecimal> {
        private final TextField textField = new TextField();
        private final StringConverter<BigDecimal> converter;

        public BigDecimalEditingCell(StringConverter<BigDecimal> converter) {
            this.converter = converter;

            // Guardar con Enter
            textField.setOnAction(evt -> commitHelper());

            // Guardar al perder foco
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    commitHelper();
                }
            });
        }

        private void commitHelper() {
            String text = textField.getText();
            if (text == null || text.isBlank()) {
                commitEdit(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
            } else {
                try {
                    commitEdit(new BigDecimal(text).setScale(4, RoundingMode.HALF_UP));
                } catch (NumberFormatException e) {
                    commitEdit(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
                }
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();
            BigDecimal value = getItem();

            if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
                textField.setText(""); // mostrar vacío si es 0
            } else {
                textField.setText(converter.toString(value));
            }

            setGraphic(textField);
            setText(null);
            textField.requestFocus();
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            BigDecimal value = getItem();
            if (value == null) value = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
            setText(converter.toString(value));
            setGraphic(null);
        }

        @Override
        protected void updateItem(BigDecimal item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                textField.setText(item == null || item.compareTo(BigDecimal.ZERO) == 0 ? "" : converter.toString(item));
                setGraphic(textField);
                setText(null);
            } else {
                if (item == null) {
                    setText("0.0000");
                } else {
                    setText(converter.toString(item));
                }
                setGraphic(null);
            }
        }
    }


}
