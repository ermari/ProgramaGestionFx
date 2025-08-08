package CatalogoGestion.Empresas.Controlador;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistroSucursalController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtPais;
    @FXML private CheckBox chkActivo;

    private Empresa empresa;
    private final SucursalDAO sucursalDAO = new SucursalDAO();
    private Sucursal sucursal;
    private Runnable onGuardar;

    // Se eliminó la variable 'empresaId' ya que el objeto 'empresa' es suficiente.

    /**
     * Establece la empresa a la que pertenece la sucursal.
     * @param empresa El objeto Empresa.
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    /**
     * Carga los datos de la sucursal en los campos del formulario.
     * Si el objeto sucursal es null, prepara el formulario para un nuevo registro.
     * @param sucursal El objeto Sucursal a editar, o null para una nueva.
     */
    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
        if (sucursal != null) {
            // Carga datos para la edición
            txtNombre.setText(sucursal.getNombre());
            txtCodigo.setText(sucursal.getCodigo());
            txtCodigo.setDisable(true); // Bloquea la edición del código
            txtDireccion.setText(sucursal.getDireccion());
            txtTelefono.setText(sucursal.getTelefono());
            txtEmail.setText(sucursal.getEmail());
            txtCiudad.setText(sucursal.getCiudad());
            txtPais.setText(sucursal.getPais());
            chkActivo.setSelected(sucursal.isEstado());
        } else {
            // Limpia campos para un nuevo registro
            limpiarCampos();
        }
    }

    /**
     * Establece la acción que se ejecutará al guardar (por ejemplo, refrescar la lista).
     * @param onGuardar Un objeto Runnable con la acción a ejecutar.
     */
    public void setOnGuardar(Runnable onGuardar) {
        this.onGuardar = onGuardar;
    }

    @FXML
    private void guardarSucursal() {
        try {
            // Validación básica de campos
            if (txtNombre.getText().trim().isEmpty() || txtCodigo.getText().trim().isEmpty()) {
                mostrarAlerta("Error de validación", "El nombre y el código no pueden estar vacíos.");
                return;
            }

            Sucursal s = new Sucursal();
            s.setNombre(txtNombre.getText().trim());
            s.setCodigo(txtCodigo.getText().trim());
            s.setDireccion(txtDireccion.getText().trim());
            s.setTelefono(txtTelefono.getText().trim());
            s.setEmail(txtEmail.getText().trim());
            s.setCiudad(txtCiudad.getText().trim());
            s.setPais(txtPais.getText().trim());
            s.setEstado(chkActivo.isSelected());
            s.setEmpresa(empresa); // Asigna la empresa correctamente

            if (sucursal == null) {
                // Nuevo registro
                sucursalDAO.guardarSucursal(s);
            } else {
                // Actualización de registro
                s.setSucursalId(sucursal.getSucursalId());
                sucursalDAO.actualizarSucursal(s);
            }

            if (onGuardar != null) {
                onGuardar.run(); // Ejecuta la acción de refresco
            }
            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al guardar", "Ocurrió un error al intentar guardar la sucursal.");
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtCodigo.clear();
        txtCodigo.setDisable(false); // Habilita la edición del código para un nuevo registro
        txtDireccion.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtCiudad.clear();
        txtPais.clear();
        chkActivo.setSelected(true); // Por defecto, una nueva sucursal está activa
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}