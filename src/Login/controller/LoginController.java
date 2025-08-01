package Login.controller;



import CatalogoGestion.Empresas.Modelo.Sucursal;
import Home.HomeController;
import Home.User.Modelo.Usuario;
import Home.User.Modelo.UsuarioDAO;
import Login.model.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    @FXML
    private void onLogin() {
        String username = txtUsuario.getText();
        String password = txtPassword.getText();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuarioAutenticado = usuarioDAO.login(username, password);

        if (usuarioAutenticado != null) {
            try {

                Sesion.setUsuarioActual(usuarioAutenticado); // 🔥 Guardar en sesión


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/Home.fxml"));
                Parent root = loader.load();

                // Aquí podrías pasar usuarioAutenticado al HomeController si lo necesitas

                Stage homeStage = new Stage();
                homeStage.setScene(new Scene(root));
                homeStage.setTitle("Home");
                homeStage.setMaximized(true);
                homeStage.show();

                // Cerrar login
                Stage loginStage = (Stage) txtUsuario.getScene().getWindow();
                loginStage.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            lblMensaje.setText("Credenciales inválidas");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }



}
