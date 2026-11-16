package co.edu.uniquindio.poo.monederovirtual.model;

public class MonederoNoEncontradoException extends Exception {
    public MonederoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
