package CatalogoGestion.Empresas.Modelo;

import java.time.LocalDate;

public class Empresa {
    private int empresaId;
    private String nombre;
    private String razonSocial;
    private String ruc;
    private String direccion;
    private String telefono;
    private String email;
    private String representante;
    private String tipoEmpresa;
    private LocalDate fechaConstitucion;
    private boolean estado;

    public Empresa(int empresaId, String nombre, String razonSocial, String ruc, String direccion, String telefono, String email, String representante, String tipoEmpresa, LocalDate fechaConstitucion, boolean estado) {
        this.empresaId = empresaId;
        this.nombre = nombre;
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.representante = representante;
        this.tipoEmpresa = tipoEmpresa;
        this.fechaConstitucion = fechaConstitucion;
        this.estado = estado;
    }

    public Empresa() {

    }
// Getters, Setters y Constructor

    public int getEmpresaId() {
        return empresaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }


    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public LocalDate getFechaConstitucion() {
        return fechaConstitucion;
    }

    public void setFechaConstitucion(LocalDate fechaConstitucion) {
        this.fechaConstitucion = fechaConstitucion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public void setEmpresaId(int empresaId) {
        this.empresaId = empresaId;
    }




    @Override
    public String toString() {
        return this.nombre; // o getNombre()
    }

}
