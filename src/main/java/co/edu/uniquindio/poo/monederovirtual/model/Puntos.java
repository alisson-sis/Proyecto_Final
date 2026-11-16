package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;


public class Puntos {

    public int calcularPuntos(Transaccion transaccion) {
        return transaccion.calcularPuntos();
    }

    public void acumularPuntos(Cliente cliente, int puntos, Transaccion transaccion) {
        cliente.actualizarPuntos(puntos);

        MovimientosPuntos movimiento = new MovimientosPuntos(
                puntos, transaccion, TipoMovimientoPuntos.ACUMULACION
        );

        cliente.agregarMovimientoPuntos(movimiento);
    }
    public int consultarPuntos(Cliente cliente) {
        return cliente.getPuntosAcumulados();
    }

    public void recalcularPuntos(Cliente cliente) {
        int total=0;
        for(MovimientosPuntos movimientosPuntos: cliente.getHistorialPuntos()) {
            total +=movimientosPuntos.getCantidad();

        }
        cliente.setPuntosAcumulados(total);
        }
    public void registrarReversion(Cliente cliente, Transaccion transaccion) {
        int puntos = transaccion.calcularPuntos();

        MovimientosPuntos movimiento = new MovimientosPuntos(
                -puntos, transaccion, TipoMovimientoPuntos.REVERSION
        );

        cliente.agregarMovimientoPuntos(movimiento);
        recalcularPuntos(cliente);
    }

        public Rango calcularRango(Cliente cliente) {
            int puntos = cliente.getPuntosAcumulados();

            if (puntos <= 500) {
                return Rango.BRONCE;
            } else if (puntos <= 1000) {
                return Rango.PLATA;
            } else if (puntos <= 5000) {
                return Rango.ORO;
            } else {
                return Rango.PLATINO;
            }
        }

        public void canjearPuntos(Cliente cliente, Beneficio beneficio) throws PuntosInsuficientesException {
            if (cliente == null || beneficio == null) {
                throw new IllegalArgumentException("El cliente y el beneficio no pueden ser nulos");
            }
            if (beneficio.getPuntosNecesarios() <= 0) {
                throw new IllegalArgumentException("Los puntos necesarios deben ser mayores que cero");
            }
            if (consultarPuntos(cliente) < beneficio.getPuntosNecesarios()) {
                throw new PuntosInsuficientesException("Puntos insuficientes");
            }

            MovimientosPuntos movimiento = new MovimientosPuntos(
                    -beneficio.getPuntosNecesarios(),
                    null,
                    TipoMovimientoPuntos.CANJE
            );
            LocalDateTime ahora = LocalDateTime.now();

            beneficio.setFechaCanje(ahora);

            if (beneficio.getPuntosNecesarios() == 500) {
                beneficio.setFechaVencimiento(ahora.plusMonths(1));
            }
            cliente.agregarMovimientoPuntos(movimiento);
            recalcularPuntos(cliente);
            cliente.agregarBeneficio(beneficio);
        }
    public boolean tieneBeneficio(Cliente cliente, String nombreBeneficio){
        return cliente.getBeneficiosCanjeados().stream()
                .anyMatch(b -> b.getNombreBeneficio().equals(nombreBeneficio));
    }
    public boolean tieneBeneficioActivo(Cliente cliente, String nombreBeneficio) {
        LocalDateTime ahora = LocalDateTime.now();

        return cliente.getBeneficiosCanjeados().stream()
                .anyMatch(b -> b.getNombreBeneficio().equals(nombreBeneficio)
                        && b.getFechaCanje() != null
                        && !b.getFechaCanje().isAfter(ahora)
                        && (b.getFechaVencimiento() == null
                        || b.getFechaVencimiento().isAfter(ahora))
                );
    }
    }






