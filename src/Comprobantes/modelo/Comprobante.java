package Comprobantes.modelo;


import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Comprobante {
    private final IntegerProperty idComprobante;
    private final ObjectProperty<LocalDate> fecha;
    private final StringProperty numeroComprobante;
    private final StringProperty concepto;
    private final ObjectProperty<List<DetalleComprobante>> detalles; // Lista de detalles

    public Comprobante(int idComprobante, LocalDate fecha, String numeroComprobante, String concepto) {
        this.idComprobante = new SimpleIntegerProperty(idComprobante);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.numeroComprobante = new SimpleStringProperty(numeroComprobante);
        this.concepto = new SimpleStringProperty(concepto);
        this.detalles = new SimpleObjectProperty<>(new ArrayList<>());
    }

    // Constructor sin ID (para nuevos comprobantes)
    public Comprobante(LocalDate fecha, String numeroComprobante, String concepto) {
        this(0, fecha, numeroComprobante, concepto); // ID 0 o algún marcador para "nuevo"
    }

    // --- Getters para los valores ---
    public int getIdComprobante() { return idComprobante.get(); }
    public LocalDate getFecha() { return fecha.get(); }
    public String getNumeroComprobante() { return numeroComprobante.get(); }
    public String getConcepto() { return concepto.get(); }
    public List<DetalleComprobante> getDetalles() { return detalles.get(); }

    // --- Getters para las Properties ---
    public IntegerProperty idComprobanteProperty() { return idComprobante; }
    public ObjectProperty<LocalDate> fechaProperty() { return fecha; }
    public StringProperty numeroComprobanteProperty() { return numeroComprobante; }
    public StringProperty conceptoProperty() { return concepto; }
    public ObjectProperty<List<DetalleComprobante>> detallesProperty() { return detalles; }

    // --- Setters ---
    public void setIdComprobante(int idComprobante) { this.idComprobante.set(idComprobante); }
    public void setFecha(LocalDate fecha) { this.fecha.set(fecha); }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante.set(numeroComprobante); }
    public void setConcepto(String concepto) { this.concepto.set(concepto); }
    public void setDetalles(List<DetalleComprobante> detalles) { this.detalles.set(detalles); }

    // Método para añadir un detalle al comprobante
    public void addDetalle(DetalleComprobante detalle) {
        if (this.detalles.get() == null) {
            this.detalles.set(new ArrayList<>());
        }
        this.detalles.get().add(detalle);
    }
}
