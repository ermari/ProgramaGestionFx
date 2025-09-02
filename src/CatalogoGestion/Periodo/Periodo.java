package CatalogoGestion.Periodo;

import java.time.LocalDate;

public class Periodo {

    private int id;
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    /**
     * @return
     */
    @Override
    public String toString() {
        return nombre + " (" + getDesdeFechaInicio() + ")";
    }


}