package co.edu.uniquindio.poo.monederovirtual.model;

import java.util.*;

public class Monedero {
    private String codigoMonedero;
    private double saldo;
    private String tipo;

    private LinkedList<Transaccion> listTransaccion;

    public Monedero(String codigoMonedero, double saldo, String tipo) {
        this.codigoMonedero = codigoMonedero;
        this.saldo=saldo;
        this.tipo=tipo;
        this.listTransaccion = new LinkedList<>();
    }

    public String getCodigoMonedero() {
        return codigoMonedero;
    }

    public void setCodigoMonedero(String codigoMonedero) {
        this.codigoMonedero = codigoMonedero;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public LinkedList<Transaccion> getListTransaccion() {
        return listTransaccion;
    }

    public void setListTransaccion(LinkedList<Transaccion> listTransaccion) {
        this.listTransaccion = listTransaccion;
    }


    public void aumentarSaldo(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }

        saldo += monto;
    }

    public void disminuirSaldo(double monto) throws SaldoInsuficienteException {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }

        if (saldo < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        saldo -= monto;
    }

    public void agregarTransaccion(Transaccion transaccion) {
        listTransaccion.add(transaccion);
    }

}
