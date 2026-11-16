package co.edu.uniquindio.poo.monederovirtual.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnalizadorTransacciones {
    private AnalisisGastos analisisGastos;
//
    public AnalizadorTransacciones() {
        this.analisisGastos =new AnalisisGastos();
    }

    public AnalisisGastos getAnalisisGastos() {
        return analisisGastos;
    }

    public void setAnalisisGastos(AnalisisGastos analisisGastos) {
        this.analisisGastos = analisisGastos;
    }
    //

    public double calcularPromedioMontos(Cliente cliente) {
        return obtenerTransaccionesHistoricas(cliente).stream()
                .mapToDouble(t -> t.getMonto())
                .average()
                .orElse(0.0);
    }
    public List<Transaccion> obtenerTransaccionesHistoricas(Cliente cliente) {
        return cliente.getListMonederos().stream()
                .flatMap(m -> m.getListTransaccion().stream())
                .filter(t -> t.getEstado() == EstadoTransaccion.COMPLETADA)
                .distinct()
                .collect(Collectors.toList());
    }
    public double calcularDesviacionEstandar(Cliente cliente){
        List<Transaccion> transacciones = obtenerTransaccionesHistoricas(cliente);

        if (transacciones.isEmpty()) {
            return 0.0;
        }
        double promedio = calcularPromedioMontos(cliente);
        double sumaDiferencias=0.0;
        for (Transaccion transaccion: transacciones) {
            double diferencia = transaccion.getMonto() - promedio;
            sumaDiferencias += diferencia * diferencia;
        }
            double varianza = sumaDiferencias / transacciones.size();

        return Math.sqrt(varianza);

    }
    public double calcularLimiteAtipico(Cliente cliente) {
        double promedio = calcularPromedioMontos(cliente);
        double desviacionEstandar = calcularDesviacionEstandar(cliente);
        double limite = promedio + (2 * desviacionEstandar);

        return limite;
    }
    public List<Transaccion> detectarTransaccionesAtipicas(
            Cliente cliente, Transaccion nuevaTransaccion) {

        if (cliente == null || nuevaTransaccion == null) {
            throw new IllegalArgumentException("Los datos no pueden ser nulos");
        }

        if (obtenerTransaccionesHistoricas(cliente).isEmpty()) {
            return new ArrayList<>();
        }

        double limite = calcularLimiteAtipico(cliente);
        List<Transaccion> atipicas = new ArrayList<>();

        if (nuevaTransaccion.getMonto() > limite) {
            atipicas.add(nuevaTransaccion);
        }

        for (Transaccion transaccion : atipicas) {
            if (!transaccion.isSospechosa()) {
                transaccion.setSospechosa(true);

                String mensaje = "Se ha detectado una transacción potencialmente sospechosa. "
                        + "El monto de la operación es superior al comportamiento habitual de tu cuenta.";

                Alerta alerta = new Alerta(mensaje, transaccion);

                cliente.agregarAlerta(alerta);
            }
        }

        return atipicas;
    }
}

