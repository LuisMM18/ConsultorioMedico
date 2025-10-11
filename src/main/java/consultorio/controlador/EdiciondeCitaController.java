package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EdiciondeCitaController {

    @FXML
    private TextField txtPaciente;

    @FXML
    private DatePicker dpFechaHora;

    @FXML
    private TextField txtTipoConsulta;

    @FXML
    private TextField txtNotas;

    private String horaOriginal;
    private String descripcionOriginal;

    private boolean guardado = false;

    // Método para inicializar con los datos actuales
    public void setDatos(String hora, String descripcion) {
        this.horaOriginal = hora;
        this.descripcionOriginal = descripcion;

        txtPaciente.setText(descripcion);
        txtNotas.setText("");
        txtTipoConsulta.setText("");
    }

    @FXML
    private void onGuardar() {
        // Podrías validar campos aquí antes de cerrar
        guardado = true;
        cerrarVentana();
    }

    @FXML
    private void onCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtPaciente.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public String getNuevoDescripcion() {
        return txtPaciente.getText();
    }
}
