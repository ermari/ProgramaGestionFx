package Login.controller;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import Home.User.Modelo.Usuario;
import Home.User.Modelo.UsuarioDAO;
import Login.model.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    @FXML
    private void onLogin() throws SQLException {
        String username = txtUsuario.getText();
        String password = txtPassword.getText();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuarioAutenticado = usuarioDAO.login(username, password);

        if (usuarioAutenticado != null) {
            try {
                Sesion.setUsuarioActual(usuarioAutenticado);

                List<Sucursal> sucursales = usuarioDAO.obtenerSucursalesDelUsuario(usuarioAutenticado.getUsuarioId());
                usuarioAutenticado.setSucursales(sucursales);

                // Obtener empresas únicas sin repetidos
                List<Empresa> empresas = sucursales.stream()
                        .map(Sucursal::getEmpresa)
                        .filter(Objects::nonNull)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        Empresa::getEmpresaId,
                                        e -> e,
                                        (e1, e2) -> e1 // en caso de colisión, conservar la primera
                                ),
                                m -> new ArrayList<>(m.values())
                        ));

                Sesion.setEmpresasDisponibles(empresas);

                // Abrir selector modal
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/User/Vista/SelectorEmpresaSucursal.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Seleccionar Empresa y Sucursal");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Validar selección
                Empresa empresaSeleccionada = Sesion.getEmpresaSeleccionada();
                Sucursal sucursalSeleccionada = Sesion.getSucursalSeleccionada();

                if (empresaSeleccionada == null || sucursalSeleccionada == null) {
                    lblMensaje.setText("Debe seleccionar empresa y sucursal");
                    lblMensaje.setStyle("-fx-text-fill: red;");
                    return;
                }

                // Aquí cargas Home con datos del usuario y empresa/sucursal seleccionadas
                FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/Home/Home.fxml"));
                Parent homeRoot = homeLoader.load();

                Stage homeStage = new Stage();
                homeStage.setScene(new Scene(homeRoot));
                homeStage.setTitle("Home");
                homeStage.setMaximized(true);
                homeStage.show();

                // Cerrar login
                Stage loginStage = (Stage) txtUsuario.getScene().getWindow();
                loginStage.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            lblMensaje.setText("Credenciales inválidas");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }


}
