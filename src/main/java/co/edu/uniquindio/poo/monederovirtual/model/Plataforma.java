package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Comparator;

public class Plataforma {
    private Puntos puntos;
    private AnalizadorTransacciones analizadorTransacciones;
    private ArrayList<Cliente> listclientes;
    private List<TransaccionProgramada> transaccionesProgramadas;




    public Plataforma() {
        this.puntos = new Puntos();
        this.listclientes = new ArrayList<>();
        this.transaccionesProgramadas = new ArrayList<>();
        this.analizadorTransacciones=new AnalizadorTransacciones();
    }

    public Puntos getPuntos() {
        return puntos;
    }

    public void setPuntos(Puntos puntos) {
        this.puntos = puntos;
    }

    public ArrayList<Cliente> getListclientes() {
        return listclientes;
    }

    public void setListclientes(ArrayList<Cliente> listclientes) {
        this.listclientes = listclientes;
    }

    public List<TransaccionProgramada> getTransaccionesProgramadas() { return transaccionesProgramadas;}

    public void setTransaccionesProgramadas(List<TransaccionProgramada> transaccionesProgramadas) {
        this.transaccionesProgramadas = transaccionesProgramadas;
    }

    //registrar cliente
    public void registrarCliente(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException("El contenido no puede estar vacio");
        }
        Optional<Cliente> clienteEncontrado = buscarCliente(cliente.getIdentificacion());
        if (clienteEncontrado.isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con el código "
                    + cliente.getIdentificacion());

        }
        listclientes.add(cliente);
    }

    //buscar
    public Optional<Cliente> buscarCliente(String identificacion) {
        return listclientes.stream()
                .filter(c -> c.getIdentificacion().equals(identificacion))
                .findFirst();
    }

    //crear monedero
    public void crearMonedero(String identificacion, String codigoMonedero, String tipo) {
        Optional<Cliente> clienteEncontrado = buscarCliente(identificacion);
        if (clienteEncontrado.isEmpty()) {
            throw new IllegalArgumentException("el cliente no existe");
        }
        Cliente cliente = clienteEncontrado.get();

        if (cliente.getListMonederos().stream()
                .anyMatch(m -> m.getCodigoMonedero().equals(codigoMonedero))) {
            throw new IllegalArgumentException("El cliente ya tiene un monedero con ese código");
        }
        Monedero monedero = new Monedero(codigoMonedero, 0, tipo);
        cliente.agregarMonedero(monedero);
    }

    //buscarMonedero
    public Optional<Monedero> buscarMonedero(Cliente cliente, String codigoMonedero) {
        return cliente.getListMonederos().stream()
                .filter(m -> m.getCodigoMonedero().equals(codigoMonedero))
                .findFirst();
    }

    //realizar deposito
    public void realizarDeposito(String identificacion, String codigoMonedero, double monto) {
        Optional<Cliente> clienteEncontrado = buscarCliente(identificacion);
        if (clienteEncontrado.isEmpty()) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        Cliente cliente = clienteEncontrado.get();
        Optional<Monedero> monederoEncontrado = buscarMonedero(cliente, codigoMonedero);
        if (monederoEncontrado.isEmpty()) {
            throw new IllegalArgumentException("El monedero no existe");
        }
        Monedero monedero = monederoEncontrado.get();
        Deposito deposito = new Deposito(
                monto,
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                monedero);

        deposito.ejecutar();

        analizadorTransacciones.detectarTransaccionesAtipicas(cliente, deposito);

        monedero.agregarTransaccion(deposito);

        int puntosGenerados = puntos.calcularPuntos(deposito);
        puntos.acumularPuntos(cliente, puntosGenerados, deposito);
    }

    //Realizar retiro
    public void realizarRetiro(String identificacion, String codigoMonedero, double monto) throws MonederoNoEncontradoException, SaldoInsuficienteException {
        Optional<Cliente> clienteEncontrado = buscarCliente(identificacion);
        if (clienteEncontrado.isEmpty()) {
            throw new IllegalArgumentException("no se encuentra al cliente");
        }
        Cliente cliente = clienteEncontrado.get();
        Optional<Monedero> monederoEncontrado = buscarMonedero(cliente, codigoMonedero);

        if (monederoEncontrado.isEmpty()) {
            throw new MonederoNoEncontradoException("no se encontro monedero");
        }
        Monedero monedero = monederoEncontrado.get();

        Retiro retiro = new Retiro(
                monto,
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                monedero);

        retiro.ejecutar();

        analizadorTransacciones.detectarTransaccionesAtipicas(cliente, retiro);
        monedero.agregarTransaccion(retiro);

        int puntosGenerados = puntos.calcularPuntos(retiro);
        puntos.acumularPuntos(cliente, puntosGenerados, retiro);
        verificarSaldoBajo(cliente, monedero);

        }


    //realizar transferencia
    public void realizarTransferencia(String identificacionOrigen, String codigoMonederoOrigen,
                                      String identificacionDestino, String codigoMonederoDestino, double monto) throws MonederoNoEncontradoException,
            SaldoInsuficienteException{

        Optional<Cliente> origenEncontrado = buscarCliente(identificacionOrigen);

        if (origenEncontrado.isEmpty()) {
            throw new IllegalArgumentException("Cliente de origen no encontrado");
        }

        Cliente clienteOrigen = origenEncontrado.get();
//wawawawa
        Optional<Cliente> destinoEncontrado = buscarCliente(identificacionDestino);

        if (destinoEncontrado.isEmpty()) {
            throw new IllegalArgumentException("Cliente de destino no encontrado");
        }

        Cliente clienteDestino = destinoEncontrado.get();
//wawawawa
        Optional<Monedero> monederoOrigenEncontrado = buscarMonedero(clienteOrigen, codigoMonederoOrigen);
        if (monederoOrigenEncontrado.isEmpty()) {
            throw new MonederoNoEncontradoException("El monedero de origen no existe");
        }
        Optional<Monedero> monederoDestinoEncontrado = buscarMonedero(clienteDestino, codigoMonederoDestino);
        if (monederoDestinoEncontrado.isEmpty()) {
            throw new MonederoNoEncontradoException("El monedero de destino no existe");
        }

        Monedero monederoOrigen = monederoOrigenEncontrado.get();
        Monedero monederoDestino = monederoDestinoEncontrado.get();

        Transferencia transferencia = new Transferencia(
                monto,
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                monederoOrigen,
                monederoDestino
        );
        double comision = transferencia.calcularComision();

        if (puntos.tieneBeneficioActivo(clienteOrigen, "Reducción de comisión")) {
            comision *= 0.90;
        }

        transferencia.setComisionAplicada(comision);

            transferencia.ejecutar();


            monederoOrigen.agregarTransaccion(transferencia);
            monederoDestino.agregarTransaccion(transferencia);
            int puntosGenerados = transferencia.calcularPuntos();
            puntos.acumularPuntos(clienteOrigen, puntosGenerados, transferencia);
        verificarSaldoBajo(clienteOrigen, monederoOrigen);

    }
    //consultar saldo
    public double consultarSaldo(String identificacion, String codigoMonedero){
        Optional<Cliente>clienteEncontrado=buscarCliente(identificacion);
        if(clienteEncontrado.isEmpty()){
            throw new IllegalArgumentException("no se encontro al cliente");
        }
        Cliente cliente=clienteEncontrado.get();
        Optional<Monedero>monederoEncontrado=buscarMonedero(cliente, codigoMonedero);
        if(monederoEncontrado.isEmpty()){
            throw new IllegalArgumentException("monedero no encontrado");
        }
return monederoEncontrado.get().getSaldo();
    }
    //consultarHistorial
    public LinkedList<Transaccion> consultarHistorial(String identificacion, String codigoMonedero){
        Optional<Cliente>clienteEncontrado=buscarCliente(identificacion);
        if(clienteEncontrado.isEmpty()){
            throw new IllegalArgumentException("no se encontro al cliente");
        }
        Cliente cliente=clienteEncontrado.get();
        Optional<Monedero>monederoEncontrado=buscarMonedero(cliente, codigoMonedero);
        if(monederoEncontrado.isEmpty()){
            throw new IllegalArgumentException("monedero no encontrado");
        }
        return monederoEncontrado.get().getListTransaccion();
    }
    //buscar Transaccion
    public Optional<Transaccion> buscarTransaccion(Monedero monedero, String identificador) {
        return monedero.getListTransaccion().stream()
                .filter(t -> t.getIdentificador().equals(identificador))
                .findFirst();
    }
    //buscar propietario
    public Optional<Cliente> buscarPropietario(Monedero monedero) {
        return listclientes.stream()
                .filter(cliente -> cliente.getListMonederos().contains(monedero))
                .findFirst();
    }
    //revertirTransaccion
    public void revertirTransaccion(String identificacion, String codigoMonedero,
                                    String identificadorTransaccion) throws MonederoNoEncontradoException,
            SaldoInsuficienteException {
        Optional<Cliente>clienteEncontrado=buscarCliente(identificacion);
        if(clienteEncontrado.isEmpty()){
            throw new IllegalArgumentException("cliente no encontrado");
        }
        Cliente cliente=clienteEncontrado.get();
        Optional<Monedero>monederoEncontrado=buscarMonedero(cliente, codigoMonedero);
        if(monederoEncontrado.isEmpty()){
            throw new MonederoNoEncontradoException("no se encontro monedero");
        }
        Monedero monedero = monederoEncontrado.get();

        Optional<Transaccion>transaccionEncontrada=buscarTransaccion(monedero, identificadorTransaccion);
        if(transaccionEncontrada.isEmpty()){
            throw new IllegalArgumentException("no se encontro una transaccion");
        }
        Transaccion transaccion=transaccionEncontrada.get();

        if (transaccion instanceof Transferencia) {
            Transferencia transferencia = (Transferencia) transaccion;

            if (!transferencia.getOrigen().equals(monedero)) {
                throw new IllegalArgumentException(
                        "Solo el cliente de origen puede solicitar la reversión"
                );
            }
        }
            transaccion.revertir();
            puntos.registrarReversion(cliente, transaccion);


    }
    //canjear bonos saldo
    public void canjearBonoSaldo(String identificacion, String codigoMonedero) throws PuntosInsuficientesException {
        Optional<Cliente>clienteEncontrado=buscarCliente(identificacion);
        if(clienteEncontrado.isEmpty()){
            throw new IllegalArgumentException("no se encontro cliente");
        }
        Cliente cliente=clienteEncontrado.get();
        Optional<Monedero>monederoEncontrado=buscarMonedero(cliente, codigoMonedero);
        if(monederoEncontrado.isEmpty()){
            throw new IllegalArgumentException("no se encontro monedero");
        }
        Monedero monedero=monederoEncontrado.get();

        Beneficio beneficio = new Beneficio("Bono de saldo", 1000,
                "Bono de 50 unidades para el monedero");

        puntos.canjearPuntos(cliente, beneficio);
        monedero.aumentarSaldo(50);
    }
    //canjear reduccion comision
    public void canjearReduccionComision(String identificacion) throws PuntosInsuficientesException {
        Optional<Cliente> clienteEncontrado = buscarCliente(identificacion);

        if (clienteEncontrado.isEmpty()) {
            throw new IllegalArgumentException("No se encontró cliente");
        }

        Cliente cliente = clienteEncontrado.get();

        Beneficio beneficio = new Beneficio(
                "Reducción de comisión",
                100,
                "Reducción del 10% en la comisión por transferencias"
        );

        puntos.canjearPuntos(cliente, beneficio);
    }
    //canjear retiros sin cargos
    public void canjearRetirosSinCargos(String identificacion) throws PuntosInsuficientesException {
        Optional<Cliente> clienteEncontrado = buscarCliente(identificacion);

        if (clienteEncontrado.isEmpty()) {
            throw new IllegalArgumentException("No se encontró cliente");
        }

        Cliente cliente = clienteEncontrado.get();
        Beneficio beneficio = new Beneficio(
                "Retiro sin cargos",
                500,
                "un mes sin cargos por retiros"
        );
        puntos.canjearPuntos(cliente, beneficio);
    }
    //programar transaccion
    public void programarTransaccion(TransaccionProgramada programacion){
        if(programacion==null){
            throw new IllegalArgumentException("La programación no puede ser nula");
        }

        if (!programacion.estaPendiente()) {
            throw new IllegalStateException("La programación no está pendiente");
        }

        transaccionesProgramadas.add(programacion);

        }
