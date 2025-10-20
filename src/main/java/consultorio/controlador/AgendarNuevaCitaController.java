package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;
import java.time.LocalDate;
import java.time.Period;

public class AgendarNuevaCitaController {

    @FXML private TextField nombreField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private TextField telefonoField;
    @FXML private TextField correoField;
    @FXML private TextField notasField;

    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;

    @Getter
    private PacientesViewController.Paciente nuevoPaciente;
    @Getter
    private boolean guardado = false;

    @FXML
    public void initialize() {
        // Usando los nombres corregidos
        guardarButton.setOnAction(e -> guardarPaciente());
        cancelarButton.setOnAction(e -> cerrarVentana());
    }

    private void guardarPaciente() {
        // Usando los nombres corregidos
        if (nombreField.getText().isEmpty() || telefonoField.getText().isEmpty()) {
            mostrarAlerta("Faltan datos", "Por favor completa al menos el nombre y el teléfono.");
            return;
        }

        // --- Lógica mejorada para la edad ---
        int edad = 0;
        if (fechaNacimientoPicker.getValue() != null) {
            edad = Period.between(fechaNacimientoPicker.getValue(), LocalDate.now()).getYears();
        }

        nuevoPaciente = new PacientesViewController.Paciente(
                (int) (Math.random() * 10000), // ID aleatorio temporal
                nombreField.getText(),
                edad,
                telefonoField.getText(),
                correoField.getText()
        );

        guardado = true;
        cerrarVentana();
    }

    private void cerrarVentana() {
        // Usando el nombre corregido
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