package CatalogoGestion.Empresas.Controlador;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.EmpresaDAO;
import Home.HomeController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListaEmpresaController {

    @FXML private TableView<Empresa> tablaEmpresas;
    @FXML private TableColumn<Empresa, String> colNombre;
    @FXML private TableColumn<Empresa, String> colRazonSocial;
    @FXML private TableColumn<Empresa, String> colRuc;
    @FXML private TableColumn<Empresa, String> colDireccion;
    @FXML private TableColumn<Empresa, String> colTelefono;
    @FXML private TableColumn<Empresa, String> colEmail;
    @FXML private TableColumn<Empresa, String> colRepresentante;
    @FXML private TableColumn<Empresa, String> colTipo;
    @FXML private TableColumn<Empresa, LocalDate> colFechaConstitucion;
    @FXML private TableColumn<Empresa, String> colEstado;

    private EmpresaDAO empresaDAO = new EmpresaDAO();
    private HomeController homeController;
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @FXML
    private void salir(ActionEvent event) {
        if (homeController != null) {
            homeController.setForm("Dashboard.fxml");
        }
    }

    @FXML
    public void initialize() {
        // Enlazar las propiedades de la clase Empresa a las columnas de la tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        colRuc.setCellValueFactory(new PropertyValueFactory<>("ruc"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRepresentante.setCellValueFactory(new PropertyValueFactory<>("representante"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoEmpresa"));

        // Manejar la fecha de constitución
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        colFechaConstitucion.setCellValueFactory(new PropertyValueFactory<>("fechaConstitucion"));
        colFechaConstitucion.setCellFactory(column -> new TableCell<Empresa, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // Enlazar el estado (booleano) a un texto
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isEstado() ? "Activa" : "Inactiva"));

        cargarEmpresas();
    }

    private void cargarEmpresas() {
        try {
            List<Empresa> lista = empresaDAO.listarEmpresas();
            tablaEmpresas.getItems().setAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar las empresas desde la base de datos.");
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
            ListaSucursalController controller = loader.getController();
            controller.setEmpresa(empresa);

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