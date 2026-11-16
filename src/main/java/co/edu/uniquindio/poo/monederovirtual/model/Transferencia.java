package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transferencia extends Transaccion implements Programable{
    private Monedero origen;
    private Monedero destino;
    private double comision;
    private double comisionAplicada;

    public Transferencia(double monto, LocalDateTime fecha, String identificador,
                         Monedero origen, Monedero destino) {
        super(monto, fecha, identificador, EstadoTransaccion.PENDIENTE);

        this.origen = origen;
        this.destino = destino;
        this.comision = 0;
        this.comisionAplicada = 0;
    }

    public Monedero getOrigen() {
        return origen;
    }

    public void setOrigen(Monedero origen) {
        this.origen = origen;
    }

    public Monedero getDestino() {
        return destino;
    }

    public void setDestino(Monedero destino) {
        this.destino = destino;
    }

    public double getComision() {
        return comision;
    }

    public void setComision(double comision) {
        this.comision = comision;
    }

    public double getComisionAplicada() {
        return comisionAplicada;
    }

    public void setComisionAplicada(double comisionAplicada) {
        if (comisionAplicada < 0) {
            throw new IllegalArgumentException("La comisión no puede ser negativa");
        }
        this.comisionAplicada = comisionAplicada;
    }

    @Override
    public void ejecutar() throws SaldoInsuficienteException{
        if (getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }

        if (getEstado() != EstadoTransaccion.PENDIENTE) {
            throw new IllegalStateException("La transacción no está pendiente");
        }

        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Los monederos no pueden ser nulos");
        }

        if (origen == destino) {
            throw new IllegalArgumentException("Los monederos deben ser diferentes");
        }

        origen.disminuirSaldo(getMonto() + comisionAplicada);
        destino.aumentarSaldo(getMonto());

        setEstado(EstadoTransaccion.COMPLETADA);

    }

    @Override
    public void revertir() throws SaldoInsuficienteException{
        if (getEstado() != EstadoTransaccion.COMPLETADA) {
            throw new IllegalStateException("No se puede revertir esta transacción");
        }
        if (destino.getSaldo() < getMonto()) {
            throw new IllegalStateException("El monedero de destino no tiene saldo suficiente para revertir");
        }
        destino.disminuirSaldo(getMonto());
        origen.aumentarSaldo(getMonto() + comisionAplicada);

        setEstado(EstadoTransaccion.REVERTIDA);

    }

    @Override
    public int calcularPuntos() {
        return (int) ((getMonto() / 100) * 3);
    }

    public double calcularComision() {
        return getMonto() * 0.02;
    }

    @Override
    public Transaccion crearNuevaTransaccion(LocalDateTime fecha) {
        Transferencia nueva=new Transferencia(
                getMonto(),
                fecha,
                UUID.randomUUID().toString(),
                origen,
                destino);

        nueva.setComisionAplicada(comisionAplicada);

        return nueva;

    }

}
