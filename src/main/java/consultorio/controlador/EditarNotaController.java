package consultorio.controlador;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.function.Consumer;

public class EditarNotaController {

    @FXML public Label EditarNota;

    private Notas notaActual;
    private Consumer<Notas> onGuardar;

    private TextField txtTitulo;
    private DatePicker dpFecha;
    private TextArea txtContenido;
    private Button btnGuardar;

    public void setNota(Notas nota, Consumer<Notas> onGuardar) {
        this.notaActual = nota;
        this.onGuardar = onGuardar;
        wireIfReadyAndLoad();
    }

    @FXML
    private void initialize() {
        ChangeListener<? super javafx.scene.Scene> l = (obs, oldS, newS) -> wireIfReadyAndLoad();
        EditarNota.sceneProperty().addListener(l);
    }

    private void wireIfReadyAndLoad() {
        if (EditarNota == null || EditarNota.getScene() == null) return;

        Node root = EditarNota.getScene().getRoot();

        if (txtTitulo == null)    txtTitulo    = (TextField) root.lookup(".text-field");
        if (dpFecha == null)      dpFecha      = (DatePicker) root.lookup(".date-picker");
        if (txtContenido == null) txtContenido = (TextArea)  root.lookup(".text-area");

        if (btnGuardar == null) {
            for (Node n : root.lookupAll(".button")) {
                if (n instanceof Button b && "Guardar".equalsIgnoreCase(b.getText())) {
                    btnGuardar = b;
                    break;
                }
            }
            if (btnGuardar != null) btnGuardar.setOnAction(e -> guardar());
        }

        if (notaActual != null && txtTitulo != null && txtContenido != null) {
            txtTitulo.setText(notaActual.getTitulo());
            txtContenido.setText(notaActual.getContenido());
        }
        if (notaActual != null && dpFecha != null) {
            dpFecha.setValue(
                    notaActual.getFecha() != null
                            ? notaActual.getFecha()
                            : LocalDate.now()
            );
        }
    }

    private void guardar() {
        if (notaActual == null) return;

        String t = (txtTitulo != null && txtTitulo.getText() != null)
                ? txtTitulo.getText().trim()
                : "";
        if (t.isEmpty()) {
            alerta("El título no puede ir vacío.");
            return;
        }

        LocalDate f = (dpFecha != null && dpFecha.getValue() != null)
                ? dpFecha.getValue()
                : notaActual.getFecha();

        String c = (txtContenido != null)
                ? txtContenido.getText()
                : "";

        if (f != null && f.isBefore(LocalDate.now())) {
            alerta("No puedes guardar una nota con una fecha anterior a hoy.");
            return;
        }

        notaActual.tituloProperty().set(t);
        notaActual.contenidoProperty().set(c);
        notaActual.fechaProperty().set(f);

        if (onGuardar != null) onGuardar.accept(notaActual);
        cerrarVentana();
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) EditarNota.getScene().getWindow();
        stage.close();
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
