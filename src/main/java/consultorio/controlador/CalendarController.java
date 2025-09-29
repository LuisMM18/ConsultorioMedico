package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import javafx.scene.control.Button;
import java.awt.*;
import java.io.IOException;

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

    @FXML
    public void GenerarReporte() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/GenerarReporteView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Generar Reporte");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la principal hasta cerrar
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void hoverEnter(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setStyle("-fx-background-color: #bababa;-fx-border-color: #969696; -fx-border-radius: 8px;");
    }

    @FXML
    private void hoverExit(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setStyle("-fx-background-color: #D9D9D9; -fx-border-color: #969696; -fx-border-radius: 8px;");
    }




}
