package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import lombok.Setter;

public class CalendarController {

    @Setter
    private MainController mainController;

    // Inyecta los nodos del FXML (asegúrate que fx:id coincida)
    @FXML private Text mesText;
    @FXML private Text anioText;

    @FXML
    private void initialize() {
        System.out.println("CalendarController inicializado");
        // inicializar mesText/anioText si hace falta
    }

    // Botones del FXML (onAction="#mesAnterior" y "#mesSiguiente")
    @FXML
    private void mesAnterior(ActionEvent event) {
        // lógica para retroceder mes
        System.out.println("Mes anterior");
    }

    @FXML
    private void mesSiguiente(ActionEvent event) {
        // lógica para avanzar mes
        System.out.println("Mes siguiente");
    }

    // Metodo llamado desde MainController para volver al inicio
    public void volverInicio() {
        if (mainController != null) {
            mainController.cambiarVistaInicio();
        }
    }
}
