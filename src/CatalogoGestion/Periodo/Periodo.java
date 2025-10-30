package CatalogoGestion.Periodo;

import java.time.LocalDate;
import java.util.Objects;

public class Periodo {

    private Integer id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean estado;
    private String descripcion; // NUEVO CAMPO

    // Constructor vacío
    public Periodo() {
    }



    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    // Constructor con todos los campos
    public Periodo(int id, String nombre, LocalDate fechaInicio, LocalDate fechaFin,Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado=estado;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDesdeFechaInicio() {
        return "Desde " + (fechaInicio != null ? fechaInicio.toString() : "");
    }



    @Override
    public String toString() {
        return nombre + " (" + getDesdeFechaInicio() + ")";
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Periodo periodo)) return false;
        return estado == periodo.estado && Objects.equals(id, periodo.id) && Objects.equals(nombre, periodo.nombre) && Objects.equals(fechaInicio, periodo.fechaInicio) && Objects.equals(fechaFin, periodo.fechaFin) && Objects.equals(descripcion, periodo.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, fechaInicio, fechaFin, estado, descripcion);
    }
}