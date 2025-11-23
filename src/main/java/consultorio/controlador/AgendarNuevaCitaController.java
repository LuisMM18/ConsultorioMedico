package consultorio.controlador;

import consultorio.DAO; // IMPORTANTE: Importar el DAO
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;
import java.time.LocalDate;

//

import consultorio.model.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;


public class AgendarNuevaCitaController {

    @FXML private TextField nombreField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private TextField telefonoField;
    @FXML private TextField correoField;
    @FXML private TextField notasField; // Este campo no se usa en la lógica de 'guardarPaciente'

    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;

    // Nuevos
    @FXML private RadioButton rbNuevoPaciente;
    @FXML private RadioButton rbPacienteExistente;
    @FXML private ToggleGroup tipoPacienteGroup;
    @FXML private ComboBox<Paciente> comboPacientes;

    @Getter
    private boolean guardado = false;

    @Getter
    private Integer idPacienteSeleccionado;  // para saber cuál paciente se usó

    private DAO dao; // AÑADIDO: Instancia del DAO

    //
    @FXML
    public void initialize() {
        dao = new DAO();

        // Aseguramos el ToggleGroup (por si no lo asignaste en FXML)
        if (tipoPacienteGroup == null) {
            tipoPacienteGroup = new ToggleGroup();
            rbNuevoPaciente.setToggleGroup(tipoPacienteGroup);
            rbPacienteExistente.setToggleGroup(tipoPacienteGroup);
        }

        rbNuevoPaciente.setSelected(true);

        // Llenar ComboBox con pacientes existentes
        ObservableList<Paciente> pacientes =
                FXCollections.observableArrayList(dao.getPacientesActivosBasico());
        comboPacientes.setItems(pacientes);
        comboPacientes.setDisable(true); // al inicio solo se usa "Nuevo paciente"

        // Listener para cambiar entre "nuevo" y "existente"
        tipoPacienteGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> actualizarModoPaciente());

        // Botones
        guardarButton.setOnAction(e -> guardarPaciente());
        cancelarButton.setOnAction(e -> cerrarVentana());
    }
//

    private void guardarPaciente() {

        // Caso 1: Paciente existente
        if (rbPacienteExistente.isSelected()) {
            Paciente seleccionado = comboPacientes.getValue();
            if (seleccionado == null) {
                mostrarAlerta("Paciente no seleccionado", "Selecciona un paciente existente.");
                return;
            }

            // Guardamos el id del paciente elegido y cerramos
            this.idPacienteSeleccionado = seleccionado.getIdPaciente();
            this.guardado = true;
            cerrarVentana();
            return;
        }

        // Caso 2: Nuevo paciente (la lógica que ya tenías)
        String nombre = nombreField.getText();
        String telefono = telefonoField.getText();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Faltan datos", "Por favor completa al menos el nombre y el teléfono.");
            return;
        }

        String correo = correoField.getText();
        LocalDate fechaNac = fechaNacimientoPicker.getValue();

        boolean exito = dao.crearPaciente(nombre, fechaNac, telefono, correo);

        if (exito) {
            guardado = true;
            // Aquí podrías, si quieres, buscar el id del nuevo paciente y asignarlo a idPacienteSeleccionado
            cerrarVentana();
        } else {
            mostrarAlerta("Error de Base de Datos", "No se pudo guardar el nuevo paciente.");
        }
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

    private void actualizarModoPaciente() {
        boolean esNuevo = rbNuevoPaciente.isSelected();

        // Si es nuevo paciente, se habilita el formulario
        nombreField.setDisable(!esNuevo);
        fechaNacimientoPicker.setDisable(!esNuevo);
        telefonoField.setDisable(!esNuevo);
        correoField.setDisable(!esNuevo);

        // El combo solo se usa cuando es paciente existente
        comboPacientes.setDisable(esNuevo);
    }










}