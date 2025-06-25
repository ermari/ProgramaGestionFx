package util;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MensajeUtil {

    public static void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje, Exception ex) {
        Alert alert = new Alert(tipo);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        DialogPane dialogPane = alert.getDialogPane();

        // Estilo y gráfico según tipo
        switch (tipo) {
            case INFORMATION:
                alert.setGraphic(cargarIcono("/resource/icons/check.png"));
                dialogPane.getStylesheets().add(MensajeUtil.class.getResource("/util/css/estilos-alerta.css").toExternalForm());
                dialogPane.getStyleClass().add("alerta-exito");
                break;
            case ERROR:
                alert.setGraphic(cargarIcono("/resource/icons/error.png"));
                dialogPane.getStylesheets().add(MensajeUtil.class.getResource("/util/css/estilos-alerta.css").toExternalForm());
                dialogPane.getStyleClass().add("alerta-error");

                if (ex != null) {
                    // Mostrar detalles técnicos expandibles
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    String exceptionText = sw.toString();

                    Label label = new Label("Detalles técnicos:");
                    TextArea textArea = new TextArea(exceptionText);
                    textArea.setEditable(false);
                    textArea.setWrapText(true);
                    textArea.setMaxWidth(Double.MAX_VALUE);
                    textArea.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setVgrow(textArea, Priority.ALWAYS);
                    GridPane.setHgrow(textArea, Priority.ALWAYS);

                    GridPane expContent = new GridPane();
                    expContent.setMaxWidth(Double.MAX_VALUE);
                    expContent.add(label, 0, 0);
                    expContent.add(textArea, 0, 1);

                    alert.getDialogPane().setExpandableContent(expContent);
                }
                break;
            case WARNING:
                alert.setGraphic(cargarIcono("/resource/icons/warning.png"));
                dialogPane.getStylesheets().add(MensajeUtil.class.getResource("/util/css/estilos-alerta.css").toExternalForm());
                dialogPane.getStyleClass().add("alerta-warning");
                break;
            case CONFIRMATION:
                alert.setGraphic(cargarIcono("/resource/icons/question.png"));
                dialogPane.getStylesheets().add(MensajeUtil.class.getResource("/util/css/estilos-alerta.css").toExternalForm());
                dialogPane.getStyleClass().add("alerta-confirmacion");
                break;
            default:
                // info genérica
                break;
        }

        alert.showAndWait();
    }

    private static ImageView cargarIcono(String ruta) {
        try {
            ImageView icon = new ImageView(new Image(MensajeUtil.class.getResourceAsStream(ruta)));
            icon.setFitWidth(32);
            icon.setFitHeight(32);
            return icon;
        } catch (Exception e) {
            return null;
        }
    }
}
