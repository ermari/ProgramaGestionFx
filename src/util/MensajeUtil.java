package util;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;

import java.io.PrintWriter; // ¡Importa esta clase!
import java.io.StringWriter;

public class MensajeUtil {

    public static void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje, String ex) {
        Alert alert = new Alert(tipo);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        DialogPane dialogPane = alert.getDialogPane();

        // Estilo y gráfico según tipo
        switch (tipo) {
            case INFORMATION:
                alert.setGraphic(cargarIcono("/resources/icons/check.png"));
                dialogPane.getStylesheets().add(MensajeUtil.class.getResource("/resources/css/estilos-alerta.css").toExternalForm());
                dialogPane.getStyleClass().add("alerta-exito");
                break;
            case ERROR:
                alert.setGraphic(cargarIcono("/resources/icons/error.png"));
                dialogPane.getStylesheets().add(MensajeUtil.class.getResource("/resources/css/estilos-alerta.css").toExternalForm());
                dialogPane.getStyleClass().add("alerta-error");

                // Añadí la comprobación !ex.trim().isEmpty() para que no muestre la sección expandible si 'ex' solo tiene espacios
                if (ex != null && !ex.trim().isEmpty()) {
                    // Mostrar detalles técnicos expandibles
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw); // Crea un PrintWriter para escribir en el StringWriter
                    pw.print(ex); // ¡Escribe tu mensaje de error (ex) en el StringWriter!
                    pw.close(); // Cierra el PrintWriter para asegurar que todo se haya escrito

                    String exceptionText = sw.toString(); // ¡Ahora sí, esto contendrá tu mensaje de error!

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
                alert.setGraphic(cargarIcono("/resources/icons/warning.png"));
                dialogPane.getStylesheets().add(MensajeUtil.class.getResource("/resources/css/estilos-alerta.css").toExternalForm());
                dialogPane.getStyleClass().add("alerta-warning");
                break;
            case CONFIRMATION:
                alert.setGraphic(cargarIcono("/resources/icons/question.png"));
                dialogPane.getStylesheets().add(MensajeUtil.class.getResource("/resources/css/estilos-alerta.css").toExternalForm());
                dialogPane.getStyleClass().add("alerta-confirmacion");
                break;
            default:
                // info genérica
                break;
        }

        alert.showAndWait();
    }

    // Este es el método modificado para cargar los íconos con un tamaño específico
    private static ImageView cargarIcono(String ruta) {
        try {
            Image image = new Image(MensajeUtil.class.getResourceAsStream(ruta));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(32);   // Ajusta el ancho a 32 píxeles
            imageView.setFitHeight(32); // Ajusta el alto a 32 píxeles
            return imageView;
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: " + ruta + " - " + e.getMessage());
            return null; // Si no se encuentra la imagen, devuelve null
        }
    }

    public static void mostrarInformacion(String s) {
        // Implementa este método si planeas usarlo para mensajes de información simples
        mostrarAlerta(Alert.AlertType.INFORMATION, "Información", s, null);
    }
}