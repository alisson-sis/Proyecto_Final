package co.edu.uniquindio.poo.monederovirtual.app;

import co.edu.uniquindio.poo.monederovirtual.model.Plataforma;
import co.edu.uniquindio.poo.monederovirtual.model.Cliente;
import co.edu.uniquindio.poo.monederovirtual.model.Monedero;
import co.edu.uniquindio.poo.monederovirtual.model.MonederoNoEncontradoException;
import co.edu.uniquindio.poo.monederovirtual.model.SaldoInsuficienteException;
import co.edu.uniquindio.poo.monederovirtual.model.Transaccion;
import co.edu.uniquindio.poo.monederovirtual.model.PuntosInsuficientesException;
import co.edu.uniquindio.poo.monederovirtual.model.TransaccionProgramada;
import co.edu.uniquindio.poo.monederovirtual.model.Deposito;
import co.edu.uniquindio.poo.monederovirtual.model.EstadoTransaccion;
import co.edu.uniquindio.poo.monederovirtual.model.EstadoProgramacion;
import co.edu.uniquindio.poo.monederovirtual.model.Frecuencia;
import co.edu.uniquindio.poo.monederovirtual.model.AnalizadorTransacciones;
import co.edu.uniquindio.poo.monederovirtual.model.AnalisisGastos;
import co.edu.uniquindio.poo.monederovirtual.model.Alerta;

import java.time.LocalDateTime;
import java.util.*;


public class App {
    public static void main(String[] args) {

        Plataforma plataforma = new Plataforma();

        Cliente cliente1 = new Cliente(
                "Ana",
                "12345",
                0,
                "ana@gmail.com",
                "3001234567"
        );

        Cliente cliente2 = new Cliente(
                "Carlos",
                "67890",
                0,
                "carlos@gmail.com",
                "3007654321"
        );

        plataforma.registrarCliente(cliente1);
        plataforma.registrarCliente(cliente2);

        plataforma.crearMonedero("12345", "M001", "Ahorros");
        plataforma.crearMonedero("67890", "M002", "Gastos");

        System.out.println("Clientes registrados correctamente");
        System.out.println("Monedero de Ana: "
                + plataforma.consultarSaldo("12345", "M001"));
        System.out.println("Monedero de Carlos: "
                + plataforma.consultarSaldo("67890", "M002"));

        plataforma.realizarDeposito("12345", "M001", 1000);
        plataforma.realizarDeposito("67890", "M002", 500);

        System.out.println("Saldo de Ana: "
                + plataforma.consultarSaldo("12345", "M001"));

        System.out.println("Saldo de Carlos: "
                + plataforma.consultarSaldo("67890", "M002"));

        System.out.println("Puntos de Ana: "
                + cliente1.getPuntosAcumulados());

        System.out.println("Puntos de Carlos: "
                + cliente2.getPuntosAcumulados());

        try {
            plataforma.realizarRetiro("12345", "M001", 200);
            plataforma.realizarRetiro("67890", "M002", 100);

            System.out.println("Saldo de Ana después del retiro: "
                    + plataforma.consultarSaldo("12345", "M001"));

            System.out.println("Saldo de Carlos después del retiro: "
                    + plataforma.consultarSaldo("67890", "M002"));

            System.out.println("Puntos de Ana: "
                    + cliente1.getPuntosAcumulados());

            System.out.println("Puntos de Carlos: "
                    + cliente2.getPuntosAcumulados());

        } catch (MonederoNoEncontradoException | SaldoInsuficienteException e) {
            System.out.println(e.getMessage());
        }
        try {
            plataforma.realizarTransferencia(
                    "12345", "M001",
                    "67890", "M002",
                    300
            );

            System.out.println("Saldo de Ana: "
                    + plataforma.consultarSaldo("12345", "M001"));

            System.out.println("Saldo de Carlos: "
                    + plataforma.consultarSaldo("67890", "M002"));

            System.out.println("Puntos de Ana: "
                    + cliente1.getPuntosAcumulados());

            System.out.println("Puntos de Carlos: "
                    + cliente2.getPuntosAcumulados());

        } catch (MonederoNoEncontradoException | SaldoInsuficienteException e) {
            System.out.println(e.getMessage());
        }
        List<Transaccion> historialAna =
                plataforma.consultarHistorial("12345", "M001");

        Transaccion ultimaTransaccion =
                historialAna.get(historialAna.size() - 1);

        try {
            plataforma.revertirTransaccion(
                    "12345",
                    "M001",
                    ultimaTransaccion.getIdentificador()
            );

            System.out.println("Saldo de Ana después de revertir: "
                    + plataforma.consultarSaldo("12345", "M001"));

            System.out.println("Saldo de Carlos después de revertir: "
                    + plataforma.consultarSaldo("67890", "M002"));

            System.out.println("Puntos de Ana: "
                    + cliente1.getPuntosAcumulados());

            System.out.println("Puntos de Carlos: "
                    + cliente2.getPuntosAcumulados());

        } catch (MonederoNoEncontradoException | SaldoInsuficienteException e) {
            System.out.println(e.getMessage());
        }
        try {
            plataforma.realizarRetiro("12345", "M001", 1000);
        } catch (MonederoNoEncontradoException | SaldoInsuficienteException e) {
            System.out.println("Error controlado: " + e.getMessage());
        }
        try {
            plataforma.canjearReduccionComision("12345");
        } catch (PuntosInsuficientesException e) {
            System.out.println("Error controlado: " + e.getMessage());
        }
        Deposito depositoProgramado = new Deposito(
                500,
                LocalDateTime.now().minusMinutes(1),
                "DEP-PROG-001",
                plataforma.buscarMonedero(cliente1, "M001") .orElseThrow()
        );

        TransaccionProgramada programacion = new TransaccionProgramada(
                depositoProgramado,
                LocalDateTime.now().minusMinutes(1),
                Frecuencia.SEMANAL,
                EstadoProgramacion.PENDIENTE,
                null,
                cliente1
        );

        plataforma.programarTransaccion(programacion);

        System.out.println("Saldo antes de procesar: "
                + plataforma.consultarSaldo("12345", "M001"));

        plataforma.procesarTransaccionesProgramadas();

        System.out.println("Fecha programada: "
                + programacion.getFechaEjecucion());

        System.out.println("Nueva fecha programada: "
                + programacion.getFechaEjecucion());

        System.out.println("Estado: " + programacion.getEstado());

        System.out.println("Saldo después de procesar: "
                + plataforma.consultarSaldo("12345", "M001"));

        System.out.println("Puntos de Ana: "
                + cliente1.getPuntosAcumulados());

        System.out.println("Estado de programación: "
                + programacion.getEstado());

        //plataforma.realizarDeposito("12345", "M001", 5000);
        AnalizadorTransacciones analizador = new AnalizadorTransacciones();

        for (Transaccion t : analizador.obtenerTransaccionesHistoricas(cliente1)) {
            System.out.println(
                    "Monto: " + t.getMonto()
                            + " | Estado: " + t.getEstado()
            );
        }
        System.out.println("Promedio: " +
                analizador.calcularPromedioMontos(cliente1));

        System.out.println("Desviación estándar: " +
                analizador.calcularDesviacionEstandar(cliente1));

        System.out.println("Límite atípico: " +
                analizador.calcularLimiteAtipico(cliente1));

        //LocalDateTime fechaOriginal = programacion.getFechaEjecucion();
        //plataforma.procesarTransaccionesProgramadas();
        LocalDateTime fechaOriginal = programacion.getFechaEjecucion();

        System.out.println("Fecha original: " + fechaOriginal);
        System.out.println("Frecuencia: " + programacion.getFrecuencia());

        plataforma.procesarTransaccionesProgramadas();

        System.out.println("Fecha después de procesar: "
                + programacion.getFechaEjecucion());

        System.out.println("Estado: " + programacion.getEstado());

        System.out.println("Fecha original: " + fechaOriginal);
        System.out.println("Nueva fecha: " + programacion.getFechaEjecucion());

        Deposito depositoPrueba = new Deposito(
                5000,
                LocalDateTime.now(),
                "DEP-PRUEBA-001",
                plataforma.buscarMonedero(cliente1, "M001").orElseThrow()
        );

        List<Transaccion> sospechosas =
                analizador.detectarTransaccionesAtipicas(cliente1, depositoPrueba);

        System.out.println("Transacciones sospechosas: " + sospechosas.size());
        System.out.println("¿Es sospechosa?: " + depositoPrueba.isSospechosa());
        System.out.println("Alertas del cliente: " + cliente1.getHistorialAlertas().size());

        plataforma.procesarRecordatoriosProgramados();

        System.out.println("Alertas de Ana: "
                + cliente1.getHistorialAlertas().size());

        // Crear una programación de prueba para el recordatorio
        Deposito depositoPruebaRecordatorio = new Deposito(
                100,
                LocalDateTime.now(),
                "DEP-RECORDATORIO-001",
                plataforma.buscarMonedero(cliente1, "M001").orElseThrow()
        );

// Programar la transacción para dentro de dos horas
        LocalDateTime fechaRecordatorio = LocalDateTime.now().plusHours(2);

        TransaccionProgramada programacionRecordatorio =
                new TransaccionProgramada(
                        depositoPruebaRecordatorio,
                        fechaRecordatorio,
                        Frecuencia.UNICA,
                        EstadoProgramacion.PENDIENTE,
                        null,
                        cliente1
                );

        plataforma.programarTransaccion(programacionRecordatorio);


        plataforma.procesarRecordatoriosProgramados();

        System.out.println("Alertas de Ana después de repetir: "
                + cliente1.getHistorialAlertas().size());

        for (Alerta alerta : cliente1.getHistorialAlertas()) {
            System.out.println("Mensaje: " + alerta.getMensaje());
            System.out.println("Fecha: " + alerta.getFecha());
        }

        try {
            plataforma.realizarRetiro("12345", "M001", 1250);
        } catch (MonederoNoEncontradoException | SaldoInsuficienteException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Saldo final de Ana: "
                + plataforma.consultarSaldo("12345", "M001"));

        for (Alerta alerta : cliente1.getHistorialAlertas()) {
            System.out.println("Alerta: " + alerta.getMensaje());
        }
        System.out.println("Rango de Ana: "
                + plataforma.consultarRango("12345"));
    }
}

