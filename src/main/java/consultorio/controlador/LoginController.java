package consultorio.controlador;

import consultorio.DAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Button loginButton; // ¡ESTA LÍNEA ES IMPORTANTE!

    @FXML
    private void initialize() {
        // Inicialización si es necesaria
    }
    @FXML
    private void handleLogin() {
        String usuario = userField.getText();
        String contraseña = passField.getText();

        if (autenticarUsuario(usuario, contraseña)) {
            abrirVistaPrincipal();
            cerrarVentanaLogin();
        } else {
            mostrarError("Usuario o contraseña incorrectos");
        }
    }

    private boolean autenticarUsuario(String usuario, String contrasena) {
        // en caso de no poder iniciar sesión establecer código como comentario
        //return !usuario.isEmpty() && !contraseña.isEmpty();
        DAO dao = new DAO();

        return dao.DAOautenticarUsuario(usuario, contrasena);
    }


    private void abrirVistaPrincipal() {
        try {
            // Verificar que el archivo existe
            URL fxmlUrl = getClass().getResource("/vista/MainView.fxml");
            if (fxmlUrl == null) {
                mostrarError("No se encuentra MainView.fxml. Verifica la ruta.");
                System.err.println("Buscando en: " + System.getProperty("user.dir"));
                return;
            }

            System.out.println("Cargando MainView desde: " + fxmlUrl);
            Parent root = FXMLLoader.load(fxmlUrl);

            Stage stage = new Stage();
            stage.setTitle("Sistema de Consultorio Médico");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            mostrarError("Error al cargar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cerrarVentanaLogin() {
        // Buscar el botón en la escena si la inyección falla
        Button btn = (Button) userField.getScene().lookup("#loginButton");
        if (btn != null) {
            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();
        } else {
            // Alternativa: usar cualquier nodo de la escena
            Stage stage = (Stage) userField.getScene().getWindow();
            stage.close();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Login");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}