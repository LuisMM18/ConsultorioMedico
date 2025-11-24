package consultorio.controlador;

import consultorio.DAO;
import consultorio.Rol;
import consultorio.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public class AjustesController {

    @Setter
    private MainController mainController;
    private final DAO dao = new DAO();

    private int idUsuarioEnEdicion;

    @FXML private VBox adminPanel;
    @FXML private ComboBox<Usuario> usuarioCombo;

    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private TextField telefonoField;

    @FXML private PasswordField actualField;
    @FXML private PasswordField nuevaField;
    @FXML private PasswordField confirmarField;

    @FXML
    private void initialize() {
        int miId = Rol.getInstance().getIdUsuario();
        int miRol = Rol.getInstance().getRol();

        this.idUsuarioEnEdicion = miId;

        if (miRol == 1) {
            configurarModoAdmin(miId);
        } else {
            adminPanel.setVisible(false);
            adminPanel.setManaged(false);
            cargarDatosEnFormulario(miId);
        }
    }

    private void configurarModoAdmin(int miId) {
        adminPanel.setVisible(true);
        adminPanel.setManaged(true);

        List<Usuario> usuarios = dao.getAllUsuarios();
        usuarioCombo.getItems().addAll(usuarios);

        for (Usuario u : usuarios) {
            if (u.getIdUsuario() == miId) {
                usuarioCombo.getSelectionModel().select(u);
                break;
            }
        }

        cargarDatosEnFormulario(miId);

        usuarioCombo.setOnAction(e -> {
            Usuario seleccionado = usuarioCombo.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                this.idUsuarioEnEdicion = seleccionado.getIdUsuario();
                cargarDatosEnFormulario(this.idUsuarioEnEdicion);
                limpiarCamposPass();
            }
        });
    }

    private void cargarDatosEnFormulario(int idTarget) {
        Usuario u = dao.getUsuarioPorId(idTarget);
        if (u != null) {
            nombreField.setText(u.getNombreCompleto());
            correoField.setText(u.getCorreo());
            String tel = u.getTelefono();
            telefonoField.setText(tel != null ? tel : "");
        } else {
            mostrarAlerta("Error", "No se pudo cargar el usuario ID: " + idTarget);
        }
    }

    @FXML
    private void guardar(ActionEvent event) {
        if (nombreField.getText().trim().isEmpty() || correoField.getText().trim().isEmpty()) {
            mostrarAlerta("Faltan Datos", "Nombre y correo son obligatorios.");
            return;
        }

        String passNueva = nuevaField.getText();
        String passConfirmar = confirmarField.getText();
        String passParaEnviar = null;

        if (!passNueva.isEmpty()) {
            if (!passNueva.equals(passConfirmar)) {
                mostrarAlerta("Error", "Las contraseñas nuevas no coinciden.");
                return;
            }

            passParaEnviar = passNueva;
        }

        boolean exito = dao.actualizarUsuario(
                this.idUsuarioEnEdicion,
                nombreField.getText(),
                correoField.getText(),
                telefonoField.getText(),
                passParaEnviar
        );

        if (exito) {
            mostrarAlerta("Guardado", "Información actualizada correctamente.");
            limpiarCamposPass();

            if (adminPanel.isVisible()) {
                Usuario uSel = usuarioCombo.getSelectionModel().getSelectedItem();
                if (uSel != null) {
                    uSel.setNombre(nombreField.getText());
                }
            }
        } else {
            mostrarAlerta("Error", "No se pudo guardar en la base de datos.");
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        if (mainController != null) {
            mainController.cambiarVistaInicio();
        }
    }

    private void limpiarCamposPass() {
        actualField.clear();
        nuevaField.clear();
        confirmarField.clear();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    private void AgregarUsuarioVentana(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/AgregarUsuarioView.fxml"));
            Parent root = loader.load();

            // controlador del diálogo
            AgregarUsuarioController ctrl = loader.getController();
            // pasar DAO
            ctrl.setDao(this.dao);
            // callback para refrescar si se crea un usuario
            ctrl.setOnUserCreated(() -> {
                usuarioCombo.getItems().clear();
                usuarioCombo.getItems().addAll(dao.getAllUsuarios());
            });

            Stage stage = new Stage();
            stage.setTitle("Crear nuevo usuario");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de creación de usuario:\n" + e.getMessage());
        } catch (NullPointerException npe) {
            // Por si getResource devolvió null
            npe.printStackTrace();
            mostrarAlerta("Error", "No se encontró el FXML '/vista/AgregarUsuarioView.fxml'. Verifica que esté en src/main/resources/vista/");
        }
    }
}