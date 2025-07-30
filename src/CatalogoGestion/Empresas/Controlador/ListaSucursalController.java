package CatalogoGestion.Empresas.Controlador;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.Empresas.Modelo.SucursalDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.List;

public class ListaSucursalController {

    @FXML private TableView<Sucursal> tableSucursales;
    @FXML private TableColumn<Sucursal, String> colNombre;
    @FXML private TableColumn<Sucursal, String> colCodigo;
    @FXML private TableColumn<Sucursal, String> colDireccion;
    @FXML private TableColumn<Sucursal, String> colTelefono;
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
        colNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        colCodigo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCodigo()));
        colDireccion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDireccion()));
        colTelefono.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefono()));
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isEstado()));

        // Para mostrar checkBox en la columna de estado
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
        try {
            List<Sucursal> lista = sucursalDAO.obtenerPorEmpresa(empresa.getEmpresaId());
            sucursalesList.setAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
            // Aquí podrías mostrar alerta de error
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
        mostrarFormularioSucursal(null);  // null = nueva
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
            controller.setSucursal(sucursal);             // Para editar o nuevo
            controller.setEmpresa(this.empresa);      // Vincular sucursal a empresa
            controller.setOnGuardar(this::cargarSucursales);  // Refresca lista al guardar

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
