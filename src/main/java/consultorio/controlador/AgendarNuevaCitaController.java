package consultorio.controlador;

import consultorio.DAO; // IMPORTANTE: Importar el DAO
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
    @FXML private TextField notasField; // Este campo no se usa en la lógica de 'guardarPaciente'

    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;


    @Getter
    private boolean guardado = false;

    private DAO dao;

    @FXML
    public void initialize() {
        dao = new DAO(); // AÑADIDO: Inicializar el DAO
        guardarButton.setOnAction(e -> guardarPaciente());
        cancelarButton.setOnAction(e -> cerrarVentana());
    }

    private void guardarPaciente() {
        String nombre = nombreField.getText();
        String telefono = telefonoField.getText();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Faltan datos", "Por favor completa al menos el nombre y el teléfono.");
            return;
        }

        // Obtener los valores
        String correo = correoField.getText();
        LocalDate fechaNac = fechaNacimientoPicker.getValue();

        // MODIFICADO: Llamar al DAO para guardar en la base de datos
        boolean exito = dao.crearPaciente(nombre, fechaNac, telefono, correo);

        if (exito) {
            guardado = true; // Si el DAO lo guardó, marcamos como guardado
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
}