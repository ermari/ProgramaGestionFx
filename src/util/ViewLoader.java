package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class ViewLoader {

    /**
     * Carga un FXML en el AnchorPane dado, y le aplica una acción al controlador si es necesario.
     *
     * @param fxmlPath   Ruta del archivo FXML (ej: "/Home/Dashboard.fxml").
     * @param targetPane Panel donde se va a insertar la vista.
     * @param controllerHandler Acción opcional que recibe el controlador.
     */
    public static <T> void loadView(String fxmlPath, AnchorPane targetPane, Consumer<T> controllerHandler) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ViewLoader.class.getResource(fxmlPath)));
            AnchorPane view = loader.load();

            // Pasar el controlador al consumidor
            if (controllerHandler != null) {
                T controller = loader.getController();
                controllerHandler.accept(controller);
            }

            // Cargar la vista en el contenedor
            targetPane.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
