package RegistroEmpleado;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Home.HomeController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.MensajeUtil;
import util.UtilControllers;
import Constantes.constantes;


public class EmpleadoController implements Initializable {

    @FXML
    private TextField filterField;
    @FXML
    private TableView<Empleado> tableview;
    @FXML
    private TableColumn<Empleado, String> EmpID;
    @FXML
    private TableColumn<Empleado, String> empName;
    @FXML
    private TableColumn<Empleado, String> empEmail;
    @FXML
    private TableColumn<Empleado, String> department;
    @FXML
    private TableColumn<Empleado, Double> salary;
    @FXML
    private TableColumn<Empleado, Void> actionsColumn;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<Empleado> dataList = FXCollections.observableArrayList();

    private Stage stage;

    private EmpleadoDao empleadoDao = new EmpleadoDao();


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        EmpID.setCellValueFactory(new PropertyValueFactory<>("EmpID"));
        empName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        empEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        salary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        salary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salary.setCellFactory(column -> new TableCell<Empleado, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f", item));
                }
                setStyle("-fx-alignment: CENTER-RIGHT;");
            }
        });
        addActionButtonsToTable();

        dataList=cargarTabla();


        FilteredList<Empleado> filteredData = new FilteredList<>(dataList, b -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return employee.getFirstName().toLowerCase().contains(lowerCaseFilter)
                        || employee.getDepartment().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(employee.getSalary()).contains(lowerCaseFilter);
            });
        });

        SortedList<Empleado> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableview.comparatorProperty());
        tableview.setItems(sortedData);
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Empleado, Void>,
                TableCell<Empleado, Void>> cellFactory = param -> new TableCell<>() {

            private final Button menuButton;

            {

                //salary.setCellValueFactory(new PropertyValueFactory<>("salary"));
                salary.setStyle("-fx-alignment: CENTER-RIGHT;");
                actionsColumn.setStyle("-fx-alignment: CENTER;");


                // Icono de menú kebab (tres puntos)
                ImageView kebabIcon = new ImageView(safeLoadImage("/resource/images/menu8.png"));
                kebabIcon.setFitWidth(18);
                kebabIcon.setFitHeight(18);

                menuButton = new Button();
                menuButton.setGraphic(kebabIcon);
                menuButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                // Menú contextual
                ContextMenu contextMenu = new ContextMenu();


                MenuItem editItem = new MenuItem("modificar");
                editItem.setGraphic(resizeIcon("/resource/icons/edit.png"));
                editItem.setOnAction(e -> {
                    Empleado emp = getTableView().getItems().get(getIndex());
                    modificarEmpleado();
                    System.out.println("Modificar clicked for: " + emp);
                });

                MenuItem deleteItem = new MenuItem("eliminar");
                deleteItem.setGraphic(resizeIcon("/resource/icons/delete.png"));
                deleteItem.setOnAction(e -> {
                    Empleado emp = getTableView().getItems().get(getIndex());
                    eliminarEmpleadoDeBD(emp);
                    System.out.println("Borrar clicked for: " + emp);
                });

                contextMenu.getItems().addAll(editItem, deleteItem);

                menuButton.setOnAction(event -> {
                    contextMenu.show(menuButton, Side.BOTTOM, 0, 0);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(menuButton);
                }
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }

    private Image safeLoadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + path);
            return new Image("https://via.placeholder.com/18"); // ícono de respaldo
        }
    }

    private ImageView resizeIcon(String path) {
        ImageView icon = new ImageView(safeLoadImage(path));
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        return icon;
    }
    
    

    //llamar a la base de datos
    public ObservableList<Empleado> cargarTabla() {
        ObservableList<Empleado> lista = FXCollections.observableArrayList();
        lista=empleadoDao.listarEmpleado();
        return lista;
    }

    public void cargarEmpleados() {

        System.out.println("Recargando tabla..."); // ⚠️ Verifica si aparece
        ObservableList<Empleado> lista = cargarTabla();
        tableview.setItems(lista);


    }


    @FXML
    private void agregarEmpleado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroEmpleado/DetalleEmpleado.fxml"));
            Parent root = loader.load();

            DetalleEmpleadoController controller = loader.getController();
            controller.setEmpleado(null); // nuevo
            controller.setEmpleadoController(this); // pasar referencia directa

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Agregar Empleado");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void modificarEmpleado() {
        Empleado seleccionado = tableview.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            System.out.println("Seleccione un empleado para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroEmpleado/DetalleEmpleado.fxml"));
            Parent root = loader.load();

            DetalleEmpleadoController controller = loader.getController();
            controller.setEmpleado(seleccionado); // <- pasar el objeto para editar
            controller.setEmpleadoController(this); // <- para refrescar tabla luego

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Empleado");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void eliminarEmpleadoDeBD(Empleado emp) {

        empleadoDao.eliminarEmpleado(emp);
        // Remover por ID, no por instancia
        dataList.removeIf(e -> e.getEmpID() == emp.getEmpID());

        MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", constantes.MENSAJE_BORRADO, null);
        dataList=cargarTabla();
        cargarEmpleados();
        //UtilContrcollers.mostrarExito("Empleado Eliminado con exito");
    }







}