package Home;

import Catalogo.CatalogoController;
import CatalogoGestion.Empresas.Controlador.ListaEmpresaController;
import CatalogoGestion.MasterCatalogo.Controladores.MasterCatalogoListaController;
import CatalogoGestion.MasterCatalogo.Controladores.MasterCatalogoRegistroController;
import CatalogoGestion.Periodo.ListarPeriodoController;
import CatalogoGestion.TipoCambio.ListarTipoCambioController;
import Home.User.Controlador.ListarPermisosController;
import Home.User.Controlador.ListarRolesController;
import Home.User.Controlador.UsuariosController;
import RegistroEmpleado.EmpleadoController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
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
        //homeController.setTitulo("Bien Venido al Sistema");

    }




    @FXML
    private TextField texto;

    @FXML
    public void abrirRoles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/User/Vista/Roles.fxml"));
            AnchorPane empleado = loader.load();

            ListarRolesController controller = loader.getController();
            controller.setHomeController(homeController); // 🔁 aquí pasa el home

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
        public void abrirPermiso() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/User/Vista/ListarPermisos.fxml"));
                Parent anchorPane = loader.load();

                ListarPermisosController controller = loader.getController();
                controller.setHomeController(homeController); // 🔁 aquí pasa el home

                if (contentPane != null) {
                    contentPane.getChildren().setAll(anchorPane);
                    AnchorPane.setTopAnchor(anchorPane, 0.0);
                    AnchorPane.setBottomAnchor(anchorPane, 0.0);
                    AnchorPane.setLeftAnchor(anchorPane, 0.0);
                    AnchorPane.setRightAnchor(anchorPane, 0.0);
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
    public void abrirUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/User/Vista/Usuarios.fxml"));
            AnchorPane empleado = loader.load();

            UsuariosController controller = loader.getController();
            controller.setHomeController(homeController); // 🔁 aquí pasa el home

            if (contentPane != null) {
                contentPane.getChildren().setAll(empleado);
                AnchorPane.setTopAnchor(empleado, 0.0);
                AnchorPane.setBottomAnchor(empleado, 0.0);
                AnchorPane.setLeftAnchor(empleado, 0.0);
                AnchorPane.setRightAnchor(empleado, 0.0);
            }

            if (homeController != null) {
                homeController.setTitulo("        Registro Usuraio");
            } else {
                System.err.println("⚠️ homeController es null en abrir formulario Usuario()");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirPeriodo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/Periodo/ListarPeriodos.fxml"));
            AnchorPane empleado = loader.load();

            ListarPeriodoController controller = loader.getController();
            controller.setHomeController(homeController); // 🔁 aquí pasa el home

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
    public void abrirTipoCambio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/TipoCambio/ListarTipoCambio.fxml"));
            AnchorPane anchorPane = loader.load();

            ListarTipoCambioController controller = loader.getController();
            controller.setHomeController(homeController); // 🔁 aquí pasa el home

            if (contentPane != null) {
                contentPane.getChildren().setAll(anchorPane);
                AnchorPane.setTopAnchor(anchorPane, 0.0);
                AnchorPane.setBottomAnchor(anchorPane, 0.0);
                AnchorPane.setLeftAnchor(anchorPane, 0.0);
                AnchorPane.setRightAnchor(anchorPane, 0.0);
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

            CatalogoController controller = loader.getController();
            controller.setHomeController(homeController); // 🔁 aquí pasa el home


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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/MasterCatalogo/vistas/master_catalogo_lista.fxml"));
            AnchorPane catalogo = loader.load();

            MasterCatalogoListaController controller = loader.getController();
            controller.setHomeController(homeController); // 🔁 aquí pasa el home

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
    public void abrirEmpresa() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/Empresas/Vista/ListaEmpresa.fxml"));
            AnchorPane catalogo = loader.load();

            ListaEmpresaController controller = loader.getController();  //sustituir MasterCatalogoListaController
            controller.setHomeController(homeController); // 🔁 aquí pasa el home


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
    public void abrirEmpleado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroEmpleado/empleado.fxml"));
            AnchorPane empleado = loader.load();

            EmpleadoController controller = loader.getController();
            controller.setHomeController(homeController); // 🔁 aquí pasa el home

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


}
