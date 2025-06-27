package Catalogo;

public class Catalogo {
    private int catalogoId;
    private Integer catalogoSup;
    private String codigo;
    private String valor;
    private String descripcion;
    private int orden;

    public int getCatalogoId() {
        return catalogoId;
    }

    public void setCatalogoId(int catalogoId) {
        this.catalogoId = catalogoId;
    }

    public Catalogo() {
    }




    // Constructor, getters y setters
    public Catalogo(int catalogoId, Integer catalogoSup, String codigo, String valor, String descripcion, int orden) {
        this.catalogoId = catalogoId;
        this.catalogoSup = catalogoSup;
        this.codigo = codigo;
        this.valor = valor;
        this.descripcion = descripcion;
        this.orden = orden;
    }

    public Integer getCatalogoSup() {
        return catalogoSup;
    }

    public void setCatalogoSup(Integer catalogoSup) {
        this.catalogoSup = catalogoSup;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    @Override
    public String toString() {
        return valor; // o cualquier campo que quieras mostrar
    }
// Getters y Setters

}
