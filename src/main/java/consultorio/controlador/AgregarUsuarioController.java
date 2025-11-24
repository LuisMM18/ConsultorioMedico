package consultorio.controlador;

import consultorio.DAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.util.regex.Pattern;

public class AgregarUsuarioController {

    private DAO dao = new DAO(); // por defecto, pero puede ser sobrescrito via setDao
    private Runnable onUserCreated; // callback opcional

    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private TextField telefonoField;
    @FXML private PasswordField passField;
    @FXML private PasswordField passConfirmField;
    @FXML private TextField rolField; // si usas rol numérico o texto
    @FXML private Button cancelarBtn;
    @FXML private Button guardarBtn;

    @FXML
    private void initialize() {
        // cualquier inicialización adicional
    }

    public void setDao(DAO dao) {
        if (dao != null) this.dao = dao;
    }

    public void setOnUserCreated(Runnable onUserCreated) {
        this.onUserCreated = onUserCreated;
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        // cerrar ventana actual
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @FXML
    private void guardar(ActionEvent event) {
        String nombre = nombreField.getText() != null ? nombreField.getText().trim() : "";
        String correo = correoField.getText() != null ? correoField.getText().trim() : "";
        String telefono = telefonoField.getText() != null ? telefonoField.getText().trim() : "";
        String pass = passField.getText() != null ? passField.getText() : "";
        String passConfirm = passConfirmField.getText() != null ? passConfirmField.getText() : "";

        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
            mostrarAlerta("Faltan datos", "Nombre, correo y contraseña son obligatorios.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(correo).matches()) {
            mostrarAlerta("Correo inválido", "Ingresa un correo electrónico válido.");
            return;
        }

        if (!pass.equals(passConfirm)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.");
            return;
        }

        Integer rol = 2;
        String rolText = rolField.getText() != null ? rolField.getText().trim() : "";
        try {
            if (!rolText.isEmpty()) rol = Integer.parseInt(rolText);
        } catch (NumberFormatException ignored) { }


        boolean exito = dao.crearUsuario(nombre, correo, telefono, pass, rol);
        if (exito) {
            mostrarAlerta("Usuario creado", "El usuario se creó correctamente.");
            if (onUserCreated != null) onUserCreated.run();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            // Intentar distinguir email duplicado consultando si ya existe
            mostrarAlerta("Error", "No se pudo crear el usuario. Verifica que el correo no exista y los datos sean correctos.");
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
