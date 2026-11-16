package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;

public class TransaccionProgramada {
    private Transaccion transaccion;
    private LocalDateTime fechaEjecucion;
    private Frecuencia frecuencia;
    private EstadoProgramacion estado;
    private LocalDateTime fechaFin;
    private Cliente clientePropietario;
    private boolean recordatorioEnviado;

    public TransaccionProgramada(Transaccion transaccion, LocalDateTime fechaEjecucion,
                                 Frecuencia frecuencia, EstadoProgramacion estado,
                                 LocalDateTime fechaFin, Cliente clientePropietario) {
        this.transaccion = transaccion;
        this.fechaEjecucion=fechaEjecucion;
        this.frecuencia=frecuencia;
        this.estado=EstadoProgramacion.PENDIENTE;
        this.fechaFin=fechaFin;
        this.clientePropietario=clientePropietario;
        this.recordatorioEnviado = false;

    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(LocalDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public Frecuencia getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Frecuencia frecuencia) {
        this.frecuencia = frecuencia;
    }

    public EstadoProgramacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoProgramacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Cliente getClientePropietario() {
        return clientePropietario;
    }

    public void setClientePropietario(Cliente clientePropietario) {
        this.clientePropietario = clientePropietario;
    }

    public boolean isRecordatorioEnviado() {
        return recordatorioEnviado;
    }

    public void setRecordatorioEnviado(boolean recordatorioEnviado) {
        this.recordatorioEnviado = recordatorioEnviado;
    }

    public void marcarRecordatorioEnviado() {
        this.recordatorioEnviado = true;
    }

    //
    public boolean estaPendiente(){
        if(estado==EstadoProgramacion.PENDIENTE){
            return true;
        }
        return false;
    }
    public void cancelar(){
        if(estado==EstadoProgramacion.PENDIENTE){
            estado=EstadoProgramacion.CANCELADA;
        }
    }
    public LocalDateTime calcularSiguienteFecha(){
        if(frecuencia==Frecuencia.UNICA){
            return null;
        }
        if(frecuencia==Frecuencia.DIARIA){
            return fechaEjecucion.plusDays(1);
        }
        if(frecuencia==Frecuencia.SEMANAL){
            return fechaEjecucion.plusWeeks(1);
        }
        if(frecuencia==Frecuencia.MENSUAL){
            return fechaEjecucion.plusMonths(1);
        }
        return null;
    }
    public void actualizarProgramacion() {
        if (estado != EstadoProgramacion.PENDIENTE) {
            return;
        }
        System.out.println("ENTRÓ A ACTUALIZAR");
        System.out.println("Frecuencia: " + frecuencia);
        System.out.println("Fecha actual: " + fechaEjecucion);

        LocalDateTime siguienteFecha = calcularSiguienteFecha();

        System.out.println("Fecha calculada: " + siguienteFecha);

        if (siguienteFecha == null) {
            estado = EstadoProgramacion.EJECUTADA;
            return;
        }

        if (fechaFin != null && siguienteFecha.isAfter(fechaFin)) {
            estado = EstadoProgramacion.EJECUTADA;
        } else {
            fechaEjecucion = siguienteFecha;
        }
    }
}
