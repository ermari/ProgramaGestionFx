package Home;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
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
    private void btnGenerarReporte(ActionEvent event) {

        Connection conn = null;
        try {
            // 1️⃣ Configuración de conexión a MySQL
            String url = "jdbc:mysql://localhost:3306/datasoft";
            String user = "root";           // tu usuario
            String pass = "123465";    // tu contraseña

            // Conectar
            conn = DriverManager.getConnection(url, user, pass);

            // 2️⃣ Ruta al JRXML
            String jrxml = "src/Reportes/reporte_simple.jrxml";

            HashMap<String, Object> parameters = new HashMap<>();

            // 💡 Agregar imagen relativa
            String imagePath = getClass().getResource("/resources/images/Tree.png").toExternalForm();
            parameters.put("imagen1", imagePath);

            // 3️⃣ Compilar
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);

            // 5️⃣ Llenar el reporte usando la conexión real
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            // 6️⃣ Mostrar visor
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7️⃣ Cerrar conexión
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
