package Home.User.Controlador;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.EmpresaDAO;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import Home.User.Modelo.Usuario;
import Home.User.Modelo.UsuarioDAO;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.MensajeUtil;
import util.UtilControllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrarUsuarioController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtPasword;

    @FXML private ComboBox<Empresa> comboEmpresa;
    @FXML private ComboBox<Sucursal> comboSucursal;

    private final EmpresaDAO empresaDAO = new EmpresaDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private Usuario usuario;
    private UsuariosController usuarioController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cargarEmpresas(); // Cargar empresas siempre

        // Al cambiar la empresa, cargar sus sucursales
        comboEmpresa.valueProperty().addListener((obs, oldEmpresa, nuevaEmpresa) -> {
            if (nuevaEmpresa != null) {
                cargarSucursalesPorEmpresa(nuevaEmpresa.getEmpresaId());
            } else {
                comboSucursal.getItems().clear();
            }
        });



    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

        if (usuario != null && usuario.getUsuarioId() != 0) {
            // Edición
            txtNombre.setText(usuario.getNombreUsuario());
            txtUsuario.setText(usuario.getUsuario());
            txtEmail.setText(usuario.getEmail());
            txtPasword.setText(usuario.getPassword());

            if (usuario.getSucursal() != null) {
                Sucursal sucursal = usuario.getSucursal();

                try {
                    Empresa empresa = sucursal.getEmpresa();
                    if (empresa == null) {
                        empresa = empresaDAO.obtenerPorId(sucursal.getEmpresa().getEmpresaId());
                        sucursal.setEmpresa(empresa);
                    }

                    comboEmpresa.setValue(empresa);
                    cargarSucursalesPorEmpresa(empresa.getEmpresaId());
                    comboSucursal.setValue(sucursal);

                } catch (SQLException e) {
                    UtilControllers.mostrarError("Error al cargar empresa de la sucursal", e);
                }
            }
        } else {
            // Nuevo usuario
            this.usuario = new Usuario(); // Asegúrate de crear un nuevo objeto
            txtNombre.clear();
            txtUsuario.clear();
            txtEmail.clear();
            txtPasword.clear();
            comboEmpresa.getSelectionModel().clearSelection();
            comboSucursal.getItems().clear(); // importante si cambia de empresa
        }
    }


    private void cargarEmpresas() {
        try {
            List<Empresa> empresas = empresaDAO.listarEmpresas();
            comboEmpresa.setItems(FXCollections.observableArrayList(empresas));

            comboEmpresa.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Empresa item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });

            comboEmpresa.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Empresa item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });

        } catch (SQLException e) {
            UtilControllers.mostrarError("Error al cargar empresas", e);
        }
    }

    private void cargarSucursalesPorEmpresa(int empresaId) {
        try {
            List<Sucursal> sucursales = sucursalDAO.obtenerPorEmpresa(empresaId);
            comboSucursal.setItems(FXCollections.observableArrayList(sucursales));

            comboSucursal.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Sucursal item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });

            comboSucursal.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Sucursal item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });

        } catch (SQLException e) {
            UtilControllers.mostrarError("Error al cargar sucursales", e);
        }
    }

    public void setUsuarioController(UsuariosController usuarioController) {
        this.usuarioController = usuarioController;
    }

    @FXML
    private void onGuardar() throws SQLException {
        Sucursal sucursal = comboSucursal.getValue();
        usuario.setSucursal(sucursal);

        if (validarCampos()) {
            usuario.setNombreUsuario(txtNombre.getText());
            usuario.setEmail(txtEmail.getText());
            usuario.setUsuario(txtUsuario.getText());
            usuario.setPassword(txtPasword.getText());

            if (usuario.getUsuarioId() == 0) {
                usuarioDAO.insertar(usuario);
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente.", null);
            } else {
                usuarioDAO.actualizar(usuario);
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario actualizado correctamente.", null);
            }

            if (usuarioController != null) {
                usuarioController.cargarUsuario("", "ALL");
            }

            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.close();
        }
    }

    private boolean validarCampos() {
        StringBuilder errorMessage = new StringBuilder();

        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            errorMessage.append("El nombre no puede estar vacío.\n");
        }
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            errorMessage.append("El correo electrónico no puede estar vacío.\n");
        }
        if (txtUsuario.getText() == null || txtUsuario.getText().trim().isEmpty()) {
            errorMessage.append("El usuario no puede estar vacío.\n");
        }
        if (txtPasword.getText() == null || txtPasword.getText().trim().isEmpty()) {
            errorMessage.append("El password no puede estar vacío.\n");
        }
        if (comboEmpresa.getValue() == null) {
            errorMessage.append("Debe seleccionar una empresa.\n");
        }
        if (comboSucursal.getValue() == null) {
            errorMessage.append("Debe seleccionar una sucursal.\n");
        }

        // --- Validación de formato de correo electrónico ---
        // Solo valida el formato si el campo no está vacío para evitar doble mensaje de error
        if (txtEmail.getText() != null && !txtEmail.getText().trim().isEmpty()) {
            String email = txtEmail.getText().trim();
            // Expresión regular para validar el formato del correo electrónico
            String emailRegex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

            if (!email.matches(emailRegex)) {
                errorMessage.append("El formato del correo electrónico no es válido.\n");
            }
        }





        if (errorMessage.isEmpty()) {
            return true;
        } else {
            MensajeUtil.mostrarAlerta(Alert.AlertType.ERROR, "Campos Inválidos", "Por favor, corrija los siguientes errores:", errorMessage.toString());
            return false;
        }
    }
}
