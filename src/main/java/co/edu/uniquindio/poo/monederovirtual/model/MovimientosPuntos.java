package co.edu.uniquindio.poo.monederovirtual.model;

public class MovimientosPuntos {
        private int cantidad;
        private Transaccion transaccion;
        private TipoMovimientoPuntos tipoMovimiento;

        public MovimientosPuntos(int cantidad, Transaccion transaccion, TipoMovimientoPuntos tipoMovimiento) {
            this.cantidad = cantidad;
            this.transaccion = transaccion;
            this.tipoMovimiento = tipoMovimiento;
        }

    public int getCantidad() {
        return cantidad;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public TipoMovimientoPuntos getTipoMovimiento() {
        return tipoMovimiento;
    }

    //
}

