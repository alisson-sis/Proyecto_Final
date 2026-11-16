package co.edu.uniquindio.poo.monederovirtual.model;

import java.util.*;

public class Cliente {
    private String nombre;
    private String identificacion;
    private String correo;
    private String telefono;
    private int puntosAcumulados;

    private ArrayList<Monedero> listMonederos;
    private ArrayList<MovimientosPuntos> historialPuntos;
    private ArrayList<Beneficio> beneficiosCanjeados;
    private List<Alerta> historialAlertas;


    public Cliente(String nombre, String identificacion, int puntosAcumulados,
                   String correo, String telefono) {
        this.nombre = nombre;
        this.identificacion=identificacion;
        this.puntosAcumulados=0;
        this.correo=correo;
        this.telefono=telefono;

        this.listMonederos=new ArrayList<>();
        this.historialPuntos = new ArrayList<>();
        this.beneficiosCanjeados = new ArrayList<>();
        this.historialAlertas=new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public int getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public void setPuntosAcumulados(int puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public ArrayList<Monedero> getListMonederos() {
        return listMonederos;
    }

    public ArrayList<MovimientosPuntos> getHistorialPuntos() {
        return historialPuntos;
    }

    public ArrayList<Beneficio> getBeneficiosCanjeados() {
        return beneficiosCanjeados;
    }

    public List<Alerta> getHistorialAlertas() {
        return historialAlertas;
    }

    public void agregarMovimientoPuntos(MovimientosPuntos movimiento) {
        historialPuntos.add(movimiento);
    }
    public void agregarMonedero(Monedero monedero) {
        listMonederos.add(monedero);
    }
    public void actualizarPuntos(int puntos) {
        if (puntosAcumulados + puntos < 0) {
            throw new IllegalArgumentException("Los puntos no pueden ser negativos");
        }
        puntosAcumulados += puntos;
    }
    public void agregarBeneficio(Beneficio beneficio) {
        beneficiosCanjeados.add(beneficio);
    }
    public void agregarAlerta(Alerta alerta) {historialAlertas.add(alerta);}
}
