package consultorio.controlador;

import consultorio.DAO;
import consultorio.controlador.PacientesViewController.Paciente;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarPacienteController {

    @FXML private TextField nombreField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private TextField telefonoField;
    @FXML private TextField correoField;
    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;

    private Paciente pacienteActual;
    private DAO dao;

    @FXML
    public void initialize() {
        dao = new DAO();
        guardarButton.setOnAction(e -> guardarCambios());
        cancelarButton.setOnAction(e -> cerrarVentana());
    }

    public void setPaciente(Paciente paciente) {
        this.pacienteActual = paciente;

        nombreField.setText(paciente.getNombre());
        telefonoField.setText(paciente.getTelefono());
        correoField.setText(paciente.getCorreo());
    }

    private void guardarCambios() {
        if (nombreField.getText().isEmpty()) {
            mostrarAlerta("Error", "El nombre es obligatorio");
            return;
        }

        boolean exito = dao.actualizarPaciente(
                pacienteActual.getId(),
                nombreField.getText(),
                fechaNacimientoPicker.getValue(),
                telefonoField.getText(),
                correoField.getText()
        );

        if (exito) {
            mostrarAlerta("Éxito", "Paciente actualizado.");
            cerrarVentana();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar.");
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) guardarButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}