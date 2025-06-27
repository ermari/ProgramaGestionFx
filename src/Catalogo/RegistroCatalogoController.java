package Catalogo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class RegistroCatalogoController {

    @FXML private TextField codigoField;
    @FXML private TextField valorField;
    @FXML private TextField descripcionField;
    @FXML private TextField ordenField;
    @FXML private ComboBox<Catalogo> comboSuperior;
    @FXML private ToggleButton toggleExpandible;

    private Catalogo catalogo;
    private CatalogoController catalogoController;

    @FXML
    private void initialize() throws SQLException {
        CatalogoDAO dao = new CatalogoDAO();
        List<Catalogo> lista = dao.obtenerTodos();
        comboSuperior.setItems(FXCollections.observableArrayList(lista));

        // Inicializar estilo visual
        toggleExpandible.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarToggleEstilo(newVal));

        // Estado inicial visual
        toggleExpandible.setSelected(true);
        actualizarToggleEstilo(true);
    }

    private void actualizarToggleEstilo(boolean isSelected) {
        if (isSelected) {
            toggleExpandible.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            toggleExpandible.setText("SI");
        } else {
            toggleExpandible.setStyle("-fx-background-color: lightgray; -fx-text-fill: black;");
            toggleExpandible.setText("NO");
        }
    }

    //SET CATALOGO
    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;

        if (catalogo != null && catalogo.getCatalogoId() != 0) {
            // EDITAR
            codigoField.setText(catalogo.getCodigo());
            valorField.setText(catalogo.getValor());
            descripcionField.setText(catalogo.getDescripcion());
            ordenField.setText(String.valueOf(catalogo.getOrden()));

            if (catalogo.getCatalogoSup() != null) {
                for (Catalogo item : comboSuperior.getItems()) {
                    if (item.getCatalogoId() == catalogo.getCatalogoSup()) {
                        comboSuperior.setValue(item);
                        break;
                    }
                }
            }

            if ("S".equals(catalogo.getExpandible())) {
                toggleExpandible.setSelected(true);
            } else {
                toggleExpandible.setSelected(false);
            }

            if (catalogo.getCatalogoSup() == null) {
                // Editando raíz
                ordenField.setText("0");
                ordenField.setDisable(true);
                toggleExpandible.setDisable(true);
                comboSuperior.setDisable(true);
            } else {
                ordenField.setDisable(false);
                toggleExpandible.setDisable(false);
                comboSuperior.setDisable(true); // 🔥 Para edición normal: no cambiar combo
            }

        } else {
            // NUEVO (cuando se crea hijo o hermano se pasa un objeto con catalogoSup ya definido)
            ordenField.setText("0");
            toggleExpandible.setSelected(true);

            if (catalogo.getCatalogoSup() != null) {
                // Si viene con catalogoSup ➜ hijo o hermano
                for (Catalogo item : comboSuperior.getItems()) {
                    if (item.getCatalogoId() == catalogo.getCatalogoSup()) {
                        comboSuperior.setValue(item);
                        break;
                    }
                }

                ordenField.setDisable(false);
                toggleExpandible.setDisable(false);
                comboSuperior.setDisable(true); // 🔥 Para hijo y hermano: combo seleccionado y bloqueado
            } else {
                // Nuevo padre
                ordenField.setDisable(true);
                toggleExpandible.setDisable(true);
                comboSuperior.setDisable(true);
            }

            if (this.catalogo != null) {
                this.catalogo.setExpandible("S");
            }
        }
    }

    @FXML
    private void guardarCatalogo() throws SQLException {
        if (catalogo == null) {
            catalogo = new Catalogo();
        }

        catalogo.setCodigo(codigoField.getText());
        catalogo.setValor(valorField.getText());
        catalogo.setDescripcion(descripcionField.getText());

        try {
            catalogo.setOrden(Integer.parseInt(ordenField.getText()));
        } catch (NumberFormatException e) {
            catalogo.setOrden(0);
        }

        // Aquí el punto crítico: se toma el valor actual del toggle
       // catalogo.setExpandible(toggleExpandible.isSelected() ? "S" : "N");

        catalogo.setOrden(ordenField.isDisabled() ? 0 : Integer.parseInt(ordenField.getText()));
        catalogo.setExpandible(toggleExpandible.isSelected() ? "S" : "N");


        Catalogo seleccion = comboSuperior.getValue();
        catalogo.setCatalogoSup(seleccion != null ? seleccion.getCatalogoId() : null);

        CatalogoDAO dao = new CatalogoDAO();
        if (catalogo.getCatalogoId() == 0) {
            dao.insertar(catalogo);
        } else {
            dao.actualizar(catalogo);
        }

        if (catalogoController != null) {
            catalogoController.refrescar();
        }

        ((Stage) codigoField.getScene().getWindow()).close();
    }

    public void setCatalogoController(CatalogoController controller) {
        this.catalogoController = controller;
    }
}
