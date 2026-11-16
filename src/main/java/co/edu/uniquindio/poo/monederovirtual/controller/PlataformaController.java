package co.edu.uniquindio.poo.monederovirtual.controller;

import co.edu.uniquindio.poo.monederovirtual.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** Puente entre la interfaz y el model. Guarda la única Plataforma de la app. */
public class PlataformaController {
    private static PlataformaController instancia;
    private final Plataforma plataforma = new Plataforma();
    private final AnalizadorTransacciones analizador = new AnalizadorTransacciones();

    private PlataformaController() {
        cargarDatosDemo();
    }

    public static PlataformaController getInstancia() {
        if (instancia == null) {
            instancia = new PlataformaController();
        }
        return instancia;
    }

    public Plataforma getPlataforma() { return plataforma; }

    public AnalizadorTransacciones getAnalizador() { return analizador; }

    /** Programa un depósito (destino == null) o una transferencia hacia destino. */
    public void programar(Cliente cliente, Monedero origen, Monedero destino, double monto,
                          LocalDateTime fecha, Frecuencia frecuencia) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        if (frecuencia == null || fecha == null) {
            throw new IllegalArgumentException("Elige la fecha y la frecuencia");
        }
        Transaccion base;
        if (destino == null) {
            base = new Deposito(monto, fecha, UUID.randomUUID().toString(), origen);
        } else {
            if (origen == destino) {
                throw new IllegalArgumentException("El monedero de destino debe ser distinto al de origen");
            }
            Transferencia t = new Transferencia(monto, fecha, UUID.randomUUID().toString(), origen, destino);
            t.setComisionAplicada(t.calcularComision());
            base = t;
        }
        plataforma.programarTransaccion(new TransaccionProgramada(
                base, fecha, frecuencia, EstadoProgramacion.PENDIENTE, null, cliente));
    }

    public List<TransaccionProgramada> programacionesDe(Cliente cliente) {
        return plataforma.getTransaccionesProgramadas().stream()
                .filter(p -> p.getClientePropietario() == cliente)
                .collect(Collectors.toList());
    }

    private void cargarDatosDemo() {
        plataforma.registrarCliente(new Cliente("Ana", "12345", 0, "ana@gmail.com", "3001234567"));
        plataforma.registrarCliente(new Cliente("Carlos", "67890", 0, "carlos@gmail.com", "3007654321"));
        plataforma.crearMonedero("12345", "M001", "Ahorros");
        plataforma.crearMonedero("12345", "M002", "Gastos");
        plataforma.crearMonedero("67890", "M003", "Ahorros");
        plataforma.realizarDeposito("12345", "M001", 1000);
        plataforma.realizarDeposito("67890", "M003", 500);
    }
}