// ordenar transsacciones programas
        public void ordenarTransaccionesProgramadas() {
            transaccionesProgramadas.sort(
                    Comparator.comparing(t -> t.getFechaEjecucion()));
        }
        //registrar en historial
    private void registrarEnHistorial(Transaccion transaccion) {
        if (transaccion instanceof Deposito) {
            Deposito deposito = (Deposito) transaccion;

            deposito.getMonederoDestino()
                    .agregarTransaccion(transaccion);
        }
        else if (transaccion instanceof Transferencia) {
            Transferencia transferencia = (Transferencia) transaccion;

            transferencia.getOrigen()
                    .agregarTransaccion(transaccion);

            transferencia.getDestino()
                    .agregarTransaccion(transaccion);
        }
    }
    //procesar transaccion programadas
    public void procesarTransaccionesProgramadas(){
            ordenarTransaccionesProgramadas();
            LocalDateTime ahora = LocalDateTime.now();

            for (TransaccionProgramada programacion : transaccionesProgramadas) {
                if (!programacion.estaPendiente()) {
                    continue;
                }

                if (programacion.getFechaEjecucion().isAfter(ahora)) {
                    continue;
                }
                Transaccion transaccionOriginal = programacion.getTransaccion();
                if (!(transaccionOriginal instanceof Programable)) {
                    throw new IllegalStateException(
                            "La transacción no permite programación"
                    );
                }
                Programable programable = (Programable) transaccionOriginal;

                Transaccion nuevaTransaccion =
                        programable.crearNuevaTransaccion(
                                programacion.getFechaEjecucion());
try {
    nuevaTransaccion.ejecutar();

    registrarEnHistorial(nuevaTransaccion);

    Cliente cliente = programacion.getClientePropietario();

    int puntosGenerados = nuevaTransaccion.calcularPuntos();

    puntos.acumularPuntos(
            cliente,
            puntosGenerados,
            nuevaTransaccion
    );

    programacion.actualizarProgramacion();
}catch(SaldoInsuficienteException e){
    System.out.println(e.getMessage());
}
            }
        }
        //metodo analizar transacciones
    public List<Transaccion> analizarTransacciones(Cliente cliente, Transaccion nuevaTransaccion){
        return analizadorTransacciones.detectarTransaccionesAtipicas(cliente, nuevaTransaccion);
    }
    //verificar saldo bajo
    private void verificarSaldoBajo(Cliente cliente, Monedero monedero) {
        if (monedero.getSaldo() < 100) {
            Alerta alerta = new Alerta(
                    "Tu saldo está por debajo de 100 unidades.",
                    null
            );

            cliente.agregarAlerta(alerta);
        }
    }
    //procesar recordatorios
    public void procesarRecordatoriosProgramados() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(24);

        for (TransaccionProgramada programacion : transaccionesProgramadas) {

            if (!programacion.estaPendiente()) {
                continue;
            }

            if (programacion.isRecordatorioEnviado()) {
                continue;
            }

            LocalDateTime fechaEjecucion = programacion.getFechaEjecucion();

            if (!fechaEjecucion.isAfter(limite) &&
                    fechaEjecucion.isAfter(ahora)) {

                Cliente cliente = programacion.getClientePropietario();

                Alerta alerta = new Alerta(
                        "Recuerda que tienes una transacción programada para: "
                                + fechaEjecucion,
                        null
                );

                cliente.agregarAlerta(alerta);

                programacion.marcarRecordatorioEnviado();
            }
        }
    }
    public Rango consultarRango(String identificacion) {
        Cliente cliente = buscarCliente(identificacion)
                .orElseThrow();

        return puntos.calcularRango(cliente);
    }
    }



