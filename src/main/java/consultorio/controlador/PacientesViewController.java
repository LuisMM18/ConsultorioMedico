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

import java.io.IOException;

public class PacientesViewController {

    @FXML private TextField buscarField;
    @FXML private Button buscarButton;
    @FXML private Button nuevoButton;

    @FXML private Label totalPacientesLabel;
    @FXML private Label nuevosMesLabel;
    @FXML private Label citasActivasLabel;

    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, Integer> colID;
    @FXML private TableColumn<Paciente, String> colNombre;
    @FXML private TableColumn<Paciente, Integer> colEdad;
    @FXML private TableColumn<Paciente, String> colTelefono;
    @FXML private TableColumn<Paciente, String> colCorreo;
    @FXML private TableColumn<Paciente, Void> colAcciones;

    private ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas
        colID.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(data -> data.getValue().nombreProperty());
        colEdad.setCellValueFactory(data -> data.getValue().edadProperty().asObject());
        colTelefono.setCellValueFactory(data -> data.getValue().telefonoProperty());
        colCorreo.setCellValueFactory(data -> data.getValue().correoProperty());

        // Cargar datos
        cargarDatosEjemplo();

        // Crear lista filtrada que se basa en la lista original
        FilteredList<Paciente> filtrada = new FilteredList<>(listaPacientes, p -> true);

        // Vincular el TableView a la lista filtrada
        tablaPacientes.setItems(filtrada);

        // Configurar la columna de acciones (solo se hace una vez)
        agregarColumnaAcciones();

        // Evento de búsqueda
        buscarField.textProperty().addListener((obs, oldValue, newValue) -> {
            filtrada.setPredicate(paciente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // muestra todos
                }
                String filtro = newValue.toLowerCase();
                return paciente.getNombre().toLowerCase().contains(filtro)
                        || String.valueOf(paciente.getId()).equals(filtro);
            });
        });

        // Botón nuevo paciente
        nuevoButton.setOnAction(e -> agregarPacienteNuevo());
    }

    private void cargarDatosEjemplo() {
        listaPacientes.addAll(
                new Paciente(1, "Ana López", 25, "555-1234", "ana@example.com"),
                new Paciente(2, "Carlos Pérez", 42, "555-5678", "carlos@example.com"),
                new Paciente(3, "Marta García", 31, "555-8765", "marta@example.com"),
                new Paciente(4, "José Torres", 60, "555-2222", "jose@example.com")
        );

        totalPacientesLabel.setText(String.valueOf(listaPacientes.size()));
        nuevosMesLabel.setText("3");
        citasActivasLabel.setText("9");
    }

    private void agregarPacienteNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/AgendarNuevaCitaView.fxml"));
            Parent root = loader.load();

            AgendarNuevaCitaController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Paciente");
            stage.setScene(new Scene(root, 500, 500));
            stage.setResizable(false);
            stage.initOwner(tablaPacientes.getScene().getWindow());
            stage.showAndWait();

            if (controller.isGuardado()) {
                listaPacientes.add(controller.getNuevoPaciente());
                totalPacientesLabel.setText(String.valueOf(listaPacientes.size()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de nuevo paciente.");
        }
    }

    private void agregarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button editarBtn = new Button("Editar");
            private final Button eliminarBtn = new Button("Eliminar");
            private final Button notasBtn = new Button("Notas");
            private final HBox botones = new HBox(10, editarBtn, eliminarBtn, notasBtn);

            {
                editarBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6;");
                eliminarBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6;");
                notasBtn.setStyle("-fx-background-color: #grey; -fx-text-fill: black; -fx-background-radius: 6;");

                editarBtn.setOnAction(e -> {
                    // paciente = getTableView().getItems().get(getIndex());
                    // mostrarAlerta("Editar paciente", "Editar datos de: " + paciente.getNombre());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/EditarPacienteView.fxml"));
                        Parent root = loader.load();

                        Stage stage = new Stage();
                        stage.setTitle("Editar Paciente");
                        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la principal hasta cerrar
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });

                eliminarBtn.setOnAction(e -> {


                    Paciente paciente = getTableView().getItems().get(getIndex());
                    listaPacientes.remove(paciente);
                    totalPacientesLabel.setText(String.valueOf(listaPacientes.size()));
                });
                notasBtn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Notas.fxml"));
                        Parent root = loader.load();

                        Stage stage = new Stage();
                        stage.setTitle("Notas");
                        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la principal hasta cerrar
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botones);
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Clase modelo interna (puedes moverla a un archivo aparte si prefieres)
    public static class Paciente {
        private final javafx.beans.property.IntegerProperty id;
        private final javafx.beans.property.StringProperty nombre;
        private final javafx.beans.property.IntegerProperty edad;
        private final javafx.beans.property.StringProperty telefono;
        private final javafx.beans.property.StringProperty correo;

        public Paciente(int id, String nombre, int edad, String telefono, String correo) {
            this.id = new javafx.beans.property.SimpleIntegerProperty(id);
            this.nombre = new javafx.beans.property.SimpleStringProperty(nombre);
            this.edad = new javafx.beans.property.SimpleIntegerProperty(edad);
            this.telefono = new javafx.beans.property.SimpleStringProperty(telefono);
            this.correo = new javafx.beans.property.SimpleStringProperty(correo);
        }

        // Getters y setters con propiedades JavaFX
        public int getId() { return id.get(); }
        public javafx.beans.property.IntegerProperty idProperty() { return id; }

        public String getNombre() { return nombre.get(); }
        public javafx.beans.property.StringProperty nombreProperty() { return nombre; }

        public int getEdad() { return edad.get(); }
        public javafx.beans.property.IntegerProperty edadProperty() { return edad; }

        public String getTelefono() { return telefono.get(); }
        public javafx.beans.property.StringProperty telefonoProperty() { return telefono; }

        public String getCorreo() { return correo.get(); }
        public javafx.beans.property.StringProperty correoProperty() { return correo; }
    }
}
