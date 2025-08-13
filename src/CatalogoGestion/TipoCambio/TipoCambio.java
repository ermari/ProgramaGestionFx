package CatalogoGestion.TipoCambio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.math.RoundingMode;

public class TipoCambio {

    private int tipoCambioId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal valor;

    public TipoCambio(int tipoCambioId, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal valor) {
        this.tipoCambioId = tipoCambioId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        // Se establece el valor con 4 decimales redondeando al alza (HALF_UP)
        this.valor = valor.setScale(4, RoundingMode.HALF_UP);
    }

    // --- Getters y Setters ---

    public int getTipoCambioId() {
        return tipoCambioId;
    }

    public void setTipoCambioId(int tipoCambioId) {
        this.tipoCambioId = tipoCambioId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor.setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "TipoCambio{" +
                "tipoCambioId=" + tipoCambioId +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", valor=" + valor +
                '}';
    }
}