package CatalogoGestion.Empresas.Controlador;


import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.EmpresaDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class RegistroEmpresaController {

    @FXML private TextField txtNombre, txtRazonSocial, txtRuc, txtDireccion, txtTelefono, txtEmail, txtRepresentante, txtTipo;
    @FXML private DatePicker dpFechaConstitucion;
    @FXML private CheckBox chkActivo;

    private EmpresaDAO empresaDAO = new EmpresaDAO();
    private Empresa empresa;

    private Runnable onGuardar;

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        if (empresa != null) {
            txtNombre.setText(empresa.getNombre());
            txtRazonSocial.setText(empresa.getRazonSocial());
            txtRuc.setText(empresa.getRuc());
            txtDireccion.setText(empresa.getDireccion());
            txtTelefono.setText(empresa.getTelefono());
            txtEmail.setText(empresa.getEmail());
            txtRepresentante.setText(empresa.getRepresentante());
            txtTipo.setText(empresa.getTipoEmpresa());
            dpFechaConstitucion.setValue(empresa.getFechaConstitucion());
            chkActivo.setSelected(empresa.isEstado());
        } else {
            dpFechaConstitucion.setValue(LocalDate.now());
            chkActivo.setSelected(true);
        }
    }

    public void setOnGuardar(Runnable callback) {
        this.onGuardar = callback;
    }

    @FXML
    private void guardarEmpresa() {
        try {
            if (empresa == null) empresa = new Empresa();
            empresa.setNombre(txtNombre.getText());
            empresa.setRazonSocial(txtRazonSocial.getText());
            empresa.setRuc(txtRuc.getText());
            empresa.setDireccion(txtDireccion.getText());
            empresa.setTelefono(txtTelefono.getText());
            empresa.setEmail(txtEmail.getText());
            empresa.setRepresentante(txtRepresentante.getText());
            empresa.setTipoEmpresa(txtTipo.getText());
            empresa.setFechaConstitucion(dpFechaConstitucion.getValue());
            empresa.setEstado(chkActivo.isSelected());

            if (empresa.getEmpresaId() == 0) {
                empresaDAO.guardarEmpresa(empresa);
            } else {
                empresaDAO.actualizarEmpresa(empresa);
            }

            if (onGuardar != null) onGuardar.run();
            cerrar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Método guardarEmpresa ejecutado");
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
