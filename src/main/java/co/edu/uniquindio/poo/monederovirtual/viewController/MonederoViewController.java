package co.edu.uniquindio.poo.monederovirtual.viewController;

import co.edu.uniquindio.poo.monederovirtual.controller.PlataformaController;
import co.edu.uniquindio.poo.monederovirtual.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MonederoViewController {
    private static final String CSS = "/co/edu/uniquindio/poo/monederovirtual/style.css";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PlataformaController ctrl = PlataformaController.getInstancia();
    private final Plataforma plataforma = ctrl.getPlataforma();
    private String css;

    @FunctionalInterface
    private interface Accion { void run() throws Exception; }

    // Cabecera
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Monedero> cbMonedero;
    @FXML private Label lblSaldo, lblEstado;
    // Operaciones
    @FXML private TextField txtMonto, txtMontoTransf;
    @FXML private ComboBox<Cliente> cbDestinoCliente;
    @FXML private ComboBox<Monedero> cbDestinoMonedero;
    // Historial
    @FXML private TableView<Transaccion> tblHistorial;
    // Puntos
    @FXML private Label lblPuntos, lblRango;
    @FXML private TableView<MovimientosPuntos> tblPuntos;
    @FXML private ListView<String> lvBeneficios;
    // Programadas
    @FXML private ComboBox<String> cbTipoProg;
    @FXML private TextField txtMontoProg, txtHora;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<Frecuencia> cbFrecuencia;
    @FXML private ComboBox<Cliente> cbDestinoClienteProg;
    @FXML private ComboBox<Monedero> cbDestinoMonederoProg;
    @FXML private TableView<TransaccionProgramada> tblProgramadas;
    // Análisis
    @FXML private TextArea txtAnalisis;
    @FXML private PieChart pcGastos;
    @FXML private ListView<String> lvAlertas;

    @FXML
    public void initialize() {
        css = getClass().getResource(CSS).toExternalForm();

        StringConverter<Cliente> convCliente = conv(c -> c.getNombre() + " (" + c.getIdentificacion() + ")");
        StringConverter<Monedero> convMonedero = conv(m -> m.getTipo() + " · " + m.getCodigoMonedero());
        cbCliente.setConverter(convCliente);
        cbDestinoCliente.setConverter(convCliente);
        cbDestinoClienteProg.setConverter(convCliente);
        cbMonedero.setConverter(convMonedero);
        cbDestinoMonedero.setConverter(convMonedero);
        cbDestinoMonederoProg.setConverter(convMonedero);

        cbTipoProg.setItems(FXCollections.observableArrayList("Depósito", "Transferencia"));
        cbTipoProg.getSelectionModel().selectFirst();
        cbFrecuencia.setItems(FXCollections.observableArrayList(Frecuencia.values()));
        cbFrecuencia.getSelectionModel().selectFirst();
        dpFecha.setValue(LocalDate.now());
        txtHora.setText(LocalTime.now().plusHours(1).withSecond(0).withNano(0).toString());
        cbDestinoClienteProg.setDisable(true);
        cbDestinoMonederoProg.setDisable(true);
        cbTipoProg.valueProperty().addListener((o, a, n) -> {
            boolean deposito = "Depósito".equals(n);
            cbDestinoClienteProg.setDisable(deposito);
            cbDestinoMonederoProg.setDisable(deposito);
        });

        configurarTablas();
        cbDestinoCliente.valueProperty().addListener((o, a, n) -> cargarDestino(cbDestinoCliente, cbDestinoMonedero));
        cbDestinoClienteProg.valueProperty().addListener((o, a, n) -> cargarDestino(cbDestinoClienteProg, cbDestinoMonederoProg));
        cbCliente.valueProperty().addListener((o, a, n) -> recargarMonederos());
        cbMonedero.valueProperty().addListener((o, a, n) -> refrescar());

        recargarClientes();
    }

    // ---------------------------------------------------------------- Operaciones

    @FXML
    private void onDepositar() {
        ejecutar(() -> {
            plataforma.realizarDeposito(idCliente(), codMonedero(), leerMonto(txtMonto));
            txtMonto.clear();
        }, "¡Depósito realizado! 💖");
    }

    @FXML
    private void onRetirar() {
        ejecutar(() -> {
            plataforma.realizarRetiro(idCliente(), codMonedero(), leerMonto(txtMonto));
            txtMonto.clear();
        }, "¡Retiro realizado! 🌷");
    }

    @FXML
    private void onTransferir() {
        ejecutar(() -> {
            Cliente dc = cbDestinoCliente.getValue();
            Monedero dm = cbDestinoMonedero.getValue();
            if (dc == null || dm == null) {
                throw new IllegalArgumentException("Elige el cliente y el monedero de destino");
            }
            plataforma.realizarTransferencia(idCliente(), codMonedero(),
                    dc.getIdentificacion(), dm.getCodigoMonedero(), leerMonto(txtMontoTransf));
            txtMontoTransf.clear();
        }, "¡Transferencia enviada! 💌");
    }

    @FXML
    private void onRevertir() {
        Transaccion t = tblHistorial.getSelectionModel().getSelectedItem();
        if (t == null) {
            mostrar(Alert.AlertType.INFORMATION, "Revertir", "Selecciona primero una transacción del historial 🌸");
            return;
        }
        ejecutar(() -> plataforma.revertirTransaccion(idCliente(), codMonedero(), t.getIdentificador()),
                "Transacción revertida 🌈");
    }

    // ---------------------------------------------------------------- Puntos

    @FXML
    private void onCanjeComision() {
        ejecutar(() -> plataforma.canjearReduccionComision(idCliente()), "¡Canjeaste la reducción de comisión! 🎀");
    }

    @FXML
    private void onCanjeRetiros() {
        ejecutar(() -> plataforma.canjearRetirosSinCargos(idCliente()), "¡Canjeaste un mes sin cargos! 🎀");
    }

    @FXML
    private void onCanjeBono() {
        ejecutar(() -> plataforma.canjearBonoSaldo(idCliente(), codMonedero()), "¡Bono de 50 unidades añadido! 💰");
    }

    // ---------------------------------------------------------------- Programadas

    @FXML
    private void onProgramar() {
        ejecutar(() -> {
            double monto = leerMonto(txtMontoProg);
            if (dpFecha.getValue() == null) {
                throw new IllegalArgumentException("Elige una fecha");
            }
            LocalDateTime fecha = LocalDateTime.of(dpFecha.getValue(), LocalTime.parse(txtHora.getText().trim()));
            Monedero destino = null;
            if ("Transferencia".equals(cbTipoProg.getValue())) {
                destino = cbDestinoMonederoProg.getValue();
                if (destino == null) {
                    throw new IllegalArgumentException("Elige el monedero de destino de la transferencia");
                }
            }
            ctrl.programar(cbCliente.getValue(), cbMonedero.getValue(), destino, monto, fecha, cbFrecuencia.getValue());
            txtMontoProg.clear();
        }, "Transacción programada ⏰");
    }

    @FXML
    private void onProcesarProgramadas() {
        ejecutar(plataforma::procesarTransaccionesProgramadas, "Programadas vencidas procesadas 🚀");
    }

    @FXML
    private void onRecordatorios() {
        ejecutar(plataforma::procesarRecordatoriosProgramados, "Recordatorios revisados 🔔");
    }

    @FXML
    private void onCancelarProgramada() {
        TransaccionProgramada p = tblProgramadas.getSelectionModel().getSelectedItem();
        if (p == null) {
            mostrar(Alert.AlertType.INFORMATION, "Cancelar", "Selecciona primero una programación 🌸");
            return;
        }
        ejecutar(p::cancelar, "Programación cancelada");
    }

    // ---------------------------------------------------------------- Clientes y monederos

    @FXML
    private void onNuevoCliente() {
        pedirDatos("Nuevo cliente 🎀", "Nombre", "Identificación", "Correo", "Teléfono").ifPresent(d -> {
            try {
                String nombre = d.get(0), id = d.get(1), correo = d.get(2), tel = d.get(3);
                if (nombre.isEmpty() || !id.matches("[A-Za-z0-9]+")) {
                    throw new IllegalArgumentException("Escribe un nombre y una identificación (letras y números)");
                }
                if (!correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
                    throw new IllegalArgumentException("El correo no tiene un formato válido");
                }
                if (!tel.matches("\\d{7,12}")) {
                    throw new IllegalArgumentException("El teléfono debe tener entre 7 y 12 dígitos");
                }
                Cliente nuevo = new Cliente(nombre, id, 0, correo, tel);
                plataforma.registrarCliente(nuevo);
                recargarClientes();
                cbCliente.getSelectionModel().select(nuevo);
                estado("Cliente " + nombre + " registrado 🌸", false);
            } catch (IllegalArgumentException e) {
                error(e.getMessage());
            }
        });
    }

    @FXML
    private void onNuevoMonedero() {
        Cliente c = cbCliente.getValue();
        if (c == null) {
            mostrar(Alert.AlertType.INFORMATION, "Monedero", "Primero elige un cliente 🌸");
            return;
        }
        pedirDatos("Nuevo monedero 👛", "Código (ej. M010)", "Tipo (Ahorros, Gastos...)").ifPresent(d -> {
            try {
                if (d.get(0).isEmpty() || d.get(1).isEmpty()) {
                    throw new IllegalArgumentException("El código y el tipo son obligatorios");
                }
                plataforma.crearMonedero(c.getIdentificacion(), d.get(0), d.get(1));
                recargarMonederos();
                cbMonedero.getSelectionModel().select(c.getListMonederos().get(c.getListMonederos().size() - 1));
                estado("Monedero creado 👛", false);
            } catch (IllegalArgumentException e) {
                error(e.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------- Refrescar pantalla

    private void recargarClientes() {
        Cliente sel = cbCliente.getValue();
        ObservableList<Cliente> lista = FXCollections.observableArrayList(plataforma.getListclientes());
        cbCliente.setItems(lista);
        cbDestinoCliente.setItems(lista);
        cbDestinoClienteProg.setItems(lista);
        if (sel != null && lista.contains(sel)) {
            cbCliente.getSelectionModel().select(sel);
        } else if (!lista.isEmpty()) {
            cbCliente.getSelectionModel().selectFirst();
        }
        recargarMonederos();
    }

    private void recargarMonederos() {
        Cliente c = cbCliente.getValue();
        Monedero sel = cbMonedero.getValue();
        ObservableList<Monedero> lista = FXCollections.observableArrayList(
                c == null ? new ArrayList<Monedero>() : c.getListMonederos());
        cbMonedero.setItems(lista);
        if (sel != null && lista.contains(sel)) {
            cbMonedero.getSelectionModel().select(sel);
        } else if (!lista.isEmpty()) {
            cbMonedero.getSelectionModel().selectFirst();
        } else {
            cbMonedero.getSelectionModel().clearSelection();
        }
        cargarDestino(cbDestinoCliente, cbDestinoMonedero);
        cargarDestino(cbDestinoClienteProg, cbDestinoMonederoProg);
        refrescar();
    }

    private void cargarDestino(ComboBox<Cliente> clientes, ComboBox<Monedero> monederos) {
        Cliente c = clientes.getValue();
        monederos.setItems(FXCollections.observableArrayList(
                c == null ? new ArrayList<Monedero>() : c.getListMonederos()));
    }

    private void refrescar() {
        Cliente c = cbCliente.getValue();
        Monedero m = cbMonedero.getValue();
        lblSaldo.setText(m == null ? "—" : dinero(m.getSaldo()));

        List<Transaccion> historial = m == null ? new ArrayList<Transaccion>()
                : m.getListTransaccion().stream()
                .sorted(Comparator.comparing(Transaccion::getFecha).reversed())
                .collect(Collectors.toList());
        tblHistorial.setItems(FXCollections.observableArrayList(historial));

        refrescarPuntos(c);
        tblProgramadas.setItems(FXCollections.observableArrayList(
                c == null ? new ArrayList<TransaccionProgramada>() : ctrl.programacionesDe(c)));
        refrescarAnalisis(c);
    }

    private void refrescarPuntos(Cliente c) {
        if (c == null) {
            lblPuntos.setText("—");
            lblRango.setText("—");
            tblPuntos.getItems().clear();
            lvBeneficios.getItems().clear();
            return;
        }
        lblPuntos.setText(c.getPuntosAcumulados() + " ✨");
        Rango rango = plataforma.consultarRango(c.getIdentificacion());
        String emoji = rango == Rango.PLATINO ? "💎" : rango == Rango.ORO ? "🥇"
                : rango == Rango.PLATA ? "🥈" : "🥉";
        lblRango.setText(emoji + " " + rango.name());
        tblPuntos.setItems(FXCollections.observableArrayList(c.getHistorialPuntos()));
        lvBeneficios.setItems(FXCollections.observableArrayList(c.getBeneficiosCanjeados().stream()
                .map(b -> "🎁 " + b.getNombreBeneficio() + " · canjeado " + FMT.format(b.getFechaCanje())
                        + (b.getFechaVencimiento() == null ? "" : " · vence " + FMT.format(b.getFechaVencimiento())))
                .collect(Collectors.toList())));
    }

    private void refrescarAnalisis(Cliente c) {
        if (c == null) {
            txtAnalisis.clear();
            pcGastos.getData().clear();
            lvAlertas.getItems().clear();
            return;
        }
        AnalizadorTransacciones an = ctrl.getAnalizador();
        AnalisisGastos ag = an.getAnalisisGastos();
        txtAnalisis.setText(String.join("\n",
                "💸 Gastos (retiros y transferencias enviadas)",
                "Total: " + dinero(ag.calcularTotalGastos(c)),
                "Promedio: " + dinero(ag.calcularPromedioGastos(c)),
                "Mayor: " + dinero(ag.obtenerGastoMayor(c)) + "   Menor: " + dinero(ag.obtenerGastoMenor(c)),
                "",
                "🔍 Detección de transacciones atípicas",
                "Promedio histórico: " + dinero(an.calcularPromedioMontos(c)),
                "Desviación estándar: " + dinero(an.calcularDesviacionEstandar(c)),
                "Límite (promedio + 2 × desviación): " + dinero(an.calcularLimiteAtipico(c))));

        pcGastos.setData(FXCollections.observableArrayList(ag.calcularTotalPorTipo(c).entrySet().stream()
                .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));

        lvAlertas.setItems(FXCollections.observableArrayList(c.getHistorialAlertas().stream()
                .sorted(Comparator.comparing(Alerta::getFecha).reversed())
                .map(a -> "🔔 " + FMT.format(a.getFecha()) + " — " + a.getMensaje())
                .collect(Collectors.toList())));
    }

    private void configurarTablas() {
        tblHistorial.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        col(tblHistorial, "Tipo", this::describirTipo);
        col(tblHistorial, "Monto", t -> dinero(t.getMonto()));
        col(tblHistorial, "Fecha", t -> FMT.format(t.getFecha()));
        col(tblHistorial, "Estado", t -> t.getEstado().name());
        col(tblHistorial, "Alerta", t -> t.isSospechosa() ? "⚠️ Sospechosa" : "✓");

        tblPuntos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        col(tblPuntos, "Movimiento", m -> m.getTipoMovimiento().name());
        col(tblPuntos, "Puntos", m -> (m.getCantidad() > 0 ? "+" : "") + m.getCantidad());
        col(tblPuntos, "Transacción", m -> m.getTransaccion() == null ? "—"
                : describirTipo(m.getTransaccion()) + " " + dinero(m.getTransaccion().getMonto()));

        tblProgramadas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        col(tblProgramadas, "Tipo", p -> p.getTransaccion() instanceof Deposito ? "Depósito" : "Transferencia");
        col(tblProgramadas, "Monto", p -> dinero(p.getTransaccion().getMonto()));
        col(tblProgramadas, "Próxima ejecución", p -> FMT.format(p.getFechaEjecucion()));
        col(tblProgramadas, "Frecuencia", p -> p.getFrecuencia().name());
        col(tblProgramadas, "Estado", p -> p.getEstado().name());

        tblHistorial.setPlaceholder(new Label("Aún no hay movimientos 🌸"));
        tblProgramadas.setPlaceholder(new Label("No hay transacciones programadas ⏰"));
        tblPuntos.setPlaceholder(new Label("Sin movimientos de puntos"));
    }

    // ---------------------------------------------------------------- Utilidades

    /** Ejecuta una acción del model, muestra el resultado y avisa si se generó una alerta nueva. */
    private void ejecutar(Accion accion, String exito) {
        try {
            if (cbCliente.getValue() == null || cbMonedero.getValue() == null) {
                throw new IllegalArgumentException("Primero elige (o crea) un cliente y un monedero 🌸");
            }
            Cliente c = cbCliente.getValue();
            int alertasAntes = c.getHistorialAlertas().size();
            accion.run();
            estado(exito, false);
            refrescar();
            List<Alerta> alertas = c.getHistorialAlertas();
            if (alertas.size() > alertasAntes) {
                mostrar(Alert.AlertType.WARNING, "Nueva alerta 🔔", alertas.get(alertas.size() - 1).getMensaje());
            }
        } catch (NumberFormatException e) {
            error("El monto no es válido, escribe solo números 🥺");
        } catch (DateTimeParseException e) {
            error("La hora debe tener el formato HH:mm ⏰");
        } catch (SaldoInsuficienteException | PuntosInsuficientesException | MonederoNoEncontradoException
                 | IllegalArgumentException | IllegalStateException e) {
            error(e.getMessage());
        } catch (Exception e) {
            error("Algo salió mal: " + e.getMessage());
        } finally {
            refrescar();
        }
    }

    private double leerMonto(TextField campo) {
        double monto = Double.parseDouble(campo.getText().trim().replace(",", "."));
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        return monto;
    }

    private String idCliente() { return cbCliente.getValue().getIdentificacion(); }

    private String codMonedero() { return cbMonedero.getValue().getCodigoMonedero(); }

    private String describirTipo(Transaccion t) {
        if (t instanceof Deposito) return "Depósito";
        if (t instanceof Retiro) return "Retiro";
        if (t instanceof Transferencia) {
            return ((Transferencia) t).getOrigen() == cbMonedero.getValue()
                    ? "Transferencia enviada" : "Transferencia recibida";
        }
        return "Transacción";
    }

    private String dinero(double v) { return String.format("$ %,.2f", v); }

    private <T> void col(TableView<T> tabla, String titulo, Function<T, String> valor) {
        TableColumn<T, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(d -> new SimpleStringProperty(valor.apply(d.getValue())));
        tabla.getColumns().add(c);
    }

    private <T> StringConverter<T> conv(Function<T, String> f) {
        return new StringConverter<T>() {
            @Override public String toString(T o) { return o == null ? "" : f.apply(o); }
            @Override public T fromString(String s) { return null; }
        };
    }

    private void estado(String mensaje, boolean esError) {
        lblEstado.setText(mensaje);
        lblEstado.getStyleClass().removeAll("estado-ok", "estado-error");
        lblEstado.getStyleClass().add(esError ? "estado-error" : "estado-ok");
    }

    private void error(String mensaje) {
        estado("(｡•́︿•̀｡) " + mensaje, true);
        mostrar(Alert.AlertType.ERROR, "¡Ups!", mensaje);
    }

    private void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo, mensaje, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.getDialogPane().getStylesheets().add(css);
        a.showAndWait();
    }

    private java.util.Optional<List<String>> pedirDatos(String titulo, String... campos) {
        Dialog<List<String>> d = new Dialog<>();
        d.setTitle(titulo);
        d.setHeaderText(titulo);
        d.getDialogPane().getStylesheets().add(css);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(16));
        List<TextField> campoTexto = new ArrayList<>();
        for (int i = 0; i < campos.length; i++) {
            TextField tf = new TextField();
            g.add(new Label(campos[i]), 0, i);
            g.add(tf, 1, i);
            campoTexto.add(tf);
        }
        d.getDialogPane().setContent(g);
        d.setResultConverter(bt -> bt == ButtonType.OK
                ? campoTexto.stream().map(t -> t.getText().trim()).collect(Collectors.toList()) : null);
        return d.showAndWait();
    }
}
