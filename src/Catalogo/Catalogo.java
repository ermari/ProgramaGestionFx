package Catalogo;

public class Catalogo {
    private int catalogoId;
    private Integer catalogoSup;
    private String codigo;
    private String valor;
    private String descripcion;
    private int orden;
    private int nivel;
    private String expandible; // "S" o "N"
    private String codigoPadre;

    public Catalogo(int i, String root, String root1, String catálogoRaíz,
                    int i1, Object o, String s) {
    }

    public Catalogo(int catalogoId, Catalogo catalogoSup,
                    String codigo, String valor, String descripcion, int orden, boolean expandible){}



    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    // Getters y Setters
    public String getExpandible() { return expandible; }
    public void setExpandible(String expandible) { this.expandible = expandible; }

    public int getCatalogoId() {
        return catalogoId;
    }

    public void setCatalogoId(int catalogoId) {
        this.catalogoId = catalogoId;
    }

    public Catalogo() {
    }


    // Constructor, getters y setters
    public Catalogo(int catalogoId, Integer catalogoSup, String codigo, String valor, String descripcion, int orden, int nivel, String codigoPadre) {
        this.catalogoId = catalogoId;
        this.catalogoSup = catalogoSup;
        this.codigo = codigo;
        this.valor = valor;
        this.descripcion = descripcion;
        this.orden = orden;
        this.nivel=nivel;
        this.codigoPadre=codigoPadre;
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

    public String getCodigoPadre() {
        return codigoPadre;
    }

    public void setCodigoPadre(String codigoPadre) {
        this.codigoPadre = codigoPadre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Catalogo other = (Catalogo) obj;
        return catalogoId == other.catalogoId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(catalogoId);
    }



    @Override
    public String toString() {
        return valor; // o cualquier campo que quieras mostrar
    }
// Getters y Setters


}
