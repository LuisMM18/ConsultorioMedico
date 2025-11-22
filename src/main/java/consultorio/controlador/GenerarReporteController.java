package consultorio.controlador;

import consultorio.DAO;
import consultorio.model.CitaCalendario;
import consultorio.util.GeneradorReportes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class GenerarReporteController {

    private final DAO dao = new DAO();

    @FXML private ComboBox<String> tipoReporteCombo;
    @FXML private HBox panelMensual;
    @FXML private VBox panelSemanal;

    @FXML private ComboBox<String> mesCombo;
    @FXML private ComboBox<Integer> anioCombo;
    @FXML private DatePicker fechaSemanaPicker;
    @FXML private ComboBox<String> tipoFormatoCombo;

    @FXML private TextField rutaField;
    @FXML private Button generarButton;

    @FXML
    public void initialize() {
        tipoReporteCombo.getItems().addAll("Reporte Mensual", "Reporte Semanal");
        tipoFormatoCombo.getItems().addAll("PDF", "Excel (.xlsx)"); // Opciones de formato

        mesCombo.getItems().addAll(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );

        int anioActual = LocalDate.now().getYear();
        for (int i = anioActual - 5; i <= anioActual + 5; i++) {
            anioCombo.getItems().add(i);
        }
        anioCombo.setValue(anioActual);

        tipoReporteCombo.setOnAction(e -> actualizarVisibilidad());
        actualizarVisibilidad();
    }

    private void actualizarVisibilidad() {
        String seleccion = tipoReporteCombo.getValue();
        panelMensual.setVisible(false);
        panelMensual.setManaged(false);
        panelSemanal.setVisible(false);
        panelSemanal.setManaged(false);

        if ("Reporte Mensual".equals(seleccion)) {
            panelMensual.setVisible(true);
            panelMensual.setManaged(true);
        } else if ("Reporte Semanal".equals(seleccion)) {
            panelSemanal.setVisible(true);
            panelSemanal.setManaged(true);
        }
    }

    @FXML
    private void seleccionarDestino() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Seleccionar carpeta");
        File directorio = dc.showDialog(new Stage());
        if (directorio != null) {
            rutaField.setText(directorio.getAbsolutePath());
        }
    }

    @FXML
    private void generarReporte() {
        if (rutaField.getText().isEmpty()) {
            mostrarAlerta("Error", "Selecciona una carpeta de destino.");
            return;
        }
        if (tipoFormatoCombo.getValue() == null) {
            mostrarAlerta("Error", "Selecciona el formato (PDF o Excel).");
            return;
        }

        LocalDate fechaInicio, fechaFin;
        String tipo = tipoReporteCombo.getValue();

        if ("Reporte Mensual".equals(tipo)) {
            if (mesCombo.getValue() == null || anioCombo.getValue() == null) {
                mostrarAlerta("Error", "Falta seleccionar mes o año.");
                return;
            }
            int mes = mesCombo.getSelectionModel().getSelectedIndex() + 1;
            int anio = anioCombo.getValue();
            fechaInicio = LocalDate.of(anio, mes, 1);
            fechaFin = fechaInicio.with(TemporalAdjusters.lastDayOfMonth());
        } else {
            if (fechaSemanaPicker.getValue() == null) {
                mostrarAlerta("Error", "Selecciona una fecha para la semana.");
                return;
            }
            fechaInicio = fechaSemanaPicker.getValue().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            fechaFin = fechaSemanaPicker.getValue().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }

        List<CitaCalendario> listaCitas = dao.getCitasPorRango(fechaInicio, fechaFin);

        String nombreArchivo = "/Reporte_" + fechaInicio + "_" + tipoFormatoCombo.getValue();
        String rutaCompleta = rutaField.getText() + nombreArchivo;

        try {
            if (tipoFormatoCombo.getValue().contains("Excel")) {
                GeneradorReportes.generarExcel(listaCitas, rutaCompleta);
            } else {
                GeneradorReportes.generarPDF(listaCitas, rutaCompleta);
            }
            mostrarAlerta("Éxito", "Reporte guardado en:\n" + rutaCompleta);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "No se pudo crear el archivo: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        Stage stage = (Stage) generarButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}