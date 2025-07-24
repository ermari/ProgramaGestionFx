package Comprobantes.Controller;


import Catalogo.Catalogo;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;


// <S> es el tipo del objeto en la fila (DetalleComprobante)
// <T> es el tipo del objeto en la celda (Cuenta)
class ComboBoxTableCellWithValidation<S, T> extends TableCell<S, T> {

    private ComboBox<T> comboBox;
    private final ObservableList<T> items;

    public ComboBoxTableCellWithValidation(ObservableList<T> items) {
        this.items = items;
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createComboBox(); // Crea el ComboBox cada vez que se inicia la edición
            comboBox.valueProperty().set(getItem()); // Establece el valor actual
            setText(null);
            setGraphic(comboBox);
            comboBox.requestFocus();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem() != null ? getItem().toString() : "");
        setGraphic(null);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (comboBox != null) {
                    comboBox.valueProperty().set(getItem()); // Actualiza el valor del ComboBox si está editando
                }
                setText(null);
                setGraphic(comboBox);
            } else {
                setText(getItem() != null ? getItem().toString() : ""); // Muestra el texto cuando no está editando
                setGraphic(null);
            }
        }
    }

    private void createComboBox() {
        comboBox = new ComboBox<>(items);
        comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

        // Convertidor para mostrar el objeto Cuenta correctamente en el ComboBox
        comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (object instanceof Catalogo) {
                    Catalogo cuenta = (Catalogo) object;
                    return cuenta.getCodigo() + " - " + cuenta.getValor();
                }
                return object != null ? object.toString() : "";
            }

            @Override
            public T fromString(String string) {
                // No se usa para selección, solo para entrada si fuera un TextField editable
                return null;
            }
        });

        // Manejar la selección del ComboBox
        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isEditing()) {
                commitEdit(newVal); // Confirma la edición cuando se selecciona un valor
            }
        });

        // Para permitir salir de la edición al perder el foco (o Enter)
        comboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && comboBox != null) {
                commitEdit(comboBox.getValue());
            }
        });
    }
}