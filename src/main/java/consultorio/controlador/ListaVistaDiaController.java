package consultorio.controlador;

import consultorio.DAO;
import consultorio.model.CitaCalendario;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ListaVistaDiaController {

    @FXML private VBox appointmentContainer;
    @FXML private Label fechaLabel;
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;

    private LocalDate fechaHoy;
    private final DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("h:mm a", new Locale("es", "MX"));
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d ' / ' MMMM ' / ' yyyy", new Locale("es", "MX"));

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        fechaHoy = LocalDate.now(ZoneId.of("America/Hermosillo"));
        actualizarLabelFecha();
        cargarCitasParaFechaAsync();
    }

    public void setFecha(LocalDate fecha) {
        this.fechaHoy = fecha != null ? fecha : LocalDate.now(ZoneId.of("America/Hermosillo"));
        Platform.runLater(() -> {
            actualizarLabelFecha();
            cargarCitasParaFechaAsync();
        });
    }

    private void actualizarLabelFecha() {
        if (fechaLabel == null) return;
        String raw = fechaHoy.format(formatter);
        fechaLabel.setText(titleCaseSegments(raw));
    }

    private void cargarCitasParaFechaAsync() {
        Task<List<CitaCalendario>> task = new Task<>() {
            @Override
            protected List<CitaCalendario> call() throws Exception {
                DAO dao = new DAO();
                return dao.getCitasCalendarioForDate(fechaHoy);
            }
        };

        task.setOnSucceeded(evt -> {
            List<CitaCalendario> citas = task.getValue();
            appointmentContainer.getChildren().clear();

            if (citas != null && !citas.isEmpty()) {
                for (CitaCalendario c : citas) {
                    VBox nodo = crearCita(c);
                    appointmentContainer.getChildren().add(nodo);
                }
            } else {
                Label nada = new Label("No hay citas para este día");
                nada.setStyle("-fx-font-size: 16px; -fx-text-fill: grey; -fx-padding: 20;");
                appointmentContainer.getChildren().add(nada);
            }
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });

        new Thread(task, "carga-citas-hoy").start();
    }


    @FXML
    private void onNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/AgendarNuevaCitaView.fxml"));
            Parent root = loader.load();

            AgendarNuevaCitaController controller = loader.getController();
            if (this.mainController != null) {
                controller.setMainController(this.mainController);
            }

            Stage stage = new Stage();
            stage.setTitle("Nueva Cita");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarCitasParaFechaAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditar(javafx.event.ActionEvent event) {
        Button boton = (Button) event.getSource();
        VBox citaBox = (VBox) boton.getParent().getParent(); // Botón -> HBox -> VBox
        CitaCalendario citaObj = (CitaCalendario) citaBox.getUserData();

        if (citaObj == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/EdicionDeCita.fxml"));
            Parent root = loader.load();

            EdiciondeCitaController controller = loader.getController();
            controller.initData(citaObj);

            Stage stage = new Stage();
            stage.setTitle("Editar Cita");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isGuardado()) {
                System.out.println("Cambios detectados. Recargando lista...");

                try { Thread.sleep(100); } catch (InterruptedException ignored) {}

                cargarCitasParaFechaAsync();

                if (mainController != null) {
                    mainController.refrescarCalendarioSiEstaAbierto();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEliminar(javafx.event.ActionEvent event) {
        Button boton = (Button) event.getSource();
        VBox citaBox = (VBox) boton.getParent().getParent(); // Botón -> HBox -> VBox
        CitaCalendario citaObj = (CitaCalendario) citaBox.getUserData();

        if (citaObj == null) return;

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Eliminación");
        alerta.setHeaderText("¿Eliminar cita de " + citaObj.getPacienteNombre() + "?");
        alerta.setContentText("Esta acción marcará la cita como cancelada.");

        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                DAO dao = new DAO();
                boolean eliminado = dao.cancelarCita(citaObj.getIdCitas());

                if (eliminado) {
                    appointmentContainer.getChildren().remove(citaBox);

                    if (mainController != null) {
                        mainController.refrescarCalendarioSiEstaAbierto();
                    }
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR, "No se pudo eliminar en la BD");
                    error.show();
                }
            }
        });
    }

    @FXML
    private void onGuardar() {
        System.out.println("Botón Guardar presionado (Sin lógica asignada por ahora)");
    }

    private VBox crearCita(CitaCalendario c) {
        VBox cita = new VBox();
        cita.setUserData(c);

        cita.setStyle("-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        cita.setSpacing(5);

        String horaStr = c.getFechaHora() != null ? c.getFechaHora().format(horaFmt) : "--:--";
        String paciente = c.getPacienteNombre() != null ? c.getPacienteNombre() : "Sin Paciente";
        String tipo = c.getTipoConsulta() != null ? c.getTipoConsulta() : "";
        String descripcionTexto = paciente + (tipo.isEmpty() ? "" : " (" + tipo + ")");

        // Header (Hora + Eliminar)
        HBox header = new HBox();
        header.setSpacing(5);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblHora = new Label(horaStr);
        lblHora.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEliminar = new Button("✕");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnEliminar.setTooltip(new Tooltip("Cancelar Cita"));
        btnEliminar.setOnAction(this::onEliminar);

        header.getChildren().addAll(lblHora, spacer, btnEliminar);

        Label descripcion = new Label(descripcionTexto);
        descripcion.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e;");
        descripcion.setWrapText(true);

        Button btnEditar = new Button("Editar Detalles");
        btnEditar.setStyle("-fx-background-color: #9ADDFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        btnEditar.setOnAction(this::onEditar);

        HBox pie = new HBox(btnEditar);
        pie.setAlignment(javafx.geometry.Pos.BOTTOM_RIGHT);
        pie.setPadding(new javafx.geometry.Insets(5, 0, 0, 0));

        cita.getChildren().addAll(header, descripcion, pie);
        return cita;
    }

    private String titleCaseSegments(String s) {
        String[] partes = s.split(" / ");
        for (int i = 0; i < partes.length; i++) {
            String seg = partes[i].trim();
            if (!seg.isEmpty()) partes[i] = seg.substring(0,1).toUpperCase() + seg.substring(1);
        }
        return String.join(" / ", partes);
    }
}