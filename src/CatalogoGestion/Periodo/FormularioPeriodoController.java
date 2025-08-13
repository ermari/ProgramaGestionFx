package CatalogoGestion.Periodo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class FormularioPeriodoController  implements Initializable {
    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private CheckBox chkActivo;

    private int id;

    private Periodo periodo;
    private Runnable onGuardar;

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
        if (periodo != null) {
            id=periodo.getId();

            txtNombre.setText(periodo.getNombre());
            dpFechaInicio.setValue(periodo.getFechaInicio());
            dpFechaFin.setValue(periodo.getFechaFin());
            chkActivo.setSelected(periodo.isEstado());
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDatePickerFormat(dpFechaInicio);
        FormatDataPicker(dpFechaInicio);
        setDatePickerFormat(dpFechaFin);
        FormatDataPicker(dpFechaFin);

    }

    public void setOnGuardar(Runnable onGuardar) {
        this.onGuardar = onGuardar;
    }

    @FXML
    private void guardarPeriodo() throws SQLException {
        String nombre = txtNombre.getText();
        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();
        Boolean   estado =  (chkActivo.isSelected());

        periodo=new Periodo();

        if (nombre == null || nombre.trim().isEmpty() || fechaInicio == null || fechaFin == null) {
            mostrarAlerta("Error de validación", "Todos los campos son obligatorios.");
            return;
        }

        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta("Error de validación", "La fecha de inicio no puede ser posterior a la fecha de fin.");
            return;
        }


        periodo.setNombre(nombre);
        periodo.setFechaInicio(fechaInicio);
        periodo.setFechaFin(fechaFin);
        periodo.setEstado(estado);

        // Simula la lógica de guardar en la base de datos
        if (id== 0) {
            PeriodoDAO.guardarPeriodo(periodo);
        } else {
           periodo.setId(id);
            PeriodoDAO.actualizarPeriodo(periodo);
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
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void FormatDataPicker(DatePicker dp) {
    final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {



        @Override
        public DateCell call(final DatePicker datePicker) {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    // Aquí puedes añadir lógica para deshabilitar fechas si lo necesitas
                    // Por ejemplo, para deshabilitar días anteriores a hoy:
                    // if (item.isBefore(LocalDate.now())) {
                    //     setDisable(true);
                    //     setStyle("-fx-background-color: #ffc0cb;");
                    // }
                }
            };
        }
    };
        dp.setDayCellFactory(dayCellFactory);

    // Opcional: También puedes establecer el valor inicial si quieres
    // dpFecha.setValue(LocalDate.now());
}

    public static void setDatePickerFormat(DatePicker datePicker) {
        String pattern = "dd/MM/yyyy";

        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
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





}
