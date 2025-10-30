package Comprobantes.modelo;

import CatalogoGestion.Empresas.Modelo.Sucursal;
import CatalogoGestion.MasterCatalogo.Modelo.DetalleCatalogo;
import CatalogoGestion.Periodo.Periodo;
import Home.User.Modelo.Usuario;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Comprobante {

    private final IntegerProperty idComprobante;
    private final ObjectProperty<LocalDate> fecha;
    private final StringProperty numeroComprobante;
    private final StringProperty concepto;
    private final ObjectProperty<Usuario> usuario;
    private final ObjectProperty<LocalDate> fechaRegistro;
    private final ObjectProperty<DetalleCatalogo> tipoDocumento;
    private final ObjectProperty<Sucursal> sucursal;
    private final ObjectProperty<Periodo> periodo;

    //manejar los totales
    private final ObjectProperty<BigDecimal> debito = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> credito = new SimpleObjectProperty<>(BigDecimal.ZERO);


    public BigDecimal getDebito() {
        return debito.get();
    }

    public void setDebito(BigDecimal value) {
        debito.set(value);
    }

    public ObjectProperty<BigDecimal> debitoProperty() {
        return debito;
    }

    public BigDecimal getCredito() {
        return credito.get();
    }

    public void setCredito(BigDecimal value) {
        credito.set(value);
    }

    public ObjectProperty<BigDecimal> creditoProperty() {
        return credito;
    }

    public Periodo getPeriodo() {
        return periodo.get();
    }

    public DetalleCatalogo getTipoDocumento() {
        return tipoDocumento.get();
    }

    // ✅ Lista observable desde el inicio
    private final ObjectProperty<ObservableList<DetalleComprobante>> detalles;

    // ✅ Constructor para nuevos comprobantes (sin ID, con fechaRegistro = hoy)
    public Comprobante(LocalDate fecha, String numeroComprobante, String concepto,
                       Usuario usuario, DetalleCatalogo tipoDocumento,
                       Sucursal sucursal, Periodo periodo) {

        this.idComprobante = new SimpleIntegerProperty(0); // 0 = aún no persistido
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.numeroComprobante = new SimpleStringProperty(numeroComprobante);
        this.concepto = new SimpleStringProperty(concepto);
        this.usuario = new SimpleObjectProperty<>(usuario);
        this.fechaRegistro = new SimpleObjectProperty<>(LocalDate.now()); // hoy
        this.tipoDocumento = new SimpleObjectProperty<>(tipoDocumento);
        this.sucursal = new SimpleObjectProperty<>(sucursal);
        this.periodo = new SimpleObjectProperty<>(periodo);

        // Lista observable inicializada
        this.detalles = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    }

    // ✅ Constructor para comprobantes desde la BD (con ID y fechaRegistro)
    public Comprobante(IntegerProperty idComprobante, LocalDate fecha, String numeroComprobante, String concepto,
                       Usuario usuario, LocalDate fechaRegistro, DetalleCatalogo tipoDocumento,
                       Sucursal sucursal, Periodo periodo) {

        this.idComprobante = idComprobante;
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.numeroComprobante = new SimpleStringProperty(numeroComprobante);
        this.concepto = new SimpleStringProperty(concepto);
        this.usuario = new SimpleObjectProperty<>(usuario);
        this.fechaRegistro = new SimpleObjectProperty<>(fechaRegistro);
        this.tipoDocumento = new SimpleObjectProperty<>(tipoDocumento);
        this.sucursal = new SimpleObjectProperty<>(sucursal);
        this.periodo = new SimpleObjectProperty<>(periodo);

        this.detalles = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    }

    // ===== Métodos para detalles =====
    public ObservableList<DetalleComprobante> getDetalles() {
        return detalles.get();
    }

    public void setDetalles(ObservableList<DetalleComprobante> detalles) {
        this.detalles.set(detalles);
    }

    public ObjectProperty<ObservableList<DetalleComprobante>> detallesProperty() {
        return detalles;
    }

    // Método para añadir un detalle al comprobante
    public void addDetalle(DetalleComprobante detalle) {
        this.detalles.get().add(detalle);
    }

    // ===== Getters, setters y properties =====

    public int getIdComprobante() { return idComprobante.get(); }
    public void setIdComprobante(int id) { this.idComprobante.set(id); }
    public IntegerProperty idComprobanteProperty() { return idComprobante; }

    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate fecha) { this.fecha.set(fecha); }
    public ObjectProperty<LocalDate> fechaProperty() { return fecha; }

    public String getNumeroComprobante() { return numeroComprobante.get(); }
    public void setNumeroComprobante(String numero) { this.numeroComprobante.set(numero); }
    public StringProperty numeroComprobanteProperty() { return numeroComprobante; }

    public String getConcepto() { return concepto.get(); }
    public void setConcepto(String concepto) { this.concepto.set(concepto); }
    public StringProperty conceptoProperty() { return concepto; }



    public Usuario getUsuario() {
        return usuario.get();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario.set(usuario);
    }

    public ObjectProperty<Usuario> usuarioProperty() {
        return usuario;
    }






    public LocalDate getFechaRegistro() { return fechaRegistro.get(); }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro.set(fechaRegistro); }
    public ObjectProperty<LocalDate> fechaRegistroProperty() { return fechaRegistro; }

    public void setTipoDocumento(DetalleCatalogo tipoDocumento) { this.tipoDocumento.set(tipoDocumento); }
    public ObjectProperty<DetalleCatalogo> tipoDocumentoProperty() { return tipoDocumento; }

    public Sucursal getSucursal() { return sucursal.get(); }
    public void setSucursal(Sucursal sucursal) { this.sucursal.set(sucursal); }
    public ObjectProperty<Sucursal> sucursalProperty() { return sucursal; }


    public void setPeriodo(Periodo periodo) { this.periodo.set(periodo); }

    public ObjectProperty<Periodo> periodoProperty() { return periodo; }
}
