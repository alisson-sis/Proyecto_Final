package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;

public class Beneficio {
    private String nombreBeneficio;
    private int puntosNecesarios;
    private String descripcion;
    private LocalDateTime fechaCanje;
    private LocalDateTime fechaVencimiento;

    public Beneficio(String nombreBeneficio, int puntosNecesarios, String descripcion) {
        this.nombreBeneficio = nombreBeneficio;
        this.puntosNecesarios=puntosNecesarios;
        this.descripcion=descripcion;
    }

    public String getNombreBeneficio() {
        return nombreBeneficio;
    }

    public void setNombreBeneficio(String nombreBeneficio) {
        this.nombreBeneficio = nombreBeneficio;
    }

    public int getPuntosNecesarios() {
        return puntosNecesarios;
    }

    public void setPuntosNecesarios(int puntosNecesarios) {
        this.puntosNecesarios = puntosNecesarios;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCanje() {
        return fechaCanje;
    }

    public void setFechaCanje(LocalDateTime fechaCanje) {
        this.fechaCanje = fechaCanje;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
