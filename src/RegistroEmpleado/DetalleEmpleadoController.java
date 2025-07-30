package RegistroEmpleado; // ¡Debe coincidir con el paquete de EmpleadoController!

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.MensajeUtil; // Asegúrate de que esta ruta sea correcta
import util.UtilControllers;

import java.sql.SQLException; // Importar SQLException
import java.util.Arrays;

public class DetalleEmpleadoController {

    // --- CORREGIDO: Los fx:id del FXML DEBEN COINCIDIR con los nombres de las variables @FXML ---
    @FXML
    private TextField nameField; // Coincide con fx:id="nameField"
    @FXML
    private TextField emailField; // Coincide con fx:id="emailField"
    @FXML
    private TextField deptField;  // Coincide con fx:id="deptField"
    @FXML
    private TextField salaryField; // Coincide con fx:id="salaryField"
    // Ya no necesitas txtFirstName, txtEmail, etc., si no los usas en el FXML.

    private Empleado empleado;
    private EmpleadoDAO empleadoDao = new EmpleadoDAO(); // Asumo que esta clase existe y está bien.

    // Referencia al controlador principal de la tabla
    private EmpleadoController empleadoController;

    // Puedes usar initialize para hacer cosas iniciales si lo necesitas.
    // @Override
    // public void initialize(URL url, ResourceBundle rb) {
    //     // Por ejemplo: salaryField.textProperty().addListener((obs, oldVal, newVal) -> { /* Validar que sea número */ });
    // }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
        if (empleado != null && empleado.getEmpID() != 0) {
            // --- CORREGIDO: Asignar valores a los campos correctos ---
            nameField.setText(empleado.getFirstName());
            emailField.setText(empleado.getEmail());
            deptField.setText(empleado.getDepartment());
            salaryField.setText(String.valueOf(empleado.getSalary())); // Convertir double a String
        } else {
            this.empleado = new Empleado(); // Para un nuevo registro, asegúrate de tener una instancia limpia
            // --- CORREGIDO: Limpiar los campos correctos ---
            nameField.setText("");
            emailField.setText("");
            deptField.setText("");
            salaryField.setText("");
        }
    }

    public void setEmpleadoController(EmpleadoController empleadoController) {
        this.empleadoController = empleadoController;
    }

    @FXML
    private void onGuardar() throws SQLException { // Coincide con onAction="#onGuardar" en el FXML del botón
        if (validarCampos()) { // Tu método de validación
            empleado.setFirstName(nameField.getText()); // Obtener texto de nameField
            empleado.setEmail(emailField.getText());   // Obtener texto de emailField
            empleado.setDepartment(deptField.getText()); // Obtener texto de deptField

            try {
                // Parsear el salario, maneja NumberFormatException
                empleado.setSalary(Double.parseDouble(salaryField.getText()));
            } catch (NumberFormatException e) {

                UtilControllers.mostrarError("", e);
                //MensajeUtil.mostrarAlerta(Alert.AlertType.ERROR, "Error de Entrada", "El salario debe ser un número válido.", e.getMessage().toString());
                return; // Detener la ejecución si el salario no es un número
            }

            if (empleado.getEmpID() == 0) { // Asumo que ID 0 significa nuevo empleado
                empleadoDao.insertar(empleado); // Asumo que tu DAO inserta el empleado
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",  "Empleado registrado correctamente.",null);
            } else {
                empleadoDao.modificar(empleado); // Asumo que tu DAO actualiza (antes modificar)
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Empleado actualizado correctamente.", null);
            }

            // ¡IMPORTANTE! Llama a refrescarTabla del controlador principal
            if (empleadoController != null) {
                empleadoController.cargarEmpleado("","ALL");
            }

            // Cierra esta ventana después de guardar
            Stage stage = (Stage) nameField.getScene().getWindow(); // Usar cualquier campo para obtener el Stage
            stage.close();

        }
    }

    // No hay botón "Cancelar" en tu FXML actual, pero si lo añades, puedes usar esto:
    /*
    @FXML
    private void onCancelar() { // Puedes cambiar el nombre del método en el FXML
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    */

    private boolean validarCampos() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "El nombre no puede estar vacío.\n";
        }
        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            errorMessage += "El correo electrónico no puede estar vacío.\n";
        }
        if (deptField.getText() == null || deptField.getText().trim().isEmpty()) {
            errorMessage += "El departamento no puede estar vacío.\n";
        }
        if (salaryField.getText() == null || salaryField.getText().trim().isEmpty()) {
            errorMessage += "El salario no puede estar vacío.\n";
        } else {
            try {
                Double.parseDouble(salaryField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "El salario debe ser un número válido.\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            MensajeUtil.mostrarAlerta(Alert.AlertType.ERROR, "Campos Inválidos", "Por favor, corrija los siguientes errores:", errorMessage);
            return false;
        }
    }
}