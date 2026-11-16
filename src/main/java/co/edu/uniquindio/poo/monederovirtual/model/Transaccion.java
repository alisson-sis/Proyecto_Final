package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;

public abstract class Transaccion {
    private double monto;
    private LocalDateTime fecha;
    private String identificador;
    private boolean sospechosa;

    private EstadoTransaccion estado;

    public Transaccion(double monto, LocalDateTime fecha, String identificador,
                       EstadoTransaccion estado) {
        this.monto = monto;
        this.fecha=fecha;
        this.identificador=identificador;
        this.estado=estado;
        this.sospechosa = false;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransaccion estado) {
        this.estado = estado;
    }

    public abstract void ejecutar() throws SaldoInsuficienteException;

    public abstract void revertir() throws SaldoInsuficienteException;

    public abstract int calcularPuntos();

    public boolean isSospechosa() {
        return sospechosa;
    }

    public void setSospechosa(boolean sospechosa) {
        this.sospechosa = sospechosa;
    }
}
