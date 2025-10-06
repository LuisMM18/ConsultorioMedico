package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.awt.*;
import java.io.IOException;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;


public class CalendarController {

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

    ;

    @FXML
    private void initialize() {
        System.out.println("CalendarController inicializado");
        fechaActual = LocalDate.now();
        actualizarVista();
        mostrarDias();
    }

    // Botones del FXML (onAction="#mesAnterior" y "#mesSiguiente")
    @FXML
    private void mesAnterior(ActionEvent event) {
        // lógica para retroceder mes
        fechaActual = fechaActual.minusMonths(1);
        actualizarVista();
        mostrarDias();
    }

    @FXML
    private void mesSiguiente(ActionEvent event) {
        // lógica para avanzar mes
        fechaActual = fechaActual.plusMonths(1);
        actualizarVista();
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
    // Dibuja los días en el grid con tu mismo estilo visual
    private void mostrarDias() {
        gridDias.getChildren().clear();

        YearMonth yearMonth = YearMonth.of(fechaActual.getYear(), fechaActual.getMonth());
        int diasEnMes = yearMonth.lengthOfMonth();
        LocalDate primerDiaMes = fechaActual.withDayOfMonth(1);

        int diaSemana = primerDiaMes.getDayOfWeek().getValue(); // Lunes=1 ... Domingo=7
        if (diaSemana == 7) diaSemana = 0; // Ajustar domingo al inicio si quieres que empiece en lunes

        int row = 0;
        int col = diaSemana;

        for (int dia = 1; dia <= diasEnMes; dia++) {
            StackPane celda = crearCeldaDia(dia);
            gridDias.add(celda, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }
    private StackPane crearCeldaDia(int dia) {
        StackPane celda = new StackPane();
        celda.setStyle("-fx-border-color: #DDE3EA; -fx-border-width: 0 1 1 0;");

        VBox vbox = new VBox();
        vbox.setSpacing(4);
        vbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        vbox.setMaxWidth(Double.MAX_VALUE);
        vbox.setMaxHeight(Double.MAX_VALUE);
        vbox.setStyle("-fx-padding: 6;");

        HBox header = new HBox();
        Pane espacio = new Pane();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label labelDia = new Label(String.valueOf(dia));
        labelDia.setStyle("-fx-text-fill:#6E7A8A; -fx-font-size:12px;");
        header.getChildren().addAll(espacio,labelDia);

        // Ejemplo visual: agrega eventos a algunos días (puedes conectar tu BD aquí)
        if (dia == 5 || dia == 12 || dia == 20) {
            HBox evento = new HBox(8);
            evento.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Circle punto = new Circle(3, Color.web("#2D8CFF"));
            Label hora = new Label("4:00 pm");
            hora.setStyle("-fx-font-size:12px; -fx-text-fill:#1A1A1A;");
            evento.getChildren().addAll(punto,hora);
            vbox.getChildren().addAll(header, evento);
        } else {
            vbox.getChildren().add(header);
        }

        celda.getChildren().add(vbox);
        return celda;
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




}