package consultorio.controlador;

import consultorio.DAO;
import consultorio.model.CitaCalendario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.control.Tooltip;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class CalendarController  {

    @FXML
    private Button mesAnterior;
    @FXML
    private Button mesSiguiente;
    @FXML
    private TextField labelMes;
    @FXML
    private GridPane gridDias;

    private LocalDate fechaActual;
    @Setter
    private MainController mainController;
    private DAO dao;
    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("h:mm a", new Locale("es","ES"));

    private Map<Integer, List<CitaCalendario>> eventosPorDia = new HashMap<>();

    @FXML
    private void initialize() {
        System.out.println("CalendarController inicializado");
        fechaActual = LocalDate.now();
        dao = new DAO();
        actualizarVista();
        cargarCitasMes();
        mostrarDias();
    }

    @FXML
    private void mesAnterior(ActionEvent event) {
        fechaActual = fechaActual.minusMonths(1);
        actualizarVista();
        cargarCitasMes();
        mostrarDias();
    }

    @FXML
    private void mesSiguiente(ActionEvent event) {
        fechaActual = fechaActual.plusMonths(1);
        actualizarVista();
        cargarCitasMes();
        mostrarDias();
    }

    private void actualizarVista() {
        Locale es = new Locale("es", "ES");
        LocalDate mesAnt = fechaActual.minusMonths(1);
        LocalDate mesSig = fechaActual.plusMonths(1);

        String mesActualStr = capitalizar(fechaActual.getMonth().getDisplayName(TextStyle.FULL, es));
        String mesAnteriorStr = capitalizar(mesAnt.getMonth().getDisplayName(TextStyle.FULL, es));
        String mesSiguienteStr = capitalizar(mesSig.getMonth().getDisplayName(TextStyle.FULL, es));

        labelMes.setText(mesActualStr + " " + fechaActual.getYear());
        mesAnterior.setText(mesAnteriorStr + " " + mesAnt.getYear() + " ←");
        mesSiguiente.setText("→ " + mesSiguienteStr + " " + mesSig.getYear());
    }
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private void cargarCitasMes() {
        eventosPorDia.clear();
        int year = fechaActual.getYear();
        int month = fechaActual.getMonthValue();
        List<CitaCalendario> citas = dao.getCitasForMonth(year, month);
        for (CitaCalendario c : citas) {
            if (c.getFechaHora() == null) continue;
            int dia = c.getFechaHora().getDayOfMonth();
            eventosPorDia.computeIfAbsent(dia, k -> new ArrayList<>()).add(c);
        }
    }

    private void mostrarDias() {
        gridDias.getChildren().clear();

        YearMonth yearMonth = YearMonth.of(fechaActual.getYear(), fechaActual.getMonth());
        int diasEnMes = yearMonth.lengthOfMonth();
        LocalDate primerDiaMes = fechaActual.withDayOfMonth(1);

        int diaSemana = primerDiaMes.getDayOfWeek().getValue()-1;
        int row = 0;
        int col = diaSemana;

        for (int dia = 1; dia <= diasEnMes; dia++) {
            Button celdaBoton = crearCeldaDiaConEventos(dia);
            gridDias.add(celdaBoton, col, row);

            GridPane.setHgrow(celdaBoton, Priority.ALWAYS);
            GridPane.setVgrow(celdaBoton, Priority.ALWAYS);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private Button crearCeldaDiaConEventos(int dia) {
        StackPane celda = new StackPane();
        celda.setStyle("-fx-border-color: #DDE3EA; -fx-border-width: 1 1 1 1;");

        VBox vbox = new VBox();
        vbox.setSpacing(4);
        vbox.setAlignment(Pos.TOP_LEFT);
        vbox.setMaxWidth(Double.MAX_VALUE);
        vbox.setMaxHeight(Double.MAX_VALUE);
        vbox.setStyle("-fx-padding: 6;");

        HBox header = new HBox();
        Pane espacio = new Pane();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label labelDia = new Label(String.valueOf(dia));
        labelDia.setStyle("-fx-text-fill:#6E7A8A; -fx-font-size:12px;");
        header.getChildren().addAll(espacio,labelDia);
        vbox.getChildren().add(header);

        List<CitaCalendario> citasDelDia = eventosPorDia.get(dia);
        if (citasDelDia != null && !citasDelDia.isEmpty()) {
            // ordenar por hora
            citasDelDia.sort(Comparator.comparing(CitaCalendario::getFechaHora));
            int maxMostrar = 4;
            int mostradas = 0;
            for (CitaCalendario c : citasDelDia) {
                if (mostradas >= maxMostrar) {
                    Label mas = new Label("… y " + (citasDelDia.size() - mostradas) + " más");
                    mas.setStyle("-fx-font-size:11px; -fx-text-fill:#4F5966;");
                    vbox.getChildren().add(mas);
                    break;
                }
                HBox evento = crearEventoNode(c);
                vbox.getChildren().add(evento);
                mostradas++;
            }
        }

        //creación de boton
        Button botonCelda = new Button();
        botonCelda.setGraphic(vbox);
        botonCelda.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        botonCelda.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        botonCelda.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        botonCelda.setMinSize(0, 0);
        botonCelda.setFocusTraversable(true);

        botonCelda.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border-color: #DDE3EA; -fx-border-width: 1; " +
                        "-fx-padding: 0;"
        );

        LocalDate fechaCelda = fechaActual.withDayOfMonth(dia);

        botonCelda.setOnAction(e -> {
            if (mainController != null){
                mainController.cambiarVistaCitasFecha(fechaCelda);
            } else {
                System.err.println("mainController es null — asigna setMainController(...) al inicializar.");
            }
        });

        botonCelda.setOnMouseEntered(e -> botonCelda.setStyle(
                "-fx-background-color: rgba(200,200,200,0.08); -fx-border-color: #DDE3EA; -fx-border-width: 1; -fx-padding: 0;"
        ));
        botonCelda.setOnMouseExited(e -> botonCelda.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #DDE3EA; -fx-border-width: 1; -fx-padding: 0;"
        ));


        celda.getChildren().add(vbox);
        return botonCelda;
    }

    private HBox crearEventoNode(CitaCalendario c) {
        HBox evento = new HBox(6);
        evento.setAlignment(Pos.CENTER_LEFT);

        Circle punto = new Circle(3, Color.web("#2D8CFF"));

        String horaStr = c.getFechaHora() != null ? c.getFechaHora().format(horaFormatter) : "";
        String detalle = horaStr;
        // mostrar paciente o tipo de consulta si existe
        if (c.getPacienteNombre() != null && !c.getPacienteNombre().isEmpty()) {
            detalle += " • " + truncate(c.getPacienteNombre(), 18);
        } else if (c.getTipoConsulta() != null && !c.getTipoConsulta().isEmpty()) {
            detalle += " • " + truncate(c.getTipoConsulta(), 18);
        }

        Label lbl = new Label(detalle);
        lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#1A1A1A;");

        // tooltip con la info completa
        Tooltip tip = new Tooltip();
        StringBuilder tb = new StringBuilder();
        tb.append(horaStr);
        if (c.getPacienteNombre() != null && !c.getPacienteNombre().isEmpty()) {
            tb.append(" - ").append(c.getPacienteNombre());
        }
        if (c.getTipoConsulta() != null && !c.getTipoConsulta().isEmpty()) {
            tb.append("\nTipo: ").append(c.getTipoConsulta());
        }
        tip.setText(tb.toString());
        Tooltip.install(evento, tip);

        evento.getChildren().addAll(punto, lbl);
        return evento;
    }

    private String truncate(String s, int len) {
        if (s == null) return "";
        return s.length() <= len ? s : s.substring(0, len-1) + "…";
    }

    // Metodo llamado desde MainController para volver al inicio
    public void volverInicio() {
        if (mainController != null) {
            mainController.cambiarVistaInicio();
        }
    }

    @FXML
    public void GenerarReporte() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/GenerarReporteView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Generar Reporte");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la principal hasta cerrar
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void hoverEnter(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setStyle("-fx-background-color: #bababa;-fx-border-color: #969696; -fx-border-radius: 8px;");
    }

    @FXML
    private void hoverExit(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setStyle("-fx-background-color: #D9D9D9; -fx-border-color: #969696; -fx-border-radius: 8px;");
    }

    public void recargarDatosExternamente(){
        System.out.println("Sincronizando Calendario");
        cargarCitasMes();
        mostrarDias();
    }
}