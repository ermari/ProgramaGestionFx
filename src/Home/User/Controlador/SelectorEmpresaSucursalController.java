package Home.User.Controlador;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import Login.model.Sesion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class SelectorEmpresaSucursalController {

    @FXML
    private ComboBox<Empresa> comboEmpresa;

    @FXML
    private ComboBox<Sucursal> comboSucursal;

    @FXML
    public void initialize() {
        List<Empresa> empresas = Sesion.getEmpresasDisponibles();
        if (empresas != null && !empresas.isEmpty()) {
            comboEmpresa.setItems(FXCollections.observableArrayList(empresas));

            // Seleccionar la primera empresa automáticamente
            comboEmpresa.getSelectionModel().selectFirst();

            // Cargar sucursales para la empresa seleccionada inicialmente
            Empresa seleccionada = comboEmpresa.getValue();
            if (seleccionada != null) {
                List<Sucursal> sucursales = Sesion.getUsuarioActual().getSucursales()
                        .stream()
                        .filter(s -> s.getEmpresa() != null && s.getEmpresa().getEmpresaId() == seleccionada.getEmpresaId())
                        .collect(Collectors.toList());

                comboSucursal.setItems(FXCollections.observableArrayList(sucursales));
                if (!comboSucursal.getItems().isEmpty()) {
                    comboSucursal.getSelectionModel().selectFirst();
                }
            }

            // Listener para cuando cambie la empresa
            comboEmpresa.setOnAction(e -> {
                Empresa nuevaEmpresa = comboEmpresa.getValue();
                if (nuevaEmpresa != null) {
                    List<Sucursal> sucursales = Sesion.getUsuarioActual().getSucursales()
                            .stream()
                            .filter(s -> s.getEmpresa() != null && s.getEmpresa().getEmpresaId() == nuevaEmpresa.getEmpresaId())
                            .collect(Collectors.toList());

                    comboSucursal.setItems(FXCollections.observableArrayList(sucursales));
                    if (!comboSucursal.getItems().isEmpty()) {
                        comboSucursal.getSelectionModel().selectFirst();
                    } else {
                        comboSucursal.getSelectionModel().clearSelection();
                    }
                } else {
                    comboSucursal.getItems().clear();
                    comboSucursal.getSelectionModel().clearSelection();
                }
            });
        }
    }

    @FXML
    private void onAceptar() {
        Empresa empresa = comboEmpresa.getValue();
        Sucursal sucursal = comboSucursal.getValue();

        if (empresa == null || sucursal == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Faltan selecciones");
            alert.setContentText("Debe seleccionar una empresa y una sucursal.");
            alert.showAndWait();
            return;
        }

        // Guardar en la sesión global
        Sesion.setEmpresaSeleccionada(empresa);
        Sesion.setSucursalSeleccionada(sucursal);

        // Cerrar la ventana
        Stage stage = (Stage) comboEmpresa.getScene().getWindow();
        stage.close();
    }
}
