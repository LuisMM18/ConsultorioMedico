package consultorio.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
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

        // Crear lista filtrada
        FilteredList<Notas> filtrada = new FilteredList<>(listaNotas, p -> true);

        // Filtro en tiempo real
        buscarNotaField.textProperty().addListener((obs, oldValue, newValue) -> {
            String filtro = newValue.toLowerCase();
            filtrada.setPredicate(nota -> {
                if (filtro.isEmpty()) return true;
                return nota.getTitulo().toLowerCase().contains(filtro)
                        || String.valueOf(nota.getId()).contains(filtro)
                        || nota.getContenido().toLowerCase().contains(filtro);
            });
        });

        tablaNotas.setItems(filtrada);
    }

    private void agregarBotonesAcciones() {
        Callback<TableColumn<Notas, Void>, TableCell<Notas, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            {
                btnEditar.setStyle("-fx-background-color: #9ADDFF; -fx-text-fill: white; -fx-font-weight: bold;");
                btnEliminar.setStyle("-fx-background-color: #A9A9A9; -fx-text-fill: white; -fx-font-weight: bold;");
                btnEditar.setOnAction(e -> editarNota());
                btnEliminar.setOnAction(e -> {
                    Notas nota = getTableView().getItems().get(getIndex());

                    Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                    alerta.setTitle("Confirmar Eliminación");
                    alerta.setHeaderText("¿Estás seguro de que deseas eliminar esta nota?");
                    alerta.setContentText("Título: " + nota.getTitulo());

                    alerta.showAndWait().ifPresent(respuesta -> {
                        if (respuesta == ButtonType.OK) {
                            eliminarNota(nota);
                        }
                    });
                });
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

    @FXML
    public void editarNota() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/EditarNotaView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Editar Nota");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la principal hasta cerrar
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void AgregarNota(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/AgregarNotaView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Editar Nota");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la principal hasta cerrar
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void eliminarNota(Notas nota) {
        listaNotas.remove(nota);
    }
}
