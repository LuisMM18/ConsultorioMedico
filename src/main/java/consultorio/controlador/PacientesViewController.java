package consultorio.controlador;

import consultorio.DAO;
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
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class PacientesViewController {

    @FXML private TextField buscarField;
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
    private DAO pacienteDAO; // AÑADIDO: Instancia del DAO

    @FXML
    public void initialize() {
        pacienteDAO = new DAO(); // AÑADIDO: Inicializar el DAO

        // Configurar columnas
        colID.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(data -> data.getValue().nombreProperty());
        colEdad.setCellValueFactory(data -> data.getValue().edadProperty().asObject());
        colTelefono.setCellValueFactory(data -> data.getValue().telefonoProperty());
        colCorreo.setCellValueFactory(data -> data.getValue().correoProperty());

        // MODIFICADO: Cargar datos desde la base de datos en lugar de ejemplos
        refrescarTablaPacientes();

        // Crear lista filtrada que se basa en la lista original
        FilteredList<Paciente> filtrada = new FilteredList<>(listaPacientes, p -> true);

        // Vincular el TableView a la lista filtrada
        tablaPacientes.setItems(filtrada);

        // Configurar la columna de acciones
        agregarColumnaAcciones();

        // Evento de búsqueda
        buscarField.textProperty().addListener((obs, oldValue, newValue) -> {
            filtrada.setPredicate(paciente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filtro = newValue.toLowerCase();
                return paciente.getNombre().toLowerCase().contains(filtro)
                        || String.valueOf(paciente.getId()).equals(filtro);
            });
        });

        // Botón nuevo paciente
        nuevoButton.setOnAction(e -> agregarPacienteNuevo());
    }

    // MÉTODO NUEVO: Carga o actualiza los pacientes desde la base de datos
    private void refrescarTablaPacientes() {
        try {
            listaPacientes.clear(); // Limpiar la lista actual
            List<Paciente> pacientesDesdeDB = pacienteDAO.getAllPacientes(); // Obtener la lista actualizada
            listaPacientes.addAll(pacientesDesdeDB); // Añadirla a la lista observable

            // Actualizar etiquetas
            totalPacientesLabel.setText(String.valueOf(listaPacientes.size()));
            // Aquí puedes agregar lógica para calcular "nuevos este mes" y "citas activas"
            // nuevosMesLabel.setText("...");
            // citasActivasLabel.setText("...");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "No se pudieron cargar los pacientes.");
        }
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

            // MODIFICADO: Si se guardó un paciente, refrescamos toda la tabla desde la BD
            if (controller.isGuardado()) {
                refrescarTablaPacientes();
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
                    // Lógica para editar (requiere pasar el paciente a la nueva ventana)
                    try {
                        Paciente pacienteSeleccionado = getTableView().getItems().get(getIndex());

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/EditarPacienteView.fxml"));
                        Parent root = loader.load();

                        Stage stage = new Stage();
                        stage.setTitle("Editar Paciente");
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                        refrescarTablaPacientes();

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });

                eliminarBtn.setOnAction(e -> {
                    Paciente paciente = getTableView().getItems().get(getIndex());

                    Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                    alerta.setTitle("Confirmar Eliminación");
                    alerta.setHeaderText("¿Seguro que deseas eliminar a este paciente?");
                    alerta.setContentText(paciente.getNombre());

                    alerta.showAndWait().ifPresent(respuesta -> {
                        if (respuesta == ButtonType.OK) {
                            // MODIFICADO: Llamar al DAO para eliminar y luego refrescar
                            boolean eliminado = pacienteDAO.eliminarPaciente(paciente.getId());
                            if (eliminado) {
                                refrescarTablaPacientes();
                            } else {
                                mostrarAlerta("Error", "No se pudo eliminar el paciente de la base de datos.");
                            }
                        }
                    });
                });
                //Nuevo
                notasBtn.setOnAction(e -> {
                    try {
                        // 1. Paciente de esta fila
                        Paciente pacienteSeleccionado = getTableView().getItems().get(getIndex());

                        // 2. Buscar la última cita activa de ese paciente
                        Integer idCita = pacienteDAO.getUltimaCitaIdPorPaciente(pacienteSeleccionado.getId());
                        if (idCita == null) {
                            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                            alerta.setTitle("Sin citas");
                            alerta.setHeaderText("Este paciente no tiene citas activas.");
                            alerta.setContentText("Primero registra una cita para poder agregar notas.");
                            alerta.showAndWait();
                            return;
                        }

                        // 3. Cargar Notas.fxml y pasarle el id de la cita
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Notas.fxml"));
                        Parent root = loader.load();

                        NotasController notasController = loader.getController();
                        notasController.setCitaContext(idCita);

                        Stage stage = new Stage();
                        stage.setTitle("Notas");
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });

                //Nuevo

                /*
                notasBtn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Notas.fxml"));
                        Parent root = loader.load();

                        Stage stage = new Stage();
                        stage.setTitle("Notas");
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });   */

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

    // Clase modelo interna
    public static class Paciente {
        private final javafx.beans.property.IntegerProperty id;
        private final javafx.beans.property.StringProperty nombre;
        private final javafx.beans.property.IntegerProperty edad;
        private final javafx.beans.property.StringProperty telefono;
        private final javafx.beans.property.StringProperty correo;

        // MODIFICADO: El constructor ahora acepta LocalDate para la fecha de nacimiento
        public Paciente(int id, String nombre, LocalDate fechaNacimiento, String telefono, String correo) {
            this.id = new javafx.beans.property.SimpleIntegerProperty(id);
            this.nombre = new javafx.beans.property.SimpleStringProperty(nombre);

            int edadCalculada = 0;
            if (fechaNacimiento != null) {
                edadCalculada = Period.between(fechaNacimiento, LocalDate.now()).getYears();
            }
            this.edad = new javafx.beans.property.SimpleIntegerProperty(edadCalculada);

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