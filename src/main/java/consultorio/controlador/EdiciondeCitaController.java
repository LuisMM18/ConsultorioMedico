package consultorio.controlador;

import consultorio.DAO;
import consultorio.model.CitaCalendario;
import consultorio.model.Paciente; // Asegúrate de importar tu modelo Paciente
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EdiciondeCitaController {

    // CAMBIO 1: Usamos ComboBox en lugar de TextField
    @FXML private ComboBox<Paciente> comboPacientes;

    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> comboHora;
    @FXML private TextField txtTipoConsulta;
    @FXML private TextArea txtNotas;

    private CitaCalendario citaActual;
    private final DAO dao = new DAO();
    private boolean seGuardo = false;

    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    @FXML
    public void initialize() {
        cargarHorarios();
        cargarPacientes(); // Cargar la lista de la BD
    }

    private void cargarHorarios() {
        List<String> horarios = new ArrayList<>();
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(20, 0);
        while (!inicio.isAfter(fin)) {
            horarios.add(inicio.format(horaFormatter));
            inicio = inicio.plusMinutes(30);
        }
        comboHora.getItems().addAll(horarios);
    }

    // Nuevo método para llenar el combo de pacientes
    private void cargarPacientes() {
        List<Paciente> lista = dao.getPacientesActivosBasico(); // Usamos el método que ya tienes en DAO
        comboPacientes.setItems(FXCollections.observableArrayList(lista));
    }

    public void initData(CitaCalendario cita) {
        this.citaActual = cita;

        // 1. Seleccionar el Paciente Correcto en el Combo
        // Buscamos en la lista el paciente cuyo ID coincida con el de la cita
        if (comboPacientes.getItems() != null) {
            for (Paciente p : comboPacientes.getItems()) {
                if (p.getIdPaciente() == cita.getIdPacienteRef()) {
                    comboPacientes.getSelectionModel().select(p);
                    break;
                }
            }
        }

        // 2. Llenar Fecha
        if (cita.getFechaHora() != null) {
            dpFecha.setValue(cita.getFechaHora().toLocalDate());
            LocalTime hora = cita.getFechaHora().toLocalTime();
            String horaStr = hora.format(horaFormatter);
            comboHora.getSelectionModel().select(horaStr);
        }

        txtTipoConsulta.setText(cita.getTipoConsulta());
        txtNotas.setText("");
    }

    @FXML
    private void onGuardar() {
        if (dpFecha.getValue() == null || comboHora.getValue() == null || comboPacientes.getValue() == null) {
            mostrarAlerta("Datos Incompletos", "Debes seleccionar paciente, fecha y hora.");
            return;
        }

        try {
            LocalDate fechaNueva = dpFecha.getValue();
            LocalTime horaNueva = LocalTime.parse(comboHora.getValue(), horaFormatter);
            LocalDateTime fechaHoraFinal = LocalDateTime.of(fechaNueva, horaNueva);
            String nuevoTipo = txtTipoConsulta.getText();

            // CAMBIO 2: Obtenemos el ID del nuevo paciente seleccionado
            int idNuevoPaciente = comboPacientes.getValue().getIdPaciente();

            // CAMBIO 3: Llamamos al DAO actualizado
            boolean exito = dao.actualizarCita(
                    citaActual.getIdCitas(),
                    idNuevoPaciente, // Pasamos el nuevo ID
                    fechaHoraFinal,
                    nuevoTipo
            );

            if (exito) {
                mostrarAlerta("Éxito", "Cita actualizada correctamente.");
                this.seGuardo = true;
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la cita en la base de datos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Formato de datos inválido.");
        }
    }

    @FXML
    private void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) dpFecha.getScene().getWindow(); // Cambié txtPaciente por dpFecha porque borramos txtPaciente
        stage.close();
    }

    public boolean isGuardado() {
        return seGuardo;
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}