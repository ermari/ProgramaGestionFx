package Home.User.Controlador;

// ... (tus importaciones existentes)

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.EmpresaDAO;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import Home.User.Modelo.Permiso;
import Home.User.Modelo.Rol;
import Home.User.Modelo.RolDAO;
import Home.User.Modelo.Usuario;
import Home.User.Modelo.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RegistrarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPasword;

    @FXML private ComboBox<Empresa> comboEmpresa;
    @FXML private ListView<CheckBox> listViewSucursales;

    @FXML private ListView<CheckBox> listViewRoles;
    @FXML private ListView<String> listViewPermisos;

    private Usuario usuario;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EmpresaDAO empresaDAO = new EmpresaDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();
    private final RolDAO rolDAO = new RolDAO();

    @FXML
    public void initialize() {
        try {
            comboEmpresa.setItems(FXCollections.observableArrayList(empresaDAO.listarEmpresas()));

            comboEmpresa.setOnAction(e -> {
                Empresa seleccionada = comboEmpresa.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    try {
                        cargarSucursales(seleccionada.getEmpresaId());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cargarRoles();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarSucursales(int empresaId) throws SQLException {
        List<Sucursal> sucursales = sucursalDAO.obtenerPorEmpresa(empresaId);
        ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();

        for (Sucursal sucursal : sucursales) {
            CheckBox cb = new CheckBox(sucursal.getNombre());
            cb.setUserData(sucursal);
            checkBoxes.add(cb);
        }

        listViewSucursales.setItems(checkBoxes);
    }

    private void cargarRoles() throws SQLException {
        List<Rol> roles = rolDAO.listarRoles();
        ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();

        for (Rol rol : roles) {
            CheckBox cb = new CheckBox(rol.getNombre());
            cb.setUserData(rol);

            // --- CAMBIO CLAVE: Agregar el listener ---
            cb.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                actualizarPermisos();
            });
            // ------------------------------------------

            checkBoxes.add(cb);
        }

        listViewRoles.setItems(checkBoxes);
    }

    /**
     * Este método se encarga de obtener los permisos de todos los roles seleccionados
     * y de actualizar la lista de permisos en la interfaz de usuario.
     */
    private void actualizarPermisos() {
        // Obtenemos los roles seleccionados del ListView
        List<Rol> rolesSeleccionados = listViewRoles.getItems().stream()
                .filter(CheckBox::isSelected)
                .map(cb -> (Rol) cb.getUserData())
                .collect(Collectors.toList());

        // Usamos un Set para evitar permisos duplicados
        Set<String> nombresPermisos = new HashSet<>();

        try {
            for (Rol rol : rolesSeleccionados) {
                // Obtenemos los permisos para cada rol seleccionado
                List<Permiso> permisosDelRol = rolDAO.obtenerPermisosDelRol(rol.getRolId());

                // Agregamos el nombre de cada permiso al Set
                for (Permiso permiso : permisosDelRol) {
                    nombresPermisos.add(permiso.getNombre());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener permisos de los roles.");
        }

        // Convertimos el Set de nombres a una ObservableList y actualizamos el ListView
        listViewPermisos.setItems(FXCollections.observableArrayList(new ArrayList<>(nombresPermisos)));
    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

        txtNombre.setText(usuario.getNombreUsuario());
        txtEmail.setText(usuario.getEmail());
        txtUsuario.setText(usuario.getUsuario());
        txtPasword.setText(usuario.getPassword());

        try {
            List<Sucursal> sucursalesUsuario = usuarioDAO.obtenerSucursalesDelUsuario(usuario.getUsuarioId());
            if (!sucursalesUsuario.isEmpty()) {
                Sucursal primera = sucursalesUsuario.get(0);
                Empresa empresa = empresaDAO.obtenerPorId(primera.getEmpresa().getEmpresaId());

                if (empresa != null) {
                    comboEmpresa.getSelectionModel().select(empresa);
                    cargarSucursales(empresa.getEmpresaId());

                    for (CheckBox cb : listViewSucursales.getItems()) {
                        Sucursal sucursalEnLista = (Sucursal) cb.getUserData();
                        for (Sucursal sucUsuario : sucursalesUsuario) {
                            if (sucursalEnLista.getSucursalId() == sucUsuario.getSucursalId()) {
                                cb.setSelected(true);
                                break;
                            }
                        }
                    }
                }
            }

            List<Rol> rolesAsignados = usuarioDAO.obtenerRolesDelUsuario(usuario.getUsuarioId());

            for (CheckBox cb : listViewRoles.getItems()) {
                Rol rol = (Rol) cb.getUserData();
                for (Rol asignado : rolesAsignados) {
                    if (rol.getRolId() == asignado.getRolId()) {
                        cb.setSelected(true);
                        break;
                    }
                }
            }

            // --- LLAMADA INICIAL PARA CARGAR LOS PERMISOS AL INICIO ---
            actualizarPermisos();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGuardar() {
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();
        String usuarioTxt = txtUsuario.getText();
        String password = txtPasword.getText();

        if (nombre.isEmpty() || usuarioTxt.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Validación", "Nombre, usuario y password son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        List<Sucursal> sucursalesSeleccionadas = listViewSucursales.getItems().stream()
                .filter(CheckBox::isSelected)
                .map(cb -> (Sucursal) cb.getUserData())
                .collect(Collectors.toList());

        List<Rol> rolesSeleccionados = listViewRoles.getItems().stream()
                .filter(CheckBox::isSelected)
                .map(cb -> (Rol) cb.getUserData())
                .collect(Collectors.toList());

        try {
            if (usuario == null) {
                usuario = new Usuario();
                usuario.setNombreUsuario(nombre);
                usuario.setEmail(email);
                usuario.setUsuario(usuarioTxt);
                usuario.setPassword(password);
                usuario.setSucursales(sucursalesSeleccionadas);
                usuario.setRoles(rolesSeleccionados);
                usuarioDAO.insertar(usuario);
            } else {
                usuario.setNombreUsuario(nombre);
                usuario.setEmail(email);
                usuario.setUsuario(usuarioTxt);
                usuario.setPassword(password);
                usuario.setSucursales(sucursalesSeleccionadas);
                usuario.setRoles(rolesSeleccionados);
                usuarioDAO.actualizar(usuario);
            }

            cerrarVentana();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar el usuario.", Alert.AlertType.ERROR);
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void setUsuarioController(UsuariosController usuariosController) {
    }
}