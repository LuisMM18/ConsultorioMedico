package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ListaVistaDiaController {

    @FXML
    private VBox appointmentContainer; // el contenedor de todas las citas

    @FXML
    private Button btnNuevo, btnGuardar;

    @FXML
    private void onNuevo() {
        VBox nuevaCita = crearCita("Hora nueva", "Nueva cita sin descripción");
        appointmentContainer.getChildren().add(nuevaCita);
    }

    @FXML
    private void onGuardar() {
        System.out.println("Guardar todos los cambios en base de datos o archivo...");
    }

    @FXML
    private void onEditar(javafx.event.ActionEvent event) {
        Button boton = (Button) event.getSource();
        VBox cita = (VBox) boton.getParent().getParent();

        Label lblHora = null;
        Label lblDescripcion = null;

        for (var node : cita.getChildren()) {
            if (node instanceof HBox header) {
                for (var subNode : header.getChildren()) {
                    if (subNode instanceof Label label) lblHora = label;
                }
            } else if (node instanceof Label label) {
                lblDescripcion = label;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/EdicionDeCita.fxml"));
            Parent root = loader.load();

            EdiciondeCitaController controller = loader.getController();
            controller.setDatos(lblHora.getText(), lblDescripcion.getText());

            Stage stage = new Stage();
            stage.setTitle("Editar Cita");
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isGuardado()) {
                lblDescripcion.setText(controller.getNuevoDescripcion());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onEliminar(javafx.event.ActionEvent event) {
        Button boton = (Button) event.getSource();

        // Buscar el VBox principal de la cita, independientemente de su estructura
        VBox cita = null;
        if (boton.getParent() instanceof HBox && boton.getParent().getParent() instanceof VBox) {
            cita = (VBox) boton.getParent().getParent();
        }

        if (cita != null && appointmentContainer.getChildren().contains(cita)) {
            appointmentContainer.getChildren().remove(cita);
        }
    }

    private VBox crearCita(String hora, String descripcionTexto) {
        VBox cita = new VBox();
        cita.setStyle("-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: white;");
        cita.setSpacing(5);

        HBox header = new HBox();
        header.setSpacing(5);
        header.setAlignment(javafx.geometry.Pos.TOP_RIGHT);

        Label lblHora = new Label(hora);
        lblHora.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox.setHgrow(lblHora, javafx.scene.layout.Priority.ALWAYS);

        Button btnEliminar = new Button("X");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-font-size: 20px; -fx-font-weight: bold;");
        btnEliminar.setOnAction(this::onEliminar);

        header.getChildren().addAll(lblHora, btnEliminar);

        Label descripcion = new Label(descripcionTexto);
        descripcion.setStyle("-fx-font-size: 20px;");
        descripcion.setWrapText(true);

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #9ADDFF; -fx-text-fill: white; -fx-font-weight: bold;");
        btnEditar.setOnAction(this::onEditar);

        HBox pie = new HBox(btnEditar);
        pie.setAlignment(javafx.geometry.Pos.BOTTOM_RIGHT);

        cita.getChildren().addAll(header, descripcion, pie);
        return cita;
    }
}
