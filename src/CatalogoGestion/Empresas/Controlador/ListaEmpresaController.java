package CatalogoGestion.Empresas.Controlador;



import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.EmpresaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ListaEmpresaController {

    @FXML private TableView<Empresa> tablaEmpresas;
    @FXML private TableColumn<Empresa, String> colNombre;
    @FXML private TableColumn<Empresa, String> colRuc;
    @FXML private TableColumn<Empresa, String> colTipo;
    @FXML private TableColumn<Empresa, String> colEstado;

    private EmpresaDAO empresaDAO = new EmpresaDAO();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colRuc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRuc()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoEmpresa()));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isEstado() ? "Activa" : "Inactiva"));

        cargarEmpresas();
    }

    private void cargarEmpresas() {
        try {
            List<Empresa> lista = empresaDAO.listarEmpresas();
            tablaEmpresas.getItems().setAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void nuevaEmpresa() {
        mostrarFormularioEmpresa(null);
    }

    @FXML
    private void editarEmpresa() {
        Empresa seleccionada = tablaEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            mostrarFormularioEmpresa(seleccionada);
        } else {
            mostrarAlerta("Seleccione una empresa para editar.");
        }
    }



    @FXML
    private void verSucursales() {
        Empresa seleccionada = tablaEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            mostrarListaSucursales(seleccionada);
        } else {
            mostrarAlerta("Seleccione una empresa para ver sus sucursales.");
        }
    }

    private void mostrarListaSucursales(Empresa empresa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/Empresas/Vista/ListaSucursal.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la lista de sucursales
            ListaSucursalController controller = loader.getController();

            // Pasar el id de la empresa para cargar sus sucursales
            controller.setEmpresa(empresa);

            // Crear y mostrar la ventana modal
            Stage stage = new Stage();
            stage.setTitle("Sucursales de " + empresa.getNombre());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir la lista de sucursales.");
        }
    }




    private void mostrarFormularioEmpresa(Empresa empresa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/Empresas/Vista/RegistroEmpresa.fxml"));
            Parent root = loader.load();
            RegistroEmpresaController controller = loader.getController();
            controller.setEmpresa(empresa);
            controller.setOnGuardar(this::cargarEmpresas);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(empresa == null ? "Nueva Empresa" : "Editar Empresa");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
