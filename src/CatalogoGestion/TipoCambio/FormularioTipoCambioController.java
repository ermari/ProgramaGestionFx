package CatalogoGestion.TipoCambio;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class FormularioTipoCambioController implements Initializable {
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextField txtValor;

    private TipoCambio tipoCambio;
    private TipoCambioDAO tipoCambioDAO = new TipoCambioDAO();
    private Runnable onGuardar;

    public void setTipoCambio(TipoCambio tipoCambio) {
        this.tipoCambio = tipoCambio;
        if (tipoCambio != null) {
            dpFechaInicio.setValue(tipoCambio.getFechaInicio());
            dpFechaFin.setValue(tipoCambio.getFechaFin());
            txtValor.setText(tipoCambio.getValor().toString());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDatePickerFormat(dpFechaInicio);
        setDatePickerFormat(dpFechaFin);
        // Opcional: Agregar un validador para el campo de valor
        addValorValidator();
    }

    public void setOnGuardar(Runnable onGuardar) {
        this.onGuardar = onGuardar;
    }

    @FXML
    private void guardarTipoCambio() {
        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();
        String valorTexto = txtValor.getText();

        if (fechaInicio == null || fechaFin == null || valorTexto == null || valorTexto.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", "Todos los campos son obligatorios.");
            return;
        }

        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", "La fecha de inicio no puede ser posterior a la fecha de fin.");
            return;
        }

        BigDecimal valor;
        try {
            valor = new BigDecimal(valorTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de formato", "El valor del tipo de cambio debe ser un número válido.");
            return;
        }

        if (tipoCambio == null) {
            // Lógica para crear un nuevo registro
            tipoCambio = new TipoCambio(0, fechaInicio, fechaFin, valor);
            tipoCambioDAO.crear(tipoCambio);
        } else {
            // Lógica para modificar un registro existente
            tipoCambio.setFechaInicio(fechaInicio);
            tipoCambio.setFechaFin(fechaFin);
            tipoCambio.setValor(valor);
            tipoCambioDAO.modificar(tipoCambio);
        }

        if (onGuardar != null) {
            onGuardar.run(); // Refresca la tabla principal
        }
        cerrar();
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) dpFechaInicio.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void setDatePickerFormat(DatePicker datePicker) {
        String pattern = "dd/MM/yyyy";
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        datePicker.setConverter(converter);
    }

    private void addValorValidator() {
        txtValor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*\\.?\\d{0,4}")) {
                txtValor.setText(oldValue);
            }
        });
    }
}