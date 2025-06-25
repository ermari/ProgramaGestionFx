package RegistroEmpleado;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.MensajeUtil;

public class DetalleEmpleadoController {

    private EmpleadoDao empleadoDao = new EmpleadoDao();
    private Stage stage;
    private EmpleadoController empleadoController;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField deptField;
    @FXML private TextField salaryField;

    private Empleado empleado; // será null si es nuevo

    private Runnable onSaveCallback;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setEmpleadoController(EmpleadoController controller) {
        this.empleadoController = controller;
    }

    public void setEmpleado(Empleado emp) {
        this.empleado = emp;

        if (emp != null) {
            nameField.setText(emp.getFirstName());
            emailField.setText(emp.getEmail());
            deptField.setText(emp.getDepartment());
            salaryField.setText(String.valueOf(emp.getSalary()));
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void onGuardar() {
        String nombre = nameField.getText().trim();
        String correo = emailField.getText().trim();
        String depto = deptField.getText().trim();
        String salarioStr = salaryField.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || depto.isEmpty() || salarioStr.isEmpty()) {
            MensajeUtil.mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Completa todos los campos", null);
            return;
        }

        try {
            double salario = Double.parseDouble(salarioStr);
            if (salario < 0) throw new NumberFormatException();

            if (empleado == null) {
                empleado = new Empleado(); // Nuevo empleado
            }

            empleado.setFirstName(nombre);
            empleado.setEmail(correo);
            empleado.setDepartment(depto);
            empleado.setSalary(salario);

            if (empleado.getEmpID() > 0) {
                empleadoDao.modificarEmpleado(empleado);
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Modificado", "Empleado actualizado correctamente.", null);
            } else {
                empleadoDao.insertarEmpleado(empleado);
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Empleado agregado correctamente.", null);
            }

            if (empleadoController != null) {
                empleadoController.cargarEmpleados(); // Refrescar la tabla
            }

            if (onSaveCallback != null) onSaveCallback.run();

            cerrarVentana();

        } catch (NumberFormatException e) {
            MensajeUtil.mostrarAlerta(Alert.AlertType.ERROR, "Salario inválido", "Debe ser un número positivo", e);
        } catch (Exception e) {
            MensajeUtil.mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", e.getMessage(), e);
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
