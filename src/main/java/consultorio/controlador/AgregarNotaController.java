package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class AgregarNotaController {

    @FXML
    public Button Guardar;
    @FXML
    public Button Cancelar;
    public Label agregarNota;


    @FXML
    private void cerrarVentana() {
        // Usando el nombre corregido
        Stage stage = (Stage) agregarNota.getScene().getWindow();
        stage.close();
    }
}
