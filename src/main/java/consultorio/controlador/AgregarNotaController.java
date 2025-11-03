package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.function.Consumer;

public class AgregarNotaController {

    @FXML public Button Guardar;
    @FXML public Button Cancelar;
    @FXML public Label agregarNota;

    // callback para devolver la nueva nota al NotasController
    private Consumer<Notas> onGuardar;
    // proveedor de ID consecutivo (lo manda NotasController)
    private java.util.function.IntSupplier idSupplier;

    public void setOnGuardar(Consumer<Notas> onGuardar, java.util.function.IntSupplier idSupplier) {
        this.onGuardar = onGuardar;
        this.idSupplier = idSupplier;
    }

    @FXML
    private void initialize() {
        if (Guardar != null) Guardar.setOnAction(e -> guardar());
        if (Cancelar != null) Cancelar.setOnAction(e -> cerrarVentana());
    }

    private void guardar() {
        // Buscar los controles por tipo (no cambiamos FXML)
        Node root = agregarNota.getScene().getRoot();
        TextField txtTitulo  = (TextField) root.lookup(".text-field");                 // primer TextField
        DatePicker dpFecha   = (DatePicker) root.lookup(".date-picker");               // primer DatePicker
        TextArea txtContenido= (TextArea) root.lookup(".text-area");                   // primer TextArea

        String titulo = (txtTitulo != null && txtTitulo.getText() != null) ? txtTitulo.getText().trim() : "";
        String contenido = (txtContenido != null && txtContenido.getText() != null) ? txtContenido.getText().trim() : "";
        LocalDate fecha = (dpFecha != null && dpFecha.getValue() != null) ? dpFecha.getValue() : LocalDate.now();

        if (titulo.isEmpty()) {
            alerta("El título no puede ir vacío.");
            return;
        }

        int nuevoId = (idSupplier != null) ? idSupplier.getAsInt() : 1;
        Notas nueva = new Notas(nuevoId, titulo, contenido, fecha);

        if (onGuardar != null) onGuardar.accept(nueva);
        cerrarVentana();
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) agregarNota.getScene().getWindow();
        stage.close();
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
