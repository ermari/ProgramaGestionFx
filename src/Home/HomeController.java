package Home;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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

    public void setTitulo(String texto) {
        titulo.setText(texto);
        System.out.println("🏷 Título actualizado a: " + texto);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitulo("Bienvenido al Sistema");

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
        setForm("/RegistroEmpleado/Empleado.fxml");
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
