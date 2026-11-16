package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;

public class Alerta {
        private String mensaje;
        private LocalDateTime fecha;
        private Transaccion transaccion;

    public Alerta(String mensaje, Transaccion transaccion) {
        this.mensaje = mensaje;
        this.fecha=LocalDateTime.now();
        this.transaccion=transaccion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }
}
