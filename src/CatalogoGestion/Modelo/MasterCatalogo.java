package CatalogoGestion.Modelo;

import javafx.beans.property.*;

public class MasterCatalogo {
    private final LongProperty masterCatalogoId;
    private final StringProperty codigo;
    private final StringProperty nombre;
    private final StringProperty descripcion;

    public MasterCatalogo() {
        this(0L, null, null, null); // Default constructor
    }

    public MasterCatalogo(Long masterCatalogoId, String codigo, String nombre, String descripcion) {
        this.masterCatalogoId = new SimpleLongProperty(masterCatalogoId);
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
    }

    // Getters for properties (for TableView)
    public LongProperty masterCatalogoIdProperty() { return masterCatalogoId; }
    public StringProperty codigoProperty() { return codigo; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty descripcionProperty() { return descripcion; }

    // Getters and Setters for values
    public long getMasterCatalogoId() { return masterCatalogoId.get(); }
    public void setMasterCatalogoId(long masterCatalogoId) { this.masterCatalogoId.set(masterCatalogoId); }
    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String codigo) { this.codigo.set(codigo); }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
}

