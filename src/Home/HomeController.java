package Home;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import Home.User.Modelo.Usuario;
import Login.model.Sesion;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private ImageView Exit;

    @FXML
    private Label Menu;

    @FXML
    private Label MenuClose;

    @FXML
    private AnchorPane slider;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label titulo;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblSucursal;

    @FXML
    private Label lblEmpresa;

    Usuario usuario = Sesion.getUsuarioActual();
    Sucursal sucursal=Sesion.getSucursalSeleccionada();
    Empresa empresa=Sesion.getEmpresaSeleccionada();

    public void setTitulo(String texto) {
        titulo.setText(texto);
        System.out.println("🏷 Título actualizado a: " + texto);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitulo("Bienvenido al Sistema");
        lblNombre.setText("Usuario: " + usuario.getNombreUsuario());
        lblNombre.setStyle("-fx-text-fill: orange;");

        lblSucursal.setText("Sucursal: " + sucursal.getNombre());
        lblSucursal.setStyle("-fx-text-fill: orange;");

        lblEmpresa.setText("Empresa: " + empresa.getNombre());
        lblEmpresa.setStyle("-fx-text-fill: orange;");

        Platform.runLater(() -> {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setMaximized(true);
        });

//Exit.setOnMouseClicked(event -> System.exit(0));

        Menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), slider);
            slide.setToX(0);
            slide.play();
            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(false);
                MenuClose.setVisible(true);
            });
        });

        MenuClose.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), slider);
            slide.setToX(-176);
            slide.play();
            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(true);
                MenuClose.setVisible(false);
            });
        });

        slider.setVisible(true);
        setForm("Dashboard.fxml");
    }

    @FXML
    private void handleDashboard() throws IOException {
        setForm("Dashboard.fxml");
    }

    @FXML
    public void handleAdd() throws IOException {
        setForm("/RegistroUsuario/Usuarios.fxml");
    }

    @FXML
    private void handleReport() throws IOException {
        setForm("Report.fxml");
    }

    @FXML
    private void handleExit() throws IOException {
        System.exit(0);
    }

    public void setForm(String fxml) {
        try {
            FXMLLoader loader;

            // Si empieza con "/", es ruta absoluta
            if (fxml.startsWith("/")) {
                loader = new FXMLLoader(getClass().getResource(fxml));

            } else {
                loader = new FXMLLoader(getClass().getResource("/Home/" + fxml));
            }


            AnchorPane view = loader.load();
            String nombre = fxml.toLowerCase();

            if (nombre.contains("dashboard")) {
                DashboardController controller = loader.getController();
                controller.setContentPane(contentArea);
                controller.setHomeController(this);
                setTitulo("        Bienvenido al Sistema");
            } else if (nombre.contains("report")) {
                ReporteController controller = loader.getController();
                controller.setContentPane(contentArea);
                controller.setHomeController(this);
                setTitulo("       Bienvenido al Reportes");
            } else {
                setTitulo("Vista cargada");
            }

            contentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
