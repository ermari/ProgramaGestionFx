package Home;

import Comprobantes.Controller.BusquedaCatalogoController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private TextField texto;

    @FXML
    public void abrirEmpleado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroUsuario/listaUsuario.fxml"));
            AnchorPane empleado = loader.load();

            if (contentPane != null) {
                contentPane.getChildren().setAll(empleado);
                AnchorPane.setTopAnchor(empleado, 0.0);
                AnchorPane.setBottomAnchor(empleado, 0.0);
                AnchorPane.setLeftAnchor(empleado, 0.0);
                AnchorPane.setRightAnchor(empleado, 0.0);
            }

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
            AnchorPane catalogo = loader.load();

            if (contentPane != null) {
                contentPane.getChildren().setAll(catalogo);
                AnchorPane.setTopAnchor(catalogo, 0.0);
                AnchorPane.setBottomAnchor(catalogo, 0.0);
                AnchorPane.setLeftAnchor(catalogo, 0.0);
                AnchorPane.setRightAnchor(catalogo, 0.0);
            }

            if (homeController != null) {
                homeController.setTitulo("        Registro Catalogos");
            } else {
                System.err.println("⚠️ homeController es null en abrirCatalogo()");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirComprobante() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Comprobantes/Vista/ListaComprobantes.fxml"));
            AnchorPane catalogo = loader.load();

            if (contentPane != null) {
                contentPane.getChildren().setAll(catalogo);
                AnchorPane.setTopAnchor(catalogo, 0.0);
                AnchorPane.setBottomAnchor(catalogo, 0.0);
                AnchorPane.setLeftAnchor(catalogo, 0.0);
                AnchorPane.setRightAnchor(catalogo, 0.0);
            }

            if (homeController != null) {
                homeController.setTitulo("        Registro  Comprobante");
            } else {
                System.err.println("⚠️ homeController es null en abrirCatalogo()");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void abrirCatalogoGestion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/vistas/master_catalogo_lista.fxml"));
            AnchorPane catalogo = loader.load();

            if (contentPane != null) {
                contentPane.getChildren().setAll(catalogo);
                AnchorPane.setTopAnchor(catalogo, 0.0);
                AnchorPane.setBottomAnchor(catalogo, 0.0);
                AnchorPane.setLeftAnchor(catalogo, 0.0);
                AnchorPane.setRightAnchor(catalogo, 0.0);
            }

            if (homeController != null) {
                homeController.setTitulo("        Registro Catalogos Gestion");
            } else {
                System.err.println("⚠️ homeController es null en abrirCatalogo()");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirBusquedaCuenta() throws IOException {
        BusquedaCuenta ("Debito");
    }



    @FXML
    private void BusquedaCuenta(String tipo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Comprobantes/Vista/busqueda_catalogo.fxml"));
        Parent root = loader.load();

        BusquedaCatalogoController controller = loader.getController();
        controller.setOnCuentaSeleccionada(cuenta -> {
            if (tipo.equals("Debito") ){
                texto.setText(cuenta.getCodigo() + " - " + cuenta.getValor());
                // puedes guardar también el ID o la entidad completa si lo necesitas
            }

        });

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle("Buscar Cuenta");
        stage.showAndWait();
    }


}
