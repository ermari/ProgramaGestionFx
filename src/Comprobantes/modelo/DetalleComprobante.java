package Comprobantes.modelo;

import Catalogo.Catalogo;
import Home.User.Modelo.Usuario;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DetalleComprobante {

    private final IntegerProperty detalleId = new SimpleIntegerProperty(0);
    private final IntegerProperty numeroLinea = new SimpleIntegerProperty(0);
    private final ObjectProperty<Comprobante> comprobante = new SimpleObjectProperty<>();
    private final ObjectProperty<Catalogo> contableCuenta = new SimpleObjectProperty<>();
    private final StringProperty descripcion = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> debito = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> credito = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<Usuario> usuario = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> fechaRegistro = new SimpleObjectProperty<>(LocalDate.now());




    // ✅ Constructor completo (cuando cargas de la DB)
    public DetalleComprobante(int detalleId, int numeroLinea, Comprobante comprobante, Catalogo contableCuenta,
                              String descripcion, BigDecimal debito, BigDecimal credito, Usuario usuario, LocalDate fechaRegistro) {
        setDetalleId(detalleId);
        setNumeroLinea(numeroLinea);
        setComprobante(comprobante);
        setContableCuenta(contableCuenta);
        setDescripcion(descripcion);
        setDebito(debito != null ? debito : BigDecimal.ZERO);
        setCredito(credito != null ? credito : BigDecimal.ZERO);
        setUsuario(usuario);
        setFechaRegistro(fechaRegistro != null ? fechaRegistro : LocalDate.now());
    }

    // ✅ Constructor práctico (para nuevas partidas)
    public DetalleComprobante(Catalogo contableCuenta, BigDecimal debito, BigDecimal credito, String descripcion) {
        this(0, 0, null, contableCuenta, descripcion,
                debito != null ? debito : BigDecimal.ZERO,
                credito != null ? credito : BigDecimal.ZERO,
                null, LocalDate.now());
    }

    // ✅ Constructor rápido usando double
    public DetalleComprobante(Catalogo contableCuenta, double debito, double credito, String descripcion) {
        this(contableCuenta, BigDecimal.valueOf(debito), BigDecimal.valueOf(credito), descripcion);
    }

    // ✅ Constructor vacío (para nueva fila en la tabla)
    public DetalleComprobante() {
        this(null, BigDecimal.ZERO, BigDecimal.ZERO, "");
    }

    // --- Properties para la UI ---
    public IntegerProperty detalleIdProperty() { return detalleId; }
    public IntegerProperty numeroLineaProperty() { return numeroLinea; }
    public ObjectProperty<Comprobante> comprobanteProperty() { return comprobante; }
    public ObjectProperty<Catalogo> contableCuentaProperty() { return contableCuenta; }
    public StringProperty descripcionProperty() { return descripcion; }
    public ObjectProperty<BigDecimal> debitoProperty() { return debito; }
    public ObjectProperty<BigDecimal> creditoProperty() { return credito; }
    public ObjectProperty<Usuario> usuarioProperty() { return usuario; }
    public ObjectProperty<LocalDate> fechaRegistroProperty() { return fechaRegistro; }

    // --- Getters y Setters ---
    public int getDetalleId() { return detalleId.get(); }
    public void setDetalleId(int detalleId) { this.detalleId.set(detalleId); }

    public int getNumeroLinea() { return numeroLinea.get(); }
    public void setNumeroLinea(int numeroLinea) { this.numeroLinea.set(numeroLinea); }

    public Comprobante getComprobante() { return comprobante.get(); }
    public void setComprobante(Comprobante comprobante) { this.comprobante.set(comprobante); }

    public Catalogo getContableCuenta() { return contableCuenta.get(); }
    public void setContableCuenta(Catalogo contableCuenta) { this.contableCuenta.set(contableCuenta); }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }

    public BigDecimal getDebito() { return debito.get(); }
    public void setDebito(BigDecimal debito) { this.debito.set(debito != null ? debito : BigDecimal.ZERO); }

    public double getDebitoAsDouble() { return getDebito().doubleValue(); }
    public void setDebito(double debito) { this.debito.set(BigDecimal.valueOf(debito)); }

    public BigDecimal getCredito() { return credito.get(); }
    public void setCredito(BigDecimal credito) { this.credito.set(credito != null ? credito : BigDecimal.ZERO); }

    public double getCreditoAsDouble() { return getCredito().doubleValue(); }
    public void setCredito(double credito) { this.credito.set(BigDecimal.valueOf(credito)); }

    public Usuario getUsuario() { return usuario.get(); }
    public void setUsuario(Usuario usuario) { this.usuario.set(usuario); }

    public LocalDate getFechaRegistro() { return fechaRegistro.get(); }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro.set(fechaRegistro != null ? fechaRegistro : LocalDate.now()); }
}
