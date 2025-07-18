package CatalogoGestion.Modelo;

import javafx.beans.property.*;

public class DetalleCatalogo {
    private final LongProperty detalleCatalogoId;
    private final LongProperty masterCatalogoId; // FK
    private final StringProperty codigoItem;
    private final StringProperty nombreItem;
    private final StringProperty valorAdicional;

    public DetalleCatalogo() {
        this(0L, 0L, null, null, null); // Default constructor
    }

    public DetalleCatalogo(Long detalleCatalogoId, Long masterCatalogoId, String codigoItem, String nombreItem, String valorAdicional) {
        this.detalleCatalogoId = new SimpleLongProperty(detalleCatalogoId);
        this.masterCatalogoId = new SimpleLongProperty(masterCatalogoId);
        this.codigoItem = new SimpleStringProperty(codigoItem);
        this.nombreItem = new SimpleStringProperty(nombreItem);
        this.valorAdicional = new SimpleStringProperty(valorAdicional);
    }

    // Getters for properties
    public LongProperty detalleCatalogoIdProperty() {
        return detalleCatalogoId;
    }

    public LongProperty masterCatalogoIdProperty() {
        return masterCatalogoId;
    }

    public StringProperty codigoItemProperty() {
        return codigoItem;
    }

    public StringProperty nombreItemProperty() {
        return nombreItem;
    }

    public StringProperty valorAdicionalProperty() {
        return valorAdicional;
    }

    // Getters and Setters for values
    public long getDetalleCatalogoId() {
        return detalleCatalogoId.get();
    }

    public void setDetalleCatalogoId(long detalleCatalogoId) {
        this.detalleCatalogoId.set(detalleCatalogoId);
    }

    public long getMasterCatalogoId() {
        return masterCatalogoId.get();
    }

    public void setMasterCatalogoId(long masterCatalogoId) {
        this.masterCatalogoId.set(masterCatalogoId);
    }

    public String getCodigoItem() {
        return codigoItem.get();
    }

    public void setCodigoItem(String codigoItem) {
        this.codigoItem.set(codigoItem);
    }

    public String getNombreItem() {
        return nombreItem.get();
    }

    public void setNombreItem(String nombreItem) {
        this.nombreItem.set(nombreItem);
    }

    public String getValorAdicional() {
        return valorAdicional.get();
    }

    public void setValorAdicional(String valorAdicional) {
        this.valorAdicional.set(valorAdicional);
    }
}