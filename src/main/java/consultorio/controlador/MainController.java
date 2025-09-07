package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnInicio, btnCitas, btnPacientes, btnAgendar;
    @FXML private Label lblUsuario;

    @FXML
    public void initialize() {
        System.out.println("MainController inicializado");

        // Solo cargar la vista de inicio por ahora
        cambiarVistaInicio();
    }

    @FXML
    private void cambiarVistaInicio() {
        try {
            // Crear una vista simple en lugar de cargar FXML
            VBox vistaInicio = new VBox();
            vistaInicio.setSpacing(20);
            vistaInicio.setStyle("-fx-padding: 30;");

            Label titulo = new Label("Bienvenida/o Recepcionista");
            titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Label subtitulo = new Label("Sistema de Consultorio Médico");
            vistaInicio.getChildren().addAll(titulo, subtitulo);

            contentArea.getChildren().setAll(vistaInicio);
            actualizarBotonActivo(btnInicio);

        } catch (Exception e) {
            System.err.println("Error en vista inicio: " + e.getMessage());
            mostrarVistaDeError("Error cargando inicio");
        }
    }

    @FXML
    private void cambiarVistaCitas() {
        VBox vistaCitas = new VBox();
        vistaCitas.getChildren().add(new Label("Citas de Hoy - En desarrollo"));
        contentArea.getChildren().setAll(vistaCitas);
        actualizarBotonActivo(btnCitas);
    }

    @FXML
    private void cambiarVistaPacientes() {
        VBox vistaPacientes = new VBox();
        vistaPacientes.getChildren().add(new Label("Pacientes - En desarrollo"));
        contentArea.getChildren().setAll(vistaPacientes);
        actualizarBotonActivo(btnPacientes);
    }

    @FXML
    private void cambiarVistaAgendar() {
        VBox vistaAgendar = new VBox();
        vistaAgendar.getChildren().add(new Label("Agendar Cita - En desarrollo"));
        contentArea.getChildren().setAll(vistaAgendar);
        actualizarBotonActivo(btnAgendar);
    }

    private void mostrarVistaDeError(String mensaje) {
        VBox vistaError = new VBox();
        vistaError.getChildren().add(new Label(mensaje));
        contentArea.getChildren().setAll(vistaError);
    }

    private void actualizarBotonActivo(Button botonActivo) {
        // Restablecer todos los botones
        Button[] botones = {btnInicio, btnCitas, btnPacientes, btnAgendar};
        for (Button boton : botones) {
            boton.getStyleClass().remove("nav-button-active");
        }

        // Establecer el botón activo
        if (botonActivo != null) {
            botonActivo.getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            // Aquí irá la lógica para volver al login
            System.out.println("Cerrando sesión...");

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
    }
}