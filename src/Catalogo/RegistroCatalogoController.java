package Catalogo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class RegistroCatalogoController {

    @FXML private TextField codigoField;
    @FXML private TextField valorField;
    @FXML private TextField descripcionField;
    @FXML private TextField ordenField;
    @FXML private ComboBox<Catalogo> comboSuperior;


    private Catalogo catalogo;
    private CatalogoController catalogoController;

    @FXML
    private void initialize() throws SQLException {
        CatalogoDAO dao = new CatalogoDAO();
        List<Catalogo> lista = dao.obtenerTodos();
        comboSuperior.setItems(FXCollections.observableArrayList(lista));
        comboSuperior.setDisable(true); // opcional
    }

    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;

        if (catalogo != null) {
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
        catalogo.setOrden(Integer.parseInt(ordenField.getText()));

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
