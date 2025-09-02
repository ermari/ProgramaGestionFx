package CatalogoGestion.Empresas.Modelo;

public class Sucursal {
    private int sucursalId;
    private String nombre;
    private String codigo;
    private String direccion;
    private String telefono;
    private String email;
    private String ciudad;
    private String pais;
    private boolean estado;

    private Empresa empresa;

    public Sucursal(String ciudad, String codigo, String direccion, String email, Empresa empresa, boolean estado, String nombre, String pais, int sucursalId, String telefono) {
        this.ciudad = ciudad;
        this.codigo = codigo;
        this.direccion = direccion;
        this.email = email;
        this.empresa = empresa;
        this.estado = estado;
        this.nombre = nombre;
        this.pais = pais;
        this.sucursalId = sucursalId;
        this.telefono = telefono;
    }

    public Sucursal() {

    }

    public Sucursal(int sucursalId, String codigo, String nombre) {
    }

    public int getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(int sucursalId) {
        this.sucursalId = sucursalId;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    // Getters, Setters y Constructor


    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return this.nombre; // o getNombre()
    }
    public void setEmpresaId(int empresaId) {
    }

    public String getNombreSucursal() {
        return this.nombre;
    }
}
