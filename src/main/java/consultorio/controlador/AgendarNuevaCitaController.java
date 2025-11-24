package consultorio.controlador;

import consultorio.DAO;
import consultorio.model.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;

import java.time.LocalDate;

public class AgendarNuevaCitaController {

    @FXML private TextField nombreField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private TextField telefonoField;
    @FXML private TextField correoField;

    @FXML private DatePicker fechaCitaPicker;
    @FXML private ChoiceBox<String> horaChoiceBox;

    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;

    @FXML private RadioButton rbNuevoPaciente;
    @FXML private RadioButton rbPacienteExistente;
    @FXML private ToggleGroup tipoPacienteGroup;
    @FXML private ComboBox<Paciente> comboPacientes;

    @Getter private boolean guardado = false;
    @Getter private Integer idPacienteSeleccionado;

    @Getter private LocalDate fechaSeleccionada;
    @Getter private String horaSeleccionada;

    private DAO dao;

    @FXML
    public void initialize() {
        dao = new DAO();

        if (tipoPacienteGroup == null) {
            tipoPacienteGroup = new ToggleGroup();
            rbNuevoPaciente.setToggleGroup(tipoPacienteGroup);
            rbPacienteExistente.setToggleGroup(tipoPacienteGroup);
        }
        rbNuevoPaciente.setSelected(true);

        ObservableList<Paciente> pacientes = FXCollections.observableArrayList(dao.getPacientesActivosBasico());
        comboPacientes.setItems(pacientes);
        comboPacientes.setDisable(true);

        tipoPacienteGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> actualizarModoPaciente());

        fechaCitaPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        guardarButton.setOnAction(e -> guardarDatos());
        cancelarButton.setOnAction(e -> cerrarVentana());
    }

    private void guardarDatos() {
        // 1. Validaciones iniciales (IGUAL QUE ANTES)
        if (fechaCitaPicker.getValue() == null) {
            mostrarAlerta("Faltan datos", "Debes seleccionar una fecha para la cita.");
            return;
        }
        if (horaChoiceBox.getValue() == null) {
            mostrarAlerta("Faltan datos", "Debes seleccionar una hora para la cita.");
            return;
        }
        if (fechaCitaPicker.getValue().isBefore(LocalDate.now())) {
            mostrarAlerta("Fecha inválida", "No puedes agendar citas en días pasados.");
            return;
        }

        this.fechaSeleccionada = fechaCitaPicker.getValue();
        this.horaSeleccionada = horaChoiceBox.getValue();

        java.time.LocalTime tiempo = convertirHora(this.horaSeleccionada);
        java.time.LocalDateTime fechaHoraCita = java.time.LocalDateTime.of(this.fechaSeleccionada, tiempo);

        Integer idPacienteFinal = null;

        if (rbPacienteExistente.isSelected()) {
            Paciente seleccionado = comboPacientes.getValue();
            if (seleccionado == null) {
                mostrarAlerta("Paciente no seleccionado", "Selecciona un paciente existente.");
                return;
            }
            idPacienteFinal = seleccionado.getIdPaciente();
        } else {
            String nombre = nombreField.getText();
            String telefono = telefonoField.getText();

            if (nombre.isEmpty() || telefono.isEmpty()) {
                mostrarAlerta("Faltan datos", "Por favor completa nombre y teléfono.");
                return;
            }

            String correo = correoField.getText();
            LocalDate fechaNac = fechaNacimientoPicker.getValue();

            // Creamos al paciente
            boolean pacienteCreado = dao.crearPaciente(nombre, fechaNac, telefono, correo);

            if (pacienteCreado) {
                Paciente pNuevo = dao.getPacientesActivosBasico().stream()
                        .filter(p -> p.getNombreCompleto().equals(nombre))
                        .findFirst()
                        .orElse(null);

                if (pNuevo != null) {
                    idPacienteFinal = pNuevo.getIdPaciente();
                }
            } else {
                mostrarAlerta("Error", "No se pudo crear el paciente.");
                return;
            }
        }

        // 4. --- EL PASO QUE FALTABA: CREAR LA CITA EN LA BD ---
        if (idPacienteFinal != null) {
            int idUsuario = consultorio.Rol.getInstance().getIdUsuario();

            boolean citaCreada = dao.crearCita(idUsuario, idPacienteFinal, fechaHoraCita, "Consulta General");

            if (citaCreada) {
                this.guardado = true;
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo agendar la cita en la base de datos.");
            }
        }
    }

    // --- MÉTODO AUXILIAR PARA CONVERTIR LA HORA ---
    private java.time.LocalTime convertirHora(String horaString) {
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ENGLISH);
            return java.time.LocalTime.parse(horaString, formatter);
        } catch (Exception e) {
            return java.time.LocalTime.of(9, 0);
        }
    }

    private void actualizarModoPaciente() {
        boolean esNuevo = rbNuevoPaciente.isSelected();

        nombreField.setDisable(!esNuevo);
        fechaNacimientoPicker.setDisable(!esNuevo);
        telefonoField.setDisable(!esNuevo);
        correoField.setDisable(!esNuevo);

        comboPacientes.setDisable(esNuevo);
    }

    private void cerrarVentana() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setHeaderText(null);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}