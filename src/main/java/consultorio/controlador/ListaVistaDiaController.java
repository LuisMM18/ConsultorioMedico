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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ListaVistaDiaController {

    @FXML
    private VBox appointmentContainer; // el contenedor de todas las citas

    @FXML
    private Button btnNuevo, btnGuardar;

    private final DateTimeFormatter horaFmt =
            DateTimeFormatter.ofPattern("h:mm a", new Locale("es", "MX"));
    @FXML
    private Label fechaLabel;
    private LocalDate fechaHoy;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("EEEE d ' / ' MMMM ' / ' yyyy", new Locale("es", "MX"));

    @FXML
    private void initialize() {
        // valor por defecto si nadie setea la fecha (se carga "hoy")
        fechaHoy = LocalDate.now(ZoneId.of("America/Hermosillo"));
        actualizarLabelFecha();
        // carga las citas de la fecha actual (puede re-ejecutarse si MainController llama setFecha después)
        cargarCitasParaFechaAsync();
    }

    public void setFecha(LocalDate fecha) {
        this.fechaHoy = fecha != null ? fecha : LocalDate.now(ZoneId.of("America/Hermosillo"));
        // actualizar label y recargar citas de la nueva fecha
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
                return dao.getCitasCalendarioForDate(fechaHoy); // método agregado en DAO
            }
        };

        task.setOnSucceeded(evt -> {
            List<CitaCalendario> citas = task.getValue();
            appointmentContainer.getChildren().clear();
            if (citas != null && !citas.isEmpty()) {
                for (CitaCalendario c : citas) {
                    // c.getFechaHora() es LocalDateTime
                    String horaStr = c.getFechaHora() != null ? c.getFechaHora().format(horaFmt) : "";
                    String descripcion = c.getPacienteNombre() != null ? c.getPacienteNombre() : "(sin paciente)";
                    /*
                    try{
                        descripcion = c.getPacienteNombre();
                    } catch (Exception ex) {
                        descripcion = c.getTipoConsulta() != null ? c.getTipoConsulta() : "(sin descripción)";
                    }
                    */
                    //String descripcion = c.getPacienteNombre();
                    //String descripcion = "prueba"; // reemplaza si tu modelo tiene descripción o paciente
                    // si tu modelo tiene paciente, p.ej. c.getPacienteNombre() -> descripción
                    VBox nodo = crearCita(horaStr, descripcion);
                    appointmentContainer.getChildren().add(nodo);
                }
            } else {
                // opcional: mostrar mensaje de "no hay citas"
                Label nada = new Label("No hay citas para este día");
                appointmentContainer.getChildren().add(nada);
            }
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            // opcional: mostrar alert en UI
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR, "Error cargando citas: " + ex.getMessage(), ButtonType.OK);
                a.showAndWait();
            });
        });

        new Thread(task, "carga-citas-hoy").start();
    }

    private String titleCaseSegments(String s) {
        String[] partes = s.split(" / ");
        for (int i = 0; i < partes.length; i++) {
            String seg = partes[i].trim();
            if (seg.isEmpty()) continue;
            int firstSpace = seg.indexOf(' ');
            if (firstSpace == -1) {
                partes[i] = capitalizeFirst(seg);
            } else {
                String first = seg.substring(0, firstSpace);
                String rest  = seg.substring(firstSpace);
                partes[i] = capitalizeFirst(first) + rest;
            }
        }
        return String.join(" / ", partes);
    }

    private String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
    public void cargarDatosDeHoy(Connection conn) throws SQLException {
        // SQL que usa rango [inicio, inicioDelSiguienteDia)
        String sql = "SELECT * FROM citas WHERE fechaHora >= ? AND fechaHora < ? ORDER BY fechaHora";

        // Si usas ZoneId especifico (lo definiste antes)
        ZoneId zone = ZoneId.of("America/Hermosillo");

        // Inicio del día en la zona y el inicio del siguiente día
        ZonedDateTime zInicio = fechaHoy.atStartOfDay(zone);
        ZonedDateTime zFin = fechaHoy.plusDays(1).atStartOfDay(zone);

        Timestamp tsInicio = Timestamp.from(zInicio.toInstant());
        Timestamp tsFin = Timestamp.from(zFin.toInstant());

        DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH:mm");

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, tsInicio);
            ps.setTimestamp(2, tsFin);

            try (ResultSet rs = ps.executeQuery()) {
                // Limpiar contenedor antes de poblar
                if (appointmentContainer != null) appointmentContainer.getChildren().clear();

                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("fechaHora");
                    String descripcion = rs.getString("descripcion"); // ajusta nombre de columna
                    // Convertir timestamp a LocalDateTime y formatear sólo la hora
                    String horaStr = ts.toLocalDateTime().format(horaFmt);

                    // Crear y añadir la cita a la UI
                    VBox cita = crearCita(horaStr, descripcion != null ? descripcion : "");
                    appointmentContainer.getChildren().add(cita);
                }
            }
        }
    }
    public LocalDate getFechaHoy() {
        return fechaHoy;
    }
    //-----------------------------------------------------------------------------------------------------------------------------------

    @FXML
    private void onNuevo() {
        //VBox nuevaCita = crearCita("Hora nueva", "Nueva cita sin descripción");
        //appointmentContainer.getChildren().add(nuevaCita);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/AgendarNuevaCitaView.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Nueva Cita");
                stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la principal hasta cerrar
                stage.setScene(new Scene(root));
                stage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    @FXML
    private void onGuardar() {
        System.out.println("Guardar todos los cambios en base de datos o archivo...");
    }

    @FXML
    private void onEditar(javafx.event.ActionEvent event) {
        Button boton = (Button) event.getSource();
        VBox cita = (VBox) boton.getParent().getParent();

        Label lblHora = null;
        Label lblDescripcion = null;

        for (var node : cita.getChildren()) {
            if (node instanceof HBox header) {
                for (var subNode : header.getChildren()) {
                    if (subNode instanceof Label label) lblHora = label;
                }
            } else if (node instanceof Label label) {
                lblDescripcion = label;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/EdicionDeCita.fxml"));
            Parent root = loader.load();

            EdiciondeCitaController controller = loader.getController();
            controller.setDatos(lblHora.getText(), lblDescripcion.getText());

            Stage stage = new Stage();
            stage.setTitle("Editar Cita");
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isGuardado()) {
                lblDescripcion.setText(controller.getNuevoDescripcion());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onEliminar(javafx.event.ActionEvent event) {
        Button boton = (Button) event.getSource();
        VBox citaOriginal = null;
        if (boton.getParent() instanceof HBox && boton.getParent().getParent() instanceof VBox) {
            citaOriginal = (VBox) boton.getParent().getParent();
        }

        if (citaOriginal != null) {
            // Creamos una variable final para usar en la lambda
            final VBox citaParaEliminar = citaOriginal;

            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmar Eliminación");
            alerta.setHeaderText("¿Estás seguro de que deseas eliminar esta cita?");
            alerta.setContentText("Esta acción no se puede deshacer.");

            alerta.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    // Usamos la nueva variable que sí es final
                    appointmentContainer.getChildren().remove(citaParaEliminar);
                }
            });
        }
    }

    private VBox crearCita(String hora, String descripcionTexto) {
        VBox cita = new VBox();
        cita.setStyle("-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: white;");
        cita.setSpacing(5);

        HBox header = new HBox();
        header.setSpacing(5);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblHora = new Label(hora);
        lblHora.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        lblHora.setMaxWidth(Double.MAX_VALUE);
        lblHora.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(lblHora, javafx.scene.layout.Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button btnEliminar = new Button("X");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-font-size: 20px; -fx-font-weight: bold;");
        btnEliminar.setOnAction(this::onEliminar);

        header.getChildren().addAll(lblHora, spacer, btnEliminar);

        Label descripcion = new Label(descripcionTexto);
        descripcion.setStyle("-fx-font-size: 20px;");
        descripcion.setWrapText(true);

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #9ADDFF; -fx-text-fill: white; -fx-font-weight: bold;");
        btnEditar.setOnAction(this::onEditar);

        HBox pie = new HBox(btnEditar);
        pie.setAlignment(javafx.geometry.Pos.BOTTOM_RIGHT);

        cita.getChildren().addAll(header, descripcion, pie);
        return cita;
    }}
