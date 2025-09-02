package CatalogoGestion.MasterCatalogo.Modelo;

import javafx.beans.property.*;

public class DetalleCatalogo {
    private final LongProperty detalleCatalogoId;
    private final LongProperty masterCatalogoId; // FK
    private final StringProperty codigoItem;
    private final StringProperty nombreItem;
    private final StringProperty valorAdicional;

    // Nuevo constructor corregido
    public DetalleCatalogo(int id, String codigoItem, String nombreItem) {
        this.detalleCatalogoId = new SimpleLongProperty(id);
        this.codigoItem = new SimpleStringProperty(codigoItem);
        this.nombreItem = new SimpleStringProperty(nombreItem);
        // Las otras propiedades necesitan ser inicializadas también, si es necesario.
        // Por ejemplo, puedes inicializarlas con valores predeterminados o nulos.
        this.masterCatalogoId = new SimpleLongProperty();
        this.valorAdicional = new SimpleStringProperty();
    }



    public DetalleCatalogo(StringProperty codigoItem, LongProperty detalleCatalogoId, LongProperty masterCatalogoId, StringProperty nombreItem, StringProperty valorAdicional) {
        this.codigoItem = codigoItem;
        this.detalleCatalogoId = detalleCatalogoId;
        this.masterCatalogoId = masterCatalogoId;
        this.nombreItem = nombreItem;
        this.valorAdicional = valorAdicional;
    }



    public DetalleCatalogo(int detalleCatalogoId, String codigoItem, String nombreItem, LongProperty detalleCatalogoId1, LongProperty masterCatalogoId, StringProperty codigoItem1, StringProperty nombreItem1, StringProperty valorAdicional) {
        this.detalleCatalogoId = detalleCatalogoId1;
        this.masterCatalogoId = masterCatalogoId;
        this.codigoItem = codigoItem1;
        this.nombreItem = nombreItem1;
        this.valorAdicional = valorAdicional;
    }

    public DetalleCatalogo(LongProperty detalleCatalogoId1, LongProperty masterCatalogoId1, StringProperty codigoItem1, StringProperty nombreItem1, StringProperty valorAdicional1) {
        this.detalleCatalogoId = detalleCatalogoId1;
        this.masterCatalogoId = masterCatalogoId1;
        this.codigoItem = codigoItem1;
        this.nombreItem = nombreItem1;
        this.valorAdicional = valorAdicional1;

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
    public int getDetalleCatalogoId() {
        return (int) detalleCatalogoId.get();
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

    @Override
    public String toString() {
        return (nombreItem != null) ? nombreItem.get() : "";
    }
    
}