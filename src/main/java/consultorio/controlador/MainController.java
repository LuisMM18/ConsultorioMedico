package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnInicio, btnCitas, btnPacientes, btnAgendar, btnCalendario;
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
 /*
@FXML
private void cambiarVistaNotas() {
    VBox contenedorNotas = new VBox(20);
    contenedorNotas.setStyle("-fx-padding: 40; -fx-alignment: center;");

    Label titulo = new Label("Mis notas");
    titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

    VBox listaNotas = new VBox(10);
    listaNotas.setStyle("-fx-background-color: #fff; -fx-padding: 30; -fx-border-color: #888; -fx-border-width: 2;");

    // Ejemplo de 3 notas
    for (int i = 1; i <= 3; i++) {
        HBox filaNota = new HBox(15);
        filaNota.setStyle("-fx-alignment: center-left;");

        Label lblNota = new Label("Nota " + i + ":");
        lblNota.setStyle("-fx-font-size: 16px;");

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #90cdf4; -fx-text-fill: #222; -fx-font-weight: bold;");

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: #bbb; -fx-text-fill: #222;");

        filaNota.getChildren().addAll(lblNota, btnEditar, btnEliminar);
        listaNotas.getChildren().add(filaNota);
    }

    Button btnAgregar = new Button("Agregar Nota");
    btnAgregar.setStyle("-fx-background-color: #90cdf4; -fx-font-size: 16px; -fx-padding: 10 30 10 30;");

    VBox cajaCentral = new VBox(20, titulo, listaNotas, btnAgregar);
    cajaCentral.setStyle("-fx-alignment: center; -fx-padding: 30; -fx-border-color: #888; -fx-border-width: 1;");

    contenedorNotas.getChildren().add(cajaCentral);

    contentArea.getChildren().setAll(contenedorNotas);
    actualizarBotonActivo(null); // O el botón de notas si lo tienes
}
*/

    @FXML
    private void cambiarVistaCalendario(){
        VBox vistaCalendario = new VBox();
        vistaCalendario.getChildren().add(new Label("Calendario - En desarrollo"));
        contentArea.getChildren().setAll(vistaCalendario);
        actualizarBotonActivo(btnCalendario);
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
        Button[] botones = {btnInicio, btnCitas, btnCalendario, btnPacientes, btnAgendar};
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Loginview.fxml"));
            Parent root = (Parent) loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login - Consultorio Medico");
            stage.setScene(new Scene(root,1200, 800));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.hide();
            System.out.println("Cerrando sesión...");

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
    }
}