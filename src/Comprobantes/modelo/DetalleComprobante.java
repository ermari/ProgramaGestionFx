package Comprobantes.modelo;


import Catalogo.Catalogo;
import javafx.beans.property.*;

public class DetalleComprobante {
    private final IntegerProperty idDetalle; // ID en la base de datos
    private final IntegerProperty idComprobante; // Clave foránea al comprobante
    private final ObjectProperty<Catalogo> contableCuenta; // Objeto Cuenta del catálogo
    private final DoubleProperty debito;
    private final DoubleProperty credito;
    private final StringProperty descripcion;

    // Constructor para cargar desde DB
    public DetalleComprobante(int idDetalle, int idComprobante, Catalogo cuenta, double debito, double credito, String descripcion) {
        this.idDetalle = new SimpleIntegerProperty(idDetalle);
        this.idComprobante = new SimpleIntegerProperty(idComprobante);
        this.contableCuenta = new SimpleObjectProperty<>(cuenta);
        this.debito = new SimpleDoubleProperty(debito);
        this.credito = new SimpleDoubleProperty(credito);
        this.descripcion = new SimpleStringProperty(descripcion); // ✅ Aquí estaba el fallo
    }

    // Constructor para nuevo detalle (sin ID todavía)
    public DetalleComprobante(Catalogo cuenta, double debito, double credito, String descripcion) {
        this(0, 0, cuenta, debito, credito,descripcion);
    }

    // --- Getters para los valores ---
    public int getIdDetalle() { return idDetalle.get(); }
    public int getIdComprobante() { return idComprobante.get(); }
    public Catalogo getContableCuenta() { return contableCuenta.get(); }
    public double getDebito() { return debito.get(); }
    public double getCredito() { return credito.get(); }
    public String getDescripcion() { return descripcion.get(); }


    // --- Getters para las Properties ---
    public IntegerProperty idDetalleProperty() { return idDetalle; }
    public IntegerProperty idComprobanteProperty() { return idComprobante; }
    public ObjectProperty<Catalogo> contableCuentaProperty() { return contableCuenta; }
    public DoubleProperty debitoProperty() { return debito; }
    public DoubleProperty creditoProperty() { return credito; }
    public StringProperty descripcionProperty() { return descripcion; }


    // --- Setters ---
    public void setIdDetalle(int idDetalle) { this.idDetalle.set(idDetalle); }
    public void setIdComprobante(int idComprobante) { this.idComprobante.set(idComprobante); }
    public void setContableCuenta(Catalogo contableCuenta) { this.contableCuenta.set(contableCuenta); }
    public void setDebito(double debito) { this.debito.set(debito); }
    public void setCredito(double credito) { this.credito.set(credito); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }

    // Método auxiliar para obtener el nombre de la cuenta (útil para la columna de nombre)
    public String getNombreCuenta() {
        return (contableCuenta.get() != null) ? contableCuenta.get().getValor() : "";
    }
}