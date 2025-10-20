package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarNotaController {
    @FXML
    public Label EditarNota;


    @FXML
    private void cerrarVentana() {
        // Usando el nombre corregido
        Stage stage = (Stage) EditarNota.getScene().getWindow();
        stage.close();
    }
}
