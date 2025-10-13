package consultorio.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.time.LocalDate;

public class NotasController {

    @FXML private TableView<Notas> tablaNotas;
    @FXML private TableColumn<Notas, Integer> colId;
    @FXML private TableColumn<Notas, String> colTitulo;
    @FXML private TableColumn<Notas, String> colContenido;
    @FXML private TableColumn<Notas, LocalDate> colFecha;
    @FXML private TableColumn<Notas, Void> colAcciones;
    @FXML private TextField buscarNotaField;

    private ObservableList<Notas> listaNotas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colTitulo.setCellValueFactory(data -> data.getValue().tituloProperty());
        colContenido.setCellValueFactory(data -> data.getValue().contenidoProperty());
        colFecha.setCellValueFactory(data -> data.getValue().fechaProperty());

        agregarBotonesAcciones();

        // Datos de ejemplo
        listaNotas.addAll(
                new Notas(1, "Seguimiento Juan", "Revisar presión arterial", LocalDate.now()),
                new Notas(2, "Llamar laboratorio", "Preguntar resultados de sangre", LocalDate.now())
        );

        tablaNotas.setItems(listaNotas);
    }

    private void agregarBotonesAcciones() {
        Callback<TableColumn<Notas, Void>, TableCell<Notas, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            {
                btnEditar.setStyle("-fx-background-color: #9ADDFF; -fx-text-fill: white; -fx-font-weight: bold;");
                btnEliminar.setStyle("-fx-background-color: #A9A9A9; -fx-text-fill: white; -fx-font-weight: bold;");
                btnEditar.setOnAction(e -> editarNota(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> eliminarNota(getTableView().getItems().get(getIndex())));
            }

            private final HBox pane = new HBox(10, btnEditar, btnEliminar);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    private void editarNota(Notas nota) {
        System.out.println("Editando: " + nota.getTitulo());
    }

    private void eliminarNota(Notas nota) {
        listaNotas.remove(nota);
    }
}
