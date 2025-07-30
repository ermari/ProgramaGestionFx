package Home.User.Controlador;

import Home.HomeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ListarPlantillaController
{
    private HomeController homeController;
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }


    @FXML
    private void agregarUsuario(){

    }


    @FXML
    private void salir(ActionEvent event) {
        if (homeController != null) {
            homeController.setForm("Dashboard.fxml");
        } else {
            System.err.println("⚠️ No se pudo regresar: homeController es null");
        }
    }

    @FXML
    private void  refrescarTabla(){

    }


}
