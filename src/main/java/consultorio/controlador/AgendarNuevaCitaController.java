package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;

public class AgendarNuevaCitaController {

    @FXML private TextField txtNombre;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtNotas;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    @Getter
    private PacientesViewController.Paciente nuevoPaciente;
    @Getter
    private boolean guardado = false;

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(e -> guardarPaciente());
        btnCancelar.setOnAction(e -> cerrarVentana());
    }

    private void guardarPaciente() {
        if (txtNombre.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
            mostrarAlerta("Faltan datos", "Por favor completa al menos el nombre y el teléfono.");
            return;
        }

        nuevoPaciente = new PacientesViewController.Paciente(
                (int) (Math.random() * 1000),
                txtNombre.getText(),
                30, // edad temporal (puedes calcularla con la fecha de nacimiento si deseas)
                txtTelefono.getText(),
                txtCorreo.getText()
        );

        guardado = true;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
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
