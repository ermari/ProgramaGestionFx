package CatalogoGestion.Empresas.Controlador;


import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistroSucursalController {

    @FXML private TextField txtNombre, txtCodigo, txtDireccion, txtTelefono, txtEmail, txtCiudad, txtPais;
    @FXML private CheckBox chkActivo;

    private Empresa empresa;
    private final SucursalDAO sucursalDAO = new SucursalDAO();
    private Sucursal sucursal;
    private int empresaId;
    private Runnable onGuardar;


    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        this.empresaId = empresa.getEmpresaId(); // opcional si también necesitas empresaId
    }


    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
        if (sucursal != null) {
            // Carga datos en los campos del formulario
            txtNombre.setText(sucursal.getNombre());
            txtCodigo.setText(sucursal.getCodigo());
            // Bloquea edición del código
            txtCodigo.setDisable(true);
            txtDireccion.setText(sucursal.getDireccion());
            txtTelefono.setText(sucursal.getTelefono());
            txtEmail.setText(sucursal.getEmail());
            txtCiudad.setText(sucursal.getCiudad());
            txtPais.setText(sucursal.getPais());
            chkActivo.setSelected(sucursal.isEstado());
        } else {
            // Campos vacíos para nuevo registro
            txtNombre.clear();
            txtCodigo.clear();
            txtDireccion.clear();
            txtTelefono.clear();
            txtEmail.clear();
            txtCiudad.clear();
            txtPais.clear();
            chkActivo.setSelected(true);
        }
    }


    public void setOnGuardar(Runnable onGuardar) {
        this.onGuardar = onGuardar;
    }

    @FXML
    private void guardarSucursal() {
        try {
            Sucursal s = new Sucursal();

            // ✅ Asignar directamente el objeto Empresa (no el ID)
            s.setEmpresa(empresa); // Asegúrate de que "empresa" esté correctamente seteada antes

            s.setNombre(txtNombre.getText());
            s.setCodigo(txtCodigo.getText());
            s.setDireccion(txtDireccion.getText());
            s.setTelefono(txtTelefono.getText());
            s.setEmail(txtEmail.getText());
            s.setCiudad(txtCiudad.getText());
            s.setPais(txtPais.getText());
            s.setEstado(chkActivo.isSelected());

            if (sucursal == null) {
                sucursalDAO.guardarSucursal(s);   // INSERT
            } else {
                s.setSucursalId(sucursal.getSucursalId());
                sucursalDAO.actualizarSucursal(s); // UPDATE
            }

            if (onGuardar != null) onGuardar.run();
            cerrar(); // Cierra la ventana

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}
