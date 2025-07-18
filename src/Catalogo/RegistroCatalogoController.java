package Catalogo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
<<<<<<< HEAD
import javafx.scene.control.*;
=======
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class RegistroCatalogoController {

    @FXML private TextField codigoField;
    @FXML private TextField valorField;
    @FXML private TextArea descripcionField;
    @FXML private TextField ordenField;
    @FXML private ComboBox<Catalogo> comboSuperior;
    @FXML private ToggleButton toggleExpandible;
<<<<<<< HEAD
    @FXML private TextField codigoPadreField;
    @FXML private Label tituloId;

=======
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f

    @FXML
    private TextField nivelField;

    private Catalogo catalogo;
    private CatalogoController catalogoController;

    @FXML
    private void initialize() throws SQLException {
        CatalogoDAO dao = new CatalogoDAO();
        List<Catalogo> lista = dao.obtenerTodos();
        comboSuperior.setItems(FXCollections.observableArrayList(lista));

<<<<<<< HEAD

=======
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
        // Inicializar estilo visual
        toggleExpandible.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarToggleEstilo(newVal));

        // Estado inicial visual
        toggleExpandible.setSelected(true);
        actualizarToggleEstilo(true);
<<<<<<< HEAD

=======
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
    }

    private void actualizarToggleEstilo(boolean isSelected) {
        if (isSelected) {
<<<<<<< HEAD
           // toggleExpandible.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            toggleExpandible.setText("SI");
        } else {
           // toggleExpandible.setStyle("-fx-background-color: lightgray; -fx-text-fill: black;");
=======
            toggleExpandible.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            toggleExpandible.setText("SI");
        } else {
            toggleExpandible.setStyle("-fx-background-color: lightgray; -fx-text-fill: black;");
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
            toggleExpandible.setText("NO");
        }
    }

    //SET CATALOGO
    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;

<<<<<<< HEAD
        if (catalogo != null && catalogo.getCatalogoId() != 0 ) {
=======
        if (catalogo != null && catalogo.getCatalogoId() != 0) {
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
            // EDITAR
            codigoField.setText(catalogo.getCodigo());
            valorField.setText(catalogo.getValor());
            descripcionField.setText(catalogo.getDescripcion());
            ordenField.setText(String.valueOf(catalogo.getOrden()));
            nivelField.setText(String.valueOf(catalogo.getNivel()));
            codigoPadreField.setText(catalogo.getCodigoPadre());


          //  comboSuperior.setItems(FXCollections.observableArrayList(listaCatalogos)); // Asegúrate de esto primero

            if (catalogo.getCatalogoSup() != null) {
                for (Catalogo item : comboSuperior.getItems()) {
                    if (Integer.valueOf(item.getCatalogoId()).equals(catalogo.getCatalogoSup())) {
                        comboSuperior.setValue(item);
                        tituloId.setText("REGISTRO DE CUENTA || Cuenta Padre " + item.getCodigo() + "  " + item.getValor());
                        comboSuperior.setVisible(false);
                        break;
                    }
                }
            }

<<<<<<< HEAD

=======
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
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
<<<<<<< HEAD
                ordenField.setDisable(true);
=======
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
            } else {
                ordenField.setDisable(false);
                toggleExpandible.setDisable(false);
                comboSuperior.setDisable(true); // 🔥 Para edición normal: no cambiar combo
            }

        } else {
            // NUEVO (cuando se crea hijo o hermano se pasa un objeto con catalogoSup ya definido)
<<<<<<< HEAD
            ordenField.textProperty().addListener((obs, oldText, newText) -> {
                String codigoPadre = codigoPadreField.getText().trim();

                if (!codigoPadre.isEmpty() && !newText.isEmpty()) {
                    try {
                        int orden = Integer.parseInt(newText);
                        String codigo = codigoPadre + "." + String.format("%03d", orden);
                        codigoField.setText(codigo);
                    } catch (NumberFormatException e) {
                        // Ignorar si no es un número válido (por ejemplo, si el usuario escribió letras)
                        codigoField.setText("");
                    }
                } else {
                    codigoField.setText("");
                }
            });





=======
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
            ordenField.setText("0");
            toggleExpandible.setSelected(true);

            if (catalogo.getCatalogoSup() != null) {
                // Si viene con catalogoSup ➜ hijo o hermano
                for (Catalogo item : comboSuperior.getItems()) {
                    if (item.getCatalogoId() == catalogo.getCatalogoSup()) {
                        comboSuperior.setValue(item);
<<<<<<< HEAD
                        tituloId.setText("REGISTRO DE CUENTA || Cuenta Padre  " + item.getCodigoPadre() + "  " + item.getValor());
                        codigoPadreField.setText( item.getCodigo());
                        nivelField.setText(String.valueOf(item.getNivel() + 1));










=======
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
                        break;
                    }
                }

                ordenField.setDisable(false);
                toggleExpandible.setDisable(false);
<<<<<<< HEAD
                comboSuperior.setDisable(false);
                comboSuperior.setVisible(false);
                // 🔥 Para hijo y hermano: combo seleccionado y bloqueado
=======
                comboSuperior.setDisable(true); // 🔥 Para hijo y hermano: combo seleccionado y bloqueado
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f
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
        catalogo.setCodigoPadre(codigoPadreField.getText());
        catalogo.setValor(valorField.getText());
        catalogo.setDescripcion(descripcionField.getText());
<<<<<<< HEAD
        catalogo.setNivel(Integer.parseInt(nivelField.getText()));
=======
>>>>>>> 2016b4674c35de2756fc7be326de76702f19985f

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
