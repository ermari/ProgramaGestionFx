package Home.User.Modelo;

import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Usuario extends ObjectProperty<LocalDate> {

    private final IntegerProperty usuarioId = new SimpleIntegerProperty();
    private final StringProperty nombreUsuario = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty usuario = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    // Lista de sucursales asociadas al usuario
    private List<Sucursal> sucursales = new ArrayList<>();


    private List<Rol> roles= new ArrayList<>();;

    public Usuario(int usuarioId, String nombreUsuario, String email, String usuario, String password) {
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    private Empresa empresa;



    private Rol rol;
    private List<Permiso> permisos = new ArrayList<>();

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

    public void setRol(Rol rolSeleccionado) {
    }

    public void setPermisos(ArrayList<Permiso> permisos) {
    }

    public Rol getRol() {
        return this.rol;
    }

    public List<Rol> getRoles() {
        return this.roles;
    }


    public void setRoles(List<Rol> rols) {
        this.roles = rols ;
    }

    @Override
    public void bind(ObservableValue<? extends LocalDate> observableValue) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public LocalDate get() {
        return null;
    }

    @Override
    public void set(LocalDate localDate) {

    }

    @Override
    public void addListener(ChangeListener<? super LocalDate> changeListener) {

    }

    @Override
    public void removeListener(ChangeListener<? super LocalDate> changeListener) {

    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {

    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}
