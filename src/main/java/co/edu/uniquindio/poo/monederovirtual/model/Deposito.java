package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Deposito extends Transaccion implements Programable{
    private Monedero monederoDestino;

    public Deposito(double monto, LocalDateTime fecha, String identificador,
                    Monedero monederoDestino) {
        super(monto, fecha, identificador, EstadoTransaccion.PENDIENTE);
        this.monederoDestino = monederoDestino;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }

    public void setMonederoDestino(Monedero monederoDestino) {
        this.monederoDestino = monederoDestino;
    }

    @Override
    public void ejecutar() {
        if (getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }

        if (getEstado() != EstadoTransaccion.PENDIENTE) {
            throw new IllegalStateException("La transacción no está pendiente");
        }

        monederoDestino.aumentarSaldo(getMonto());
        setEstado(EstadoTransaccion.COMPLETADA);
    }

    @Override
    public void revertir() throws SaldoInsuficienteException{
        if (getEstado() != EstadoTransaccion.COMPLETADA) {
            throw new IllegalStateException("No se puede revertir esta transacción");
        }

        monederoDestino.disminuirSaldo(getMonto());
        setEstado(EstadoTransaccion.REVERTIDA);

    }

    @Override
    public int calcularPuntos() {
        return (int) (getMonto() / 100);
    }

    @Override
    public Transaccion crearNuevaTransaccion(LocalDateTime fecha) {
        return new Deposito(
                getMonto(),
                fecha,
                UUID.randomUUID().toString(),
                monederoDestino
        );
    }
}
