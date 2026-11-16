package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;

public class Retiro extends Transaccion{
    private Monedero monederoOrigen;

    public Retiro(double monto, LocalDateTime fecha, String identificador,
                  Monedero monederoOrigen) {
        super(monto, fecha, identificador, EstadoTransaccion.PENDIENTE);

        this.monederoOrigen=monederoOrigen;
    }
    @Override
    public void ejecutar() throws SaldoInsuficienteException {
        if (getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }

        if (getEstado() != EstadoTransaccion.PENDIENTE) {
            throw new IllegalStateException("La transacción no está pendiente");
        }

        monederoOrigen.disminuirSaldo(getMonto());
        setEstado(EstadoTransaccion.COMPLETADA);

    }

    @Override
    public void revertir() {
        if (getEstado() != EstadoTransaccion.COMPLETADA) {
            throw new IllegalStateException("No se puede revertir esta transacción");
        }

        monederoOrigen.aumentarSaldo(getMonto());
        setEstado(EstadoTransaccion.REVERTIDA);

    }

    @Override
    public int calcularPuntos() {
        return (int) ((getMonto() / 100) * 2);
    }

}
