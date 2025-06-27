package Home;

import Home.HomeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private AnchorPane contentPane;
    private HomeController homeController;

    public void setContentPane(AnchorPane contentPane) {
        this.contentPane = contentPane;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // homeController.setTitulo("Bien Venido al Sistema");
    }

    @FXML
    public void abrirEmpleado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroEmpleado/Empleado.fxml"));
            AnchorPane empleado = loader.load();

            if (contentPane != null) {
                contentPane.getChildren().setAll(empleado);
                AnchorPane.setTopAnchor(empleado, 0.0);
                AnchorPane.setBottomAnchor(empleado, 0.0);
                AnchorPane.setLeftAnchor(empleado, 0.0);
                AnchorPane.setRightAnchor(empleado, 0.0);
            }

            // ✅ Aquí movemos la llamada para cambiar el título después de cargar la vista
            if (homeController != null) {
                homeController.setTitulo("        Registro Empleado");
            } else {
                System.err.println("⚠️ homeController es null en abrirEmpleado()");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirCatalogo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Catalogo/catalogos.fxml"));
            AnchorPane empleado = loader.load();

            if (contentPane != null) {
                contentPane.getChildren().setAll(empleado);
                AnchorPane.setTopAnchor(empleado, 0.0);
                AnchorPane.setBottomAnchor(empleado, 0.0);
                AnchorPane.setLeftAnchor(empleado, 0.0);
                AnchorPane.setRightAnchor(empleado, 0.0);
            }

            // ✅ Aquí movemos la llamada para cambiar el título después de cargar la vista
            if (homeController != null) {
                homeController.setTitulo("        Registro Catalogos");
            } else {
                System.err.println("⚠️ homeController es null en abrirCatalogo()");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
