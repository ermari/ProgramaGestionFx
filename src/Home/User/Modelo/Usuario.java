package Home.User.Modelo;

import CatalogoGestion.Empresas.Modelo.Sucursal;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private final IntegerProperty usuarioId = new SimpleIntegerProperty();
    private final StringProperty nombreUsuario = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty usuario = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    // Lista de sucursales asociadas al usuario
    private List<Sucursal> sucursales = new ArrayList<>();

    // Constructor vacío
    public Usuario() {
    }

    // Constructor auxiliar
    public Usuario(int usuarioId, String nombre, String usuario, String password) {
        this.usuarioId.set(usuarioId);
        this.nombreUsuario.set(nombre);
        this.usuario.set(usuario);
        this.password.set(password);
    }

    // Getters y setters JavaFX

    public int getUsuarioId() {
        return usuarioId.get();
    }

    public void setUsuarioId(int value) {
        this.usuarioId.set(value);
    }

    public IntegerProperty usuarioIdProperty() {
        return usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario.get();
    }

    public void setNombreUsuario(String value) {
        this.nombreUsuario.set(value);
    }

    public StringProperty nombreUsuarioProperty() {
        return nombreUsuario;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String value) {
        this.email.set(value);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getUsuario() {
        return usuario.get();
    }

    public void setUsuario(String value) {
        this.usuario.set(value);
    }

    public StringProperty usuarioProperty() {
        return usuario;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String value) {
        this.password.set(value);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    // --- Lista de sucursales asociadas al usuario ---
    public List<Sucursal> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Sucursal> sucursales) {
        this.sucursales = sucursales != null ? sucursales : new ArrayList<>();
    }

    // Obtener la sucursal principal (primer elemento o null)
    public Sucursal getSucursalPrincipal() {
        return sucursales.isEmpty() ? null : sucursales.get(0);
    }

    // Añadir una sucursal sin duplicados
    public void addSucursal(Sucursal sucursal) {
        if (sucursal != null && !sucursales.contains(sucursal)) {
            sucursales.add(sucursal);
        }
    }
}
