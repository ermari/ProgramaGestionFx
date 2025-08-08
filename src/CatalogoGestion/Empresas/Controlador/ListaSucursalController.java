package CatalogoGestion.Empresas.Controlador;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ListaSucursalController {

    @FXML private TableView<Sucursal> tableSucursales;
    @FXML private TableColumn<Sucursal, String> colNombre;
    @FXML private TableColumn<Sucursal, String> colCodigo;
    @FXML private TableColumn<Sucursal, String> colDireccion;
    @FXML private TableColumn<Sucursal, String> colTelefono;
    @FXML private TableColumn<Sucursal, String> colEmail;
    @FXML private TableColumn<Sucursal, String> colCiudad;
    @FXML private TableColumn<Sucursal, String> colPais;
    @FXML private TableColumn<Sucursal, Boolean> colEstado;

    private SucursalDAO sucursalDAO = new SucursalDAO();
    private Empresa empresa;

    private ObservableList<Sucursal> sucursalesList = FXCollections.observableArrayList();

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        cargarSucursales();
    }

    @FXML
    public void initialize() {
        // Enlazar las propiedades de la clase Sucursal a las columnas de la tabla
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigo()));
        colDireccion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDireccion()));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colCiudad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCiudad()));
        colPais.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPais()));

        // Manejar el estado como un CheckBox
        colEstado.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isEstado()));
        colEstado.setCellFactory(tc -> new TableCell<Sucursal, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox check = new CheckBox();
                    check.setDisable(true);
                    check.setSelected(activo);
                    setGraphic(check);
                }
            }
        });

        tableSucursales.setItems(sucursalesList);
    }

    private void cargarSucursales() {
        if (empresa != null) {
            try {
                List<Sucursal> lista = sucursalDAO.obtenerPorEmpresa(empresa.getEmpresaId());
                sucursalesList.setAll(lista);
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error al cargar las sucursales desde la base de datos.");
            }
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) tableSucursales.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void nuevaSucursal() {
        mostrarFormularioSucursal(null);
    }

    @FXML
    private void editarSucursal() {
        Sucursal seleccionada = tableSucursales.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            mostrarFormularioSucursal(seleccionada);
        } else {
            mostrarAlerta("Seleccione una sucursal para editar.");
        }
    }

    private void mostrarFormularioSucursal(Sucursal sucursal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/Empresas/Vista/RegistroSucursal.fxml"));
            Parent root = loader.load();

            RegistroSucursalController controller = loader.getController();
            controller.setSucursal(sucursal);
            controller.setEmpresa(this.empresa);
            controller.setOnGuardar(this::cargarSucursales);

            Stage stage = new Stage();
            stage.setTitle(sucursal == null ? "Nueva Sucursal" : "Editar Sucursal");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir el formulario de sucursal.");
        }
    }
}