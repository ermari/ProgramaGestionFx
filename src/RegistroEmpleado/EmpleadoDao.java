package RegistroEmpleado;

import Constantes.constantes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import util.MensajeUtil;
import util.UtilControllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpleadoDao {

   //metodo listar todos
   public ObservableList<Empleado> listarEmpleado() {
       ObservableList<Empleado> lista = FXCollections.observableArrayList();
       try {
           String sql = "SELECT EmpID, firstName, email, department, salary FROM empleado";
           PreparedStatement preparedStatement = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
           ResultSet rs = preparedStatement.executeQuery();
           while (rs.next()) {
               int empID = rs.getInt("EmpID");
               String firstName = rs.getString("firstName");
               String email = rs.getString("email");
               String department = rs.getString("department");
               double salary = rs.getDouble("salary");

               lista.add(new Empleado(empID, firstName, email, department, salary)); // ← ya no lanza excepción
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return lista;
   }


    //metodo eliminar
    public void eliminarEmpleado (Empleado emp) {
        String sql = "DELETE FROM empleado WHERE EmpID = ?";
        try {
            PreparedStatement ps = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
            ps.setInt(1, emp.getEmpID());
            ps.executeUpdate();
            //UtilContrcollers.mostrarExito("Empleado Eliminado con exito");
        } catch (SQLException e) {
            UtilControllers.mostrarError("No se pudo eliminar el empleado", e);
        }
    }

    public void insertarEmpleado(Empleado empleado) {
        String sql = "INSERT INTO empleado (firstName, email, department, salary) VALUES (?, ?, ?, ?)";

        try  {
            PreparedStatement stmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
            stmt.setString(1, empleado.getFirstName());
            stmt.setString(2, empleado.getEmail());
            stmt.setString(3, empleado.getDepartment());
            stmt.setDouble(4, empleado.getSalary());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // puedes también lanzar una excepción si lo prefieres
        }
    }

    //modificar
    public void modificarEmpleado(Empleado empleado) {
        String sql = "UPDATE empleado SET firstName = ?, email = ?, department = ?, salary = ? WHERE EmpID = ?";

        try {
            PreparedStatement stmt = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);
            stmt.setString(1, empleado.getFirstName());
            stmt.setString(2, empleado.getEmail());
            stmt.setString(3, empleado.getDepartment());
            stmt.setDouble(4, empleado.getSalary());
            stmt.setInt(5, empleado.getEmpID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // o lanza una excepción personalizada si lo deseas
        }
    }
}


