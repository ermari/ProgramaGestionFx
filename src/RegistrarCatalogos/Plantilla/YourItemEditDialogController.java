package RegistrarCatalogos.Plantilla;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class YourItemEditDialogController {
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    // @FXML private TextField priceField; // Add more fields

    private Stage dialogStage;
    private YourItem item;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Any initial setup for the dialog fields
    }

    /**
     * Sets the stage of this dialog.
     * @param dialogStage The stage of the dialog
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the item to be edited in the dialog.
     * @param item The item to edit or null for a new item
     */
    public void setItem(YourItem item) {
        this.item = item;

        if (item != null) {
            idField.setText(String.valueOf(item.getId()));
            nameField.setText(item.getName());
            // priceField.setText(String.valueOf(item.getPrice())); // Set other fields

            idField.setEditable(false); // ID is not editable when modifying
        } else {
            // For new items, ID is editable and fields are empty
            idField.setEditable(true);
            idField.setText("");
            nameField.setText("");
            // priceField.setText("");
        }
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * @return true if OK was clicked
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks OK.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            // If it's a new item, ID is taken from the field
            // If it's an existing item, ID is already set in the 'item' object and cannot be changed
            if (item == null) { // This means it's a new item
                item = new YourItem(Integer.parseInt(idField.getText()), nameField.getText());
                // Add other properties here for the new item
            } else { // This means it's an existing item
                // Update existing item's properties
                item.setName(nameField.getText());
                // item.setPrice(Double.parseDouble(priceField.getText())); // Update other properties
            }

            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks Cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (idField.getText() == null || idField.getText().isEmpty()) {
            errorMessage += "No se ha ingresado un ID!\n";
        } else {
            try {
                // Only parse ID if it's editable (for new items)
                if (idField.isEditable()) {
                    Integer.parseInt(idField.getText());
                }
            } catch (NumberFormatException e) {
                errorMessage += "El ID debe ser un número entero válido!\n";
            }
        }

        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "No se ha ingresado un Nombre!\n";
        }
        // Add validation for other fields
        /*
        if (priceField.getText() == null || priceField.getText().isEmpty()) {
            errorMessage += "No se ha ingresado un Precio!\n";
        } else {
            try {
                Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "El Precio debe ser un número válido!\n";
            }
        }
        */

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Campos Inválidos");
            alert.setHeaderText("Por favor, corrige los campos inválidos:");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}