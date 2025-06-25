package util;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 *
 * @author JorgeLPR
 */
public class UtilControllers {
    
    public static int enumSizeExcepcion(ArrayList<String>list){
        return list.size()+1;
    }
    
    public static String toString(ArrayList<String> datos){
        String text="";
        for (int i = 0; i < datos.size(); i++) {
            text=text+datos.get(i)+"\n";
        }
        return text;
    }    

    public static boolean validateEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);                
    }

    public static void mostrarError(String mensaje, Exception excepcion) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Error");
        alert.setHeaderText("Ha ocurrido un error");
        alert.setContentText(mensaje);

        // Convertir la excepción a texto
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        excepcion.printStackTrace(pw);
        String exceptionText = sw.toString();

        // Crear el área de texto para detalles
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
        alert.showAndWait();
    }

    public static void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UNDECORATED); // sin borde del sistema
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(UtilControllers.class.getResource("/util/css/alerta.css").toExternalForm());
        dialogPane.getStyleClass().add("alerta-exito");

        alert.showAndWait();
    }


}
