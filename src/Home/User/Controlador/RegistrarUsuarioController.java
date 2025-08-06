package Home.User.Controlador;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.EmpresaDAO;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import Home.User.Modelo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPasword;

    @FXML private ComboBox<Empresa> comboEmpresa;
    // --- CAMBIO 1: El ListView ahora es de tipo CheckBox ---
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
                        // --- CAMBIO 2: Llamamos al nuevo método para cargar las sucursales como CheckBoxes ---
                        cargarSucursales(seleccionada.getEmpresaId());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cargarRoles();
            // --- CAMBIO 3: Ya no es necesario el modo de selección múltiple ya que cada CheckBox se encarga de su estado ---
            // listViewSucursales.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
           // listViewRoles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- NUEVO MÉTODO: Carga las sucursales como una lista de CheckBoxes ---
    private void cargarSucursales(int empresaId) throws SQLException {
        List<Sucursal> sucursales = sucursalDAO.obtenerPorEmpresa(empresaId);
        ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();

        for (Sucursal sucursal : sucursales) {
            CheckBox cb = new CheckBox(sucursal.getNombreSucursal());
            cb.setUserData(sucursal); // Almacenamos el objeto Sucursal en el CheckBox
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
            checkBoxes.add(cb);
        }

        listViewRoles.setItems(checkBoxes);

    }

    // --- El método actualizarListaRolesSeleccionados ya no es necesario o debe ser revisado ---
    // El método original parece tener un error lógico, por lo que lo hemos eliminado para simplificar.
    // Si su propósito es cargar permisos basados en roles, esa lógica debería estar en otro método.

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

                    // --- CAMBIO 4: Carga las sucursales como CheckBoxes ---
                    cargarSucursales(empresa.getEmpresaId());

                    // --- CAMBIO 5: Selecciona los CheckBoxes correspondientes a las sucursales del usuario ---
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
            List<Permiso>permisos =new ArrayList<>();
            // Crea una lista observable para guardar los nombres de los permisos
            ObservableList<String> nombresPermisos = FXCollections.observableArrayList();


            for (CheckBox cb : listViewRoles.getItems()) {
                Rol rol = (Rol) cb.getUserData();
                for (Rol asignado : rolesAsignados) {
                    if (rol.getRolId() == asignado.getRolId()) {
                        cb.setSelected(true);
                        permisos=rolDAO.obtenerPermisosDelRol(rol.getRolId());
                        for (Permiso permiso : permisos){
                            nombresPermisos.add(permiso.getNombre());
                        }
                        // Asigna la lista completa al ListView
                        listViewPermisos.setItems(nombresPermisos);


                        break;
                    }
                }
            }

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

        // --- CAMBIO 6: Recolecta las sucursales de los CheckBoxes seleccionados ---
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