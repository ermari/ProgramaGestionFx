package CatalogoGestion.Periodo;

import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.MasterCatalogo.Modelo.MasterCatalogo;
import Home.HomeController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ListarPeriodoController {

    @FXML private TableView<Periodo> tablaPeriodos;
    @FXML private TableColumn<Periodo, String> colNombre;
    @FXML private TableColumn<Periodo, LocalDate> colFechaInicio;
    @FXML private TableColumn<Periodo, LocalDate> colFechaFin;
    @FXML private TableColumn<Periodo, Boolean> colEstado;

    private ObservableList<Periodo> periodosList = FXCollections.observableArrayList();

    //------------------------------------------------------------------------
    private HomeController homeController;
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
    //-------------------------------------------------------------------------
    @FXML
    private void salir(ActionEvent event) {
        if (homeController != null) {
            homeController.setForm("Dashboard.fxml");
        }
    }
//-----------------------------------------------------------------------------



    @FXML
    public void initialize() {
        // Enlazar las columnas a las propiedades del modelo
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Usar CellFactory para formatear las fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        colFechaInicio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaInicio()));
        colFechaInicio.setCellFactory(column -> new TableCell<Periodo, LocalDate>() {
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

        colFechaFin.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaFin()));
        colFechaFin.setCellFactory(column -> new TableCell<Periodo, LocalDate>() {
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

        // Manejar el estado como un CheckBox
        colEstado.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isEstado()));
        colEstado.setCellFactory(tc -> new TableCell<Periodo, Boolean>() {
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

        // Cargar datos de ejemplo
        cargarDatos();
        tablaPeriodos.setItems(periodosList);
    }

    private void cargarDatos() {
        periodosList.clear();
        String sql = "select * from periodo";

        try (PreparedStatement pstmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Get the date strings from the ResultSet
                String fechaInicioString = rs.getString("fecha_inicio");
                String fechaFinString = rs.getString("fecha_fin");
                Boolean estado= rs.getBoolean("estado");

                // Convert the strings to LocalDate
                LocalDate fechaInicio = (fechaInicioString != null) ? LocalDate.parse(fechaInicioString) : null;
                LocalDate fechaFin = (fechaFinString != null) ? LocalDate.parse(fechaFinString) : null;

                periodosList.add(new Periodo(
                        rs.getInt("periodo_id"),
                        rs.getString("nombre"),
                        fechaInicio,
                        fechaFin,
                        estado

                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        //    showAlert(Alert.AlertType.ERROR, "Error de BD", "No se pudieron cargar los Maestros", e.getMessage());
        }
    }

    @FXML
    private void nuevoPeriodo() {
        mostrarFormulario(null);
    }

    @FXML
    private void editarPeriodo() {
        Periodo seleccionado = tablaPeriodos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarFormulario(seleccionado);
        } else {
            mostrarAlerta("Error", "Debe seleccionar un período para editar.");
        }
    }

    @FXML
    private void eliminarPeriodo() {
        Periodo seleccionado = tablaPeriodos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("Eliminar período");
            alert.setContentText("¿Está seguro de que desea eliminar el período: " + seleccionado.getNombre() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                periodosList.remove(seleccionado);
                // Aquí iría la lógica para eliminar de la base de datos
            }
        } else {
            mostrarAlerta("Error", "Debe seleccionar un período para eliminar.");
        }
    }

    private void mostrarFormulario(Periodo periodo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogoGestion/Periodo/FormularioPeriodo.fxml"));
            Parent root = loader.load();

            FormularioPeriodoController controller = loader.getController();
            controller.setPeriodo(periodo); // Pasar el objeto al controlador
            controller.setOnGuardar(this::refrescarTabla);

            Stage stage = new Stage();
            stage.setTitle(periodo == null ? "Nuevo Período" : "Editar Período");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refrescarTabla() {
        cargarDatos();
        System.out.println("Tabla refrescada.");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }




}
