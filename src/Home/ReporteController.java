package Home;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReporteController  implements Initializable {
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

    }


    }



