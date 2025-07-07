package RegistroEmpleado;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import util.MensajeUtil;
import Constantes.constantes;
import util.UtilControllers;

public class EmpleadoController implements Initializable {

    @FXML private TextField filterField;
    @FXML private ComboBox<String> filterCombo;
    @FXML private TableView<Empleado> tableview;
    @FXML private TableColumn<Empleado, Integer> EmpID;
    @FXML private TableColumn<Empleado, String> empName;
    @FXML private TableColumn<Empleado, String> empEmail;
    @FXML private TableColumn<Empleado, String> department;
    @FXML private TableColumn<Empleado, Double> salary;
    @FXML private TableColumn<Empleado, Void> actionsColumn;
    @FXML private Pagination pagination;

    private static final int ITEMS_PER_PAGE = 15;
    private EmpleadoDao empleadoDao = new EmpleadoDao();
    private ObservableList<Empleado> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar combo de filtros
        filterCombo.setItems(FXCollections.observableArrayList("All", "firstName", "email", "department", "salary", "empID"));
        filterCombo.getSelectionModel().select("All");

        // Configurar columnas
        EmpID.setCellValueFactory(new PropertyValueFactory<>("empID"));
        empName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        empEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
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

        actionsColumn.setStyle("-fx-alignment: CENTER;");
        addActionButtonsToTable();

        // Cargar todo al inicio
        cargarEmpleado(null, "All");

        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);

    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Empleado, Void>, TableCell<Empleado, Void>> cellFactory = param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Button menuButton = new Button();
                    ContextMenu contextMenu = new ContextMenu();

                    ImageView kebabIcon = new ImageView(safeLoadImage("/resources/images/menu8.png"));
                    kebabIcon.setFitWidth(18);
                    kebabIcon.setFitHeight(18);
                    menuButton.setGraphic(kebabIcon);
                    menuButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                    MenuItem editItem = new MenuItem("Modificar");
                    editItem.setGraphic(resizeIcon("/resources/icons/edit.png"));
                    editItem.setOnAction(e -> {
                        Empleado emp = getTableView().getItems().get(getIndex());
                        if (emp != null) {
                            try {
                                modificarEmpleado(emp);
                            } catch (IOException ex) {
                                UtilControllers.mostrarError("Error al cargar el formulario de modificación.", ex);
                            }
                        }
                    });

                    MenuItem deleteItem = new MenuItem("Eliminar");
                    deleteItem.setGraphic(resizeIcon("/resources/icons/delete.png"));
                    deleteItem.setOnAction(e -> {
                        Empleado emp = getTableView().getItems().get(getIndex());
                        if (emp != null) {
                            eliminarEmpleadoDeBD(emp);
                        }
                    });

                    contextMenu.getItems().addAll(editItem, deleteItem);

                    menuButton.setOnAction(event -> {
                        if (!isEmpty() && getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                            contextMenu.show(menuButton, Side.BOTTOM, 0, 0);
                        }
                    });

                    setGraphic(menuButton);
                    setText(null);
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
            return new Image("https://via.placeholder.com/18.png");
        }
    }

    private ImageView resizeIcon(String path) {
        ImageView icon = new ImageView(safeLoadImage(path));
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        return icon;
    }

    @FXML
    private void refrescarTabla() {
        String searchTerm = filterField.getText();
        String filter = filterCombo.getValue();
        if (filter == null) {
            filter = "All";
        }
        cargarEmpleado(searchTerm, filter);
        pagination.setCurrentPageIndex(0);
        filterCombo.getSelectionModel().select("All");
    }

    public void cargarEmpleado(String searchTerm, String filter) {
        try {
            if ((filter == null || filter.equals("All")) ) {
                // Mostrar todos
                masterData.setAll(empleadoDao.listarEmpleado());
            } else {
                // Filtrar
                masterData.setAll(empleadoDao.listarEmpleadosFiltro(searchTerm, filter));
            }
            updatePagination();
            if (pagination.getCurrentPageIndex() >= pagination.getPageCount()) {
                pagination.setCurrentPageIndex(pagination.getPageCount() - 1);
            }
            if (pagination.getCurrentPageIndex() < 0 && pagination.getPageCount() > 0)

           tableview.setItems(masterData);
        } catch (SQLException e) {
            e.printStackTrace();
            UtilControllers.mostrarError("Error de base de datos al cargar empleados.", e);
            masterData.clear();
            updatePagination();
        }
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, masterData.size());
        if (fromIndex > toIndex || fromIndex < 0) {
            tableview.setItems(FXCollections.observableArrayList());
            return new VBox();
        }
        ObservableList<Empleado> pageItems = FXCollections.observableArrayList(masterData.subList(fromIndex, toIndex));
        tableview.setItems(pageItems);
        return new VBox();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) masterData.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
    }

    @FXML
    private void agregarEmpleado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroEmpleado/DetalleEmpleado.fxml"));
            Parent root = loader.load();

            DetalleEmpleadoController controller = loader.getController();
            controller.setEmpleado(new Empleado());
            controller.setEmpleadoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Agregar Empleado");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarEmpleado(filterField.getText(), filterCombo.getValue());

        } catch (IOException e) {
            e.printStackTrace();
            UtilControllers.mostrarError("No se pudo cargar el formulario para agregar empleado.", e);
        }
    }

    private void modificarEmpleado(Empleado empleadoToEdit) throws IOException {
        if (empleadoToEdit == null) {
            MensajeUtil.mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", null, "No se ha seleccionado ningún empleado para modificar.");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroEmpleado/DetalleEmpleado.fxml"));
        Parent root = loader.load();

        DetalleEmpleadoController controller = loader.getController();
        controller.setEmpleado(empleadoToEdit);
        controller.setEmpleadoController(this);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Editar Empleado: " + empleadoToEdit.getFirstName());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarEmpleado(filterField.getText(), filterCombo.getValue());
    }

    private void eliminarEmpleadoDeBD(Empleado emp) {
        if (emp == null) {
            MensajeUtil.mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", null, "No se ha seleccionado ningún empleado para eliminar.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("Eliminar Empleado");
        confirmAlert.setContentText("¿Está seguro de que desea eliminar a " + emp.getFirstName() + " (ID: " + emp.getEmpID() + ")?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                empleadoDao.eliminarEmpleado(emp);
                MensajeUtil.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", constantes.MENSAJE_BORRADO, null);
                cargarEmpleado(filterField.getText(), filterCombo.getValue());
            }
        });
    }
}
