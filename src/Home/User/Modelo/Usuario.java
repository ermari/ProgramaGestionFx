package Home.User.Modelo;


import CatalogoGestion.Empresas.Modelo.Sucursal;

import javafx.beans.property.*;

public class Usuario {

    private final IntegerProperty usuarioId = new SimpleIntegerProperty();
    private final StringProperty nombreUsuario = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty usuario = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private Sucursal sucursal;

    public Usuario(int usuarioId, String nombre, String usuario, String sucursal) {
    }

    public Usuario() {

    }


    // --- usuarioId ---
    public int getUsuarioId() {
        return usuarioId.get();
    }

    public void setUsuarioId(int value) {
        this.usuarioId.set(value);
    }

    public IntegerProperty usuarioIdProperty() {
        return usuarioId;
    }

    // --- nombreUsuario ---
    public String getNombreUsuario() {
        return nombreUsuario.get();
    }

    public void setNombreUsuario(String value) {
        this.nombreUsuario.set(value);
    }

    public StringProperty nombreUsuarioProperty() {
        return nombreUsuario;
    }

    // --- email ---
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String value) {
        this.email.set(value);
    }

    public StringProperty emailProperty() {
        return email;
    }

    // --- usuario ---
    public String getUsuario() {
        return usuario.get();
    }

    public void setUsuario(String value) {
        this.usuario.set(value);
    }

    public StringProperty usuarioProperty() {
        return usuario;
    }

    // --- password ---
    public String getPassword() {
        return password.get();
    }

    public void setPassword(String value) {
        this.password.set(value);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }


}












