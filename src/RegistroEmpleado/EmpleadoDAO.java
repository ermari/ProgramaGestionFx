package RegistroEmpleado;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.UtilControllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpleadoDAO {

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


    //metodo elminar
    public void eliminarEmpleado(Empleado emp) {
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

    public void insertar(Empleado empleado) {
        String sql = "INSERT INTO empleado (firstName, email, department, salary) VALUES (?, ?, ?, ?)";

        try {
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
    public void modificar(Empleado empleado) {
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

    // --- NUEVO MÉTODO: Listar empleados con filtro de búsqueda ---
    public ObservableList<Empleado> listarEmpleadosFiltro(String searchTerm, String filter) throws SQLException {
        ObservableList<Empleado> empleados = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Empleado";

        boolean hasSearch = searchTerm != null && !searchTerm.trim().isEmpty();

        if (hasSearch) {
            switch (filter) {
                case "All":
                    sql += " WHERE firstName LIKE ? OR email LIKE ? OR department LIKE ? OR CAST(salary AS CHAR) LIKE ? OR CAST(empID AS CHAR) LIKE ?";
                    break;
                case "firstName":
                    sql += " WHERE firstName LIKE ?";
                    break;
                case "email":
                    sql += " WHERE email LIKE ?";
                    break;
                case "department":
                    sql += " WHERE department LIKE ?";
                    break;
                case "salary":
                    sql += " WHERE CAST(salary AS CHAR) LIKE ?";
                    break;
                case "empID":
                    sql += " WHERE CAST(empID AS CHAR) LIKE ?";
                    break;
                default:
                    sql += " WHERE firstName LIKE ?";
                    break;
            }
        }

        try {

            PreparedStatement pst = BD.BDconexion.getInstance().getConnection().prepareStatement(sql);

            if (hasSearch) {
                String term = "%" + searchTerm + "%";
                switch (filter) {
                    case "All":
                        pst.setString(1, term);
                        pst.setString(2, term);
                        pst.setString(3, term);
                        pst.setString(4, term);
                        pst.setString(5, term);
                        break;
                    default:
                        pst.setString(1, term);
                        break;
                }
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Empleado emp = new Empleado();
                emp.setEmpID(rs.getInt("empID"));
                emp.setFirstName(rs.getString("firstName"));
                emp.setEmail(rs.getString("email"));
                emp.setDepartment(rs.getString("department"));
                emp.setSalary(rs.getDouble("salary"));
                empleados.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return empleados;
    }

}





