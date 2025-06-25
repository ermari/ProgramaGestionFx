package RegistroEmpleado;

import javafx.beans.property.*;

public class Empleado {

    private IntegerProperty empID;
    private StringProperty firstName;
    private StringProperty email;
    private StringProperty department;
    private DoubleProperty salary;

    // Constructor por defecto
    public Empleado() {
        this.empID = new SimpleIntegerProperty();
        this.firstName = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.department = new SimpleStringProperty();
        this.salary = new SimpleDoubleProperty();
    }

    // Constructor con parámetros
    public Empleado(int empID, String firstName, String email, String department, double salary) {
        this.empID = new SimpleIntegerProperty(empID);
        this.firstName = new SimpleStringProperty(firstName);
        this.email = new SimpleStringProperty(email);
        this.department = new SimpleStringProperty(department);
        this.salary = new SimpleDoubleProperty(salary);
    }

    // Getters y Setters
    public int getEmpID() {
        return empID.get();
    }

    public void setEmpID(int empID) {
        this.empID.set(empID);
    }

    public IntegerProperty empIDProperty() {
        return empID;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public double getSalary() {
        return salary.get();
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
    }

    public DoubleProperty salaryProperty() {
        return salary;
    }
}
