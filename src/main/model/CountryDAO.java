/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static main.model.ConnectionPoolMySQL.EXCEPCIONES;
import util.UtilControllers;

/**
 *
 * @author JorgeLPR
 */
public class CountryDAO {
    
    public ArrayList<Country> selectCountries(){
    
        EXCEPCIONES = new ArrayList<>();        
        
        Connection connection = null;
        PreparedStatement pst;
        ResultSet rs;
        
        Country country;
        ArrayList<Country> list = new ArrayList<>();
        
        try{
        
            connection = ConnectionPoolMySQL.getInstance().getConnection();
            
            if(connection!=null){
            
                String sql = "SELECT id, country, city "
                        + "FROM countries "
                        + "ORDER BY country ASC";
                
                pst = connection.prepareStatement(sql);
                
                rs = pst.executeQuery();
                
                while(rs.next()){
                    country = new Country();
                    country.setId(rs.getInt("id"));
                    country.setCountry(rs.getString("country"));
                    country.setCity(rs.getString("city"));
                    list.add(country);
                }                
                
            }else{
                EXCEPCIONES.add(UtilControllers.enumSizeExcepcion(EXCEPCIONES)+"- "+"Error al conectar con la base de datos");
            }
            
            
        }catch(SQLException ex){
            EXCEPCIONES.add(UtilControllers.enumSizeExcepcion(EXCEPCIONES)+"- "+ex.getMessage());
        }finally{
            try{
                if(connection != null){
                    ConnectionPoolMySQL.getInstance().closeConnection(connection);            
                }            
            }catch(SQLException ex){
                EXCEPCIONES.add(UtilControllers.enumSizeExcepcion(EXCEPCIONES)+"- "+ex.getMessage());
            }        
        }
        
        System.out.println(EXCEPCIONES);
        
        return list;
    
    }
    
}
