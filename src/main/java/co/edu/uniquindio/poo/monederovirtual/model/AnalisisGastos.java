package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalisisGastos {
//obtenerGastos
    public List<Transaccion> obtenerGastos(Cliente cliente) {
        return cliente.getListMonederos().stream()
                .flatMap(m -> m.getListTransaccion().stream())
                .filter(t -> (t instanceof Retiro || t instanceof Transferencia) &&
                        t.getEstado() == EstadoTransaccion.COMPLETADA &&
                        (!(t instanceof Transferencia) ||
                                cliente.getListMonederos().contains(
                                        ((Transferencia) t).getOrigen()
                                ))).distinct().collect(Collectors.toList());
    }
//calcular total gastos
    public double calcularTotalGastos(Cliente cliente) {
        return obtenerGastos(cliente).stream()
                .mapToDouble(t -> t.getMonto())
                .sum();
    }
//calcular promedios gastos
    public double calcularPromedioGastos(Cliente cliente){
        return obtenerGastos(cliente).stream()
                .mapToDouble(t -> t.getMonto()).average().orElse(0.0);
    }
//obtener gastos mayor
    public double obtenerGastoMayor(Cliente cliente){
        return obtenerGastos(cliente).stream().mapToDouble(t -> t.getMonto())
                .max().orElse(0.0);
    }
//wawawa obtener gatos menores
    public double obtenerGastoMenor(Cliente cliente){
        return obtenerGastos(cliente).stream().mapToDouble(t -> t.getMonto())
                .min().orElse(0.0);
    }
//agrupar gastos por tipo
    public Map<String, List<Transaccion>> agruparGastosPorTipo(Cliente cliente){
return obtenerGastos(cliente).stream().collect(Collectors.groupingBy(t -> t instanceof Retiro ? "Retiro" : "Transferencia"));
    }
//calcular total por tipo
    public Map<String, Double> calcularTotalPorTipo(Cliente cliente){
        return obtenerGastos(cliente).stream()
                .collect(Collectors.groupingBy(t -> t instanceof Retiro ? "Retiro" : "Transferencia" ,  Collectors.summingDouble(t -> t.getMonto())));
    }
//calcular gastos opor periodo
    public double calcularGastoPorPeriodo(Cliente cliente, LocalDateTime fechaInicio,
                                          LocalDateTime fechaFin) {
        return obtenerGastos(cliente).stream()
                .filter(t ->
                        (t.getFecha().isAfter(fechaInicio) ||
                                t.getFecha().isEqual(fechaInicio))
                                &&
                                (t.getFecha().isBefore(fechaFin) ||
                                        t.getFecha().isEqual(fechaFin))
                )
                .mapToDouble(t -> t.getMonto())
                .sum();
    }
//obtener gastos por tipo y periodo
    public List<Transaccion> obtenerGastosPorTipoYPeriodo(Cliente cliente, String tipo, LocalDateTime fechaInicio,
            LocalDateTime fechaFin){
        return obtenerGastos(cliente).stream().filter(t -> tipo.equals(
                t instanceof Retiro ? "Retiro" : "Transferencia")).filter(t->(t.getFecha().isAfter(fechaInicio) || t.getFecha().isEqual(fechaInicio))
        && (t.getFecha().isBefore(fechaFin) || t.getFecha().isEqual(fechaFin))).collect(Collectors.toList());
    }
//obtener ultimoss gastos
    public List<Transaccion> obtenerUltimosGastos(Cliente cliente, int cantidad) {
        if (cantidad <= 0) {
        throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
    }
        return obtenerGastos(cliente).stream().sorted(Comparator.comparing(Transaccion::getFecha).reversed()).limit(cantidad).collect(Collectors.toList());
    }

    }

