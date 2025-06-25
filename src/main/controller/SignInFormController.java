package main.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;

import javafx.stage.Stage;
import main.model.Account;
import main.model.AccountsDAO;
import static main.model.ConnectionPoolMySQL.EXCEPCIONES;

import util.UtilControllers;

/**
 * FXML HomeController class
 *
 * @author JorgeLPR
 */
public class SignInFormController implements Initializable {
    
    @FXML
    private TextField txtUserSignIn, txtPasswordSignInMask;
    
    @FXML
    private PasswordField txtPasswordSignIn;
    
    @FXML
    private CheckBox checkViewPassSignIn;
    
    @FXML
    private Button btnSignIn, btnClean;
    
    private Account account;
    private AccountsDAO modelUser = new AccountsDAO();
        
    public void cleanFields(){
        txtPasswordSignIn.setText("");
        txtPasswordSignInMask.setText("");
        txtUserSignIn.setText("");        
    }
    
    @FXML
    public void eventKey(KeyEvent e){
        
        String c = e.getCharacter();
        
        if(c.equalsIgnoreCase(" ")){
            e.consume();
        }
        
    }
    
    @FXML
    public void actionEvent(ActionEvent e){
        
        Object evt = e.getSource();
        
        if(btnSignIn.equals(evt)){                    
                         
            if(!txtUserSignIn.getText().isEmpty() && !txtPasswordSignIn.getText().isEmpty()){

                String filter;
                
                if(UtilControllers.validateEmail(txtUserSignIn.getText())){
                    filter = "email";
                }else{
                    filter = "user";                
                }
                
                account = modelUser.selectAccount(txtUserSignIn.getText(), filter);
                
                if(account != null){

                    if(account.getPassword().equals(txtPasswordSignIn.getText())){

                        CallIndex(e);

                    }else{
                        JOptionPane.showMessageDialog(null, "La Contraseña que ha ingresado no es la correcta", "ADVERTENCIA", JOptionPane.WARNING_MESSAGE);                                            
                    }

                }else{

                    if(EXCEPCIONES.size()>0){
                        JOptionPane.showMessageDialog(null, "Surgieron errores en el proceso de consulta, posibles errores:\n"+
                                                      UtilControllers.toString(EXCEPCIONES), "ERROR", JOptionPane.ERROR_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "El Usuario no existe en la Base de Datos", "SIN RESULTADOS", JOptionPane.ERROR_MESSAGE);
                    }

                }
            
            }else{
                JOptionPane.showMessageDialog(null, "Debe llenar los campos obligatorios", "ERROR", JOptionPane.ERROR_MESSAGE);
            }            
                        
        }else if(btnClean.equals(evt)){        
            cleanFields();
        }
    
    }

    @FXML
    private void CallIndex(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/Home.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage(); // o usa una referencia a primaryStage si ya la tienes
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Bienvenido");
            stage.setMaximized(true);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resource/images/index.jpeg")));
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Initializes the main.controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        maskPassword(txtPasswordSignIn, txtPasswordSignInMask, checkViewPassSignIn);
        
        /*
        txtUserSignIn.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {                
                if(event.getCharacter().equals(" ")){
                    event.consume();
                }                
            }
        });*/
        
    }    
    
    public void maskPassword(PasswordField pass, TextField text, CheckBox check){
    
        text.setVisible(false);
        text.setManaged(false);
            
        text.managedProperty().bind(check.selectedProperty());
        text.visibleProperty().bind(check.selectedProperty());
        
        text.textProperty().bindBidirectional(pass.textProperty());
    
    }
    
    
    
}
