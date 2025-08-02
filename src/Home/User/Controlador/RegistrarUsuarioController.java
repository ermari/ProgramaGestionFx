package Home.User.Controlador;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.EmpresaDAO;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import Home.User.Modelo.Usuario;
import Home.User.Modelo.UsuarioDAO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import util.MensajeUtil;
import util.UtilControllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class RegistrarUsuarioController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPasword;
    @FXML private ComboBox<Empresa> comboEmpresa;
    @FXML private ListView<Sucursal> listViewSucursales;

    private final EmpresaDAO empresaDAO = new EmpresaDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private Usuario usuario;
    private UsuariosController usuarioController;

    private final Set<Integer> sucursalesAsignadasInicial = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarEmpresas();

        comboEmpresa.valueProperty().addListener((obs, oldEmpresa, nuevaEmpresa) -> {
            if (nuevaEmpresa != null) {
                cargarSucursalesPorEmpresa(nuevaEmpresa.getEmpresaId());
            } else {
                listViewSucursales.getItems().clear();
            }
        });
    }

    public void setUsuario(Usuario usuario) throws SQLException {
        this.usuario = usuario;

        if (usuario != null && usuario.getUsuarioId() != 0) {
            txtNombre.setText(usuario.getNombreUsuario());
            txtUsuario.setText(usuario.getUsuario());
            txtEmail.setText(usuario.getEmail());
            txtPasword.setText(usuario.getPassword());

            List<Sucursal> sucursalesUsuario = usuarioDAO.obtenerSucursalesDeUsuario(usuario.getUsuarioId());
            usuario.setSucursales(sucursalesUsuario);

            for (Sucursal suc : sucursalesUsuario) {
                sucursalesAsignadasInicial.add(suc.getSucursalId());
            }

            if (!sucursalesUsuario.isEmpty()) {
                Empresa empresa = sucursalesUsuario.get(0).getEmpresa();
                comboEmpresa.setValue(empresa);
                cargarSucursalesPorEmpresa(empresa.getEmpresaId());
            }
        } else {
            this.usuario = new Usuario();
            txtNombre.clear();
            txtUsuario.clear();
            txtEmail.clear();
            txtPasword.clear();
            comboEmpresa.getSelectionModel().clearSelection();
            listViewSucursales.getItems().clear();
            sucursalesAsignadasInicial.clear();
        }
    }

    private void cargarEmpresas() {
        try {
            List<Empresa> empresas = empresaDAO.listarEmpresas();
            comboEmpresa.setItems(FXCollections.observableArrayList(empresas));
        } catch (SQLException e) {
            UtilControllers.mostrarError("Error al cargar empresas", e);
        }
    }

    private void cargarSucursalesPorEmpresa(int empresaId) {
        try {
            List<Sucursal> sucursales = sucursalDAO.obtenerPorEmpresa(empresaId);
            ObservableList<Sucursal> observableList = FXCollections.observableArrayList(sucursales);
            listViewSucursales.setItems(observableList);

            listViewSucursales.setCellFactory(CheckBoxListCell.forListView(suc -> {
                BooleanProperty selected = new SimpleBooleanProperty(sucursalesAsignadasInicial.contains(suc.getSucursalId()));
                selected.addListener((obs, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        usuario.getSucursales().add(suc);
                    } else {
                        usuario.getSucursales().removeIf(s -> s.getSucursalId() == suc.getSucursalId());
                    }
                });
                return selected;
            }, new StringConverter<>() {
                @Override public String toString(Sucursal sucursal) {
                    return sucursal.getNombre();
                }
                @Override public Sucursal fromString(String string) {
                    return null;
                }
            }));

            if (usuario.getSucursales() != null) {
                for (Sucursal s : sucursales) {
                    if (usuario.getSucursales().stream().anyMatch(us -> us.getSucursalId() == s.getSucursalId())) {
                        // ya manejado por el checkbox binding
                    }
                }
            }
        } catch (SQLException e) {
            UtilControllers.mostrarError("Error al cargar sucursales", e);
        }
    }

    public void setUsuarioController(UsuariosController usuarioController) {
        this.usuarioController = usuarioController;
    }

    @FXML
    private void onGuardar() {
        try {
            if (!validarCampos()) return;

            usuario.setNombreUsuario(txtNombre.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setUsuario(txtUsuario.getText().trim());
            usuario.setPassword(txtPasword.getText());

            if (usuario.getUsuarioId() == 0) {
                usuarioDAO.insertar(usuario);
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente.", null);
            } else {
                usuarioDAO.actualizarInteligente(usuario, sucursalesAsignadasInicial);
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario actualizado correctamente.", null);
            }

            if (usuarioController != null) {
                usuarioController.cargarUsuario("", "ALL");
            }

            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            UtilControllers.mostrarError("Error al guardar usuario", e);
        }
    }

    private boolean validarCampos() {
        StringBuilder errorMessage = new StringBuilder();

        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty())
            errorMessage.append("El nombre no puede estar vacío.\n");

        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty())
            errorMessage.append("El correo electrónico no puede estar vacío.\n");

        if (txtUsuario.getText() == null || txtUsuario.getText().trim().isEmpty())
            errorMessage.append("El usuario no puede estar vacío.\n");

        if (txtPasword.getText() == null || txtPasword.getText().trim().isEmpty())
            errorMessage.append("El password no puede estar vacío.\n");

        if (comboEmpresa.getValue() == null)
            errorMessage.append("Debe seleccionar una empresa.\n");

        if (usuario.getSucursales() == null || usuario.getSucursales().isEmpty())
            errorMessage.append("Debe seleccionar al menos una sucursal.\n");

        if (!txtEmail.getText().trim().matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"))
            errorMessage.append("El formato del correo electrónico no es válido.\n");

        if (errorMessage.length() > 0) {
            MensajeUtil.mostrarAlerta(Alert.AlertType.ERROR, "Campos Inválidos",
                    "Por favor, corrija los siguientes errores:", errorMessage.toString());
            return false;
        }

        return true;
    }
}
