/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import javax.swing.JOptionPane;
import main.model.Account;
import main.model.AccountsDAO;

import static main.model.ConnectionPoolMySQL.EXCEPCIONES;
import main.model.Country;
import main.model.CountryDAO;
import util.UtilControllers;

/**
 * FXML HomeController class
 *
 * @author JorgeLPR
 */
public class SignUpFormController implements Initializable {

    @FXML
    private TextField txtEmailSignUp, txtUserSignUp;
    
    @FXML
    private PasswordField txtPassword, txtConfirmPassword;
    
    @FXML
    private ComboBox<Country> cbCountry;
    
    @FXML
    private Button btnSignUp, btnCleanSignUp;
    
    private CountryDAO model;
    private ArrayList<Country> listCountries = new ArrayList<>();
    
    private AccountsDAO modelAccount = new AccountsDAO();
    
    public void cleanFields(){
        txtEmailSignUp.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtUserSignUp.setText("");
        if(cbCountry.getItems().size()>0){
            cbCountry.getSelectionModel().select(0);            
        }else{
            listCountries = model.selectCountries();
            fillCombobox(listCountries);        
        }                
    }
    
    @FXML
    public void keyEvent(KeyEvent e){
        
        String c = e.getCharacter();
        
        if(c.equalsIgnoreCase(" ")){
            e.consume();
        }
        
    }   
    
    @FXML
    public void actionEvent(ActionEvent e){
        
        Object evt = e.getSource();
        
        if(btnSignUp.equals(evt)){
        
            if(!txtEmailSignUp.getText().isEmpty() && !txtUserSignUp.getText().isEmpty() && 
               !txtConfirmPassword.getText().isEmpty() && !txtPassword.getText().isEmpty()){
                
                if(UtilControllers.validateEmail(txtEmailSignUp.getText())){
                    
                    if(txtUserSignUp.getText().length()>=3){

                        Country country = cbCountry.getSelectionModel().getSelectedItem();

                        if(country!=null){

                            if(txtConfirmPassword.getText().equals(txtPassword.getText())){

                                Account account = new Account();
                                account.setEmail(txtEmailSignUp.getText());
                                account.setPassword(txtPassword.getText());
                                account.setUser(txtUserSignUp.getText());
                                account.setCountry(country);

                                if(modelAccount.insertAccount(account)){

                                    JOptionPane.showMessageDialog(null, "El Usuario ha sido registrado de manera éxitosa", "OPERACIÓN ÉXITOSA", JOptionPane.INFORMATION_MESSAGE);
                                    cleanFields();

                                }else{

                                    if(EXCEPCIONES.size()>0){
                                        JOptionPane.showMessageDialog(null, "Surgieron errores en el proceso, posibles errores: \n"
                                                + UtilControllers.toString(EXCEPCIONES));
                                    }

                                }

                            }else{
                                JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden, por favor verifique e intente nuevamente", "ERROR", JOptionPane.ERROR_MESSAGE);
                            }

                        }else{
                            JOptionPane.showMessageDialog(null, "Surgieron errores al conectar con la Base de Datos", "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                        
                    }else{
                        JOptionPane.showMessageDialog(null, "El Nombre de usuario debe contener al menos TRES caracteres", "ERROR", JOptionPane.ERROR_MESSAGE);                                                                                                       
                    }
                    
                }else{
                    
                    JOptionPane.showMessageDialog(null, "El Email que ha ingresado no es valido", "ERROR", JOptionPane.ERROR_MESSAGE);                                    
                
                }
                                
                
            }else{
                JOptionPane.showMessageDialog(null, "Debe llenar todos los campos obligatorios", "ERROR", JOptionPane.ERROR_MESSAGE);
            }                        
                                
        }else if(btnCleanSignUp.equals(evt)){        
            cleanFields();        
        }
    
    }
    
    
    /**
     * Initializes the main.controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        model = new CountryDAO();
        listCountries = model.selectCountries();
        fillCombobox(listCountries);
    }    

    private void fillCombobox(ArrayList<Country> listCountries) {        
        if(listCountries.size() > 0){
            cbCountry.getItems().addAll(listCountries); 
            cbCountry.setConverter(new StringConverter<Country>() {
                @Override
                public String toString(Country object) {
                    return object.getCountry();
                }

                @Override
                public Country fromString(String string) {
                    return null;
                }
            });
            cbCountry.getSelectionModel().select(0);                        
        }
    }
    
}
