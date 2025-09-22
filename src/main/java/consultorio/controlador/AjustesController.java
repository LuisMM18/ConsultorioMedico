package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import lombok.Setter;

public class AjustesController {

    @Setter
    private MainController mainController;

    // Inyecta nodos del FXML (asegúrate que fx:id en FXML coincida)
    @FXML private TextField nombreField;
    @FXML private TextField contrasenaActualField;
    @FXML private TextField nuevaContrasenaField;
    @FXML private TextField repetirContrasenaField;

    @FXML
    private void initialize() {
        System.out.println("AjustesController inicializado");
        // inicializaciones, validaciones, etc.
    }

    @FXML
    private void guardar(ActionEvent event) {
        // valida y guarda los datos
        System.out.println("Guardar ajustes");
    }

    @FXML
    private void cancelar(ActionEvent event) {
        // ejemplo: volver a inicio
        if (mainController != null) {
            mainController.cambiarVistaInicio();
        }
    }
}