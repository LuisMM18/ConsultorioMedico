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

import consultorio.DAO;
import consultorio.model.Nota;
import java.util.List;


public class NotasController {

    @FXML private TableView<Notas> tablaNotas;
    @FXML private TableColumn<Notas, Integer> colId;
    @FXML private TableColumn<Notas, String> colTitulo;
    @FXML private TableColumn<Notas, String> colContenido;
    @FXML private TableColumn<Notas, LocalDate> colFecha;
    @FXML private TableColumn<Notas, Void> colAcciones;
    @FXML private TextField buscarNotaField;

    private final ObservableList<Notas> listaNotas = FXCollections.observableArrayList();

    private final DAO dao = new DAO();
    private Integer idCitaRef; // id de la cita a la que pertenecen las notas


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

        // Lista filtrada
        FilteredList<Notas> filtrada = new FilteredList<>(listaNotas, p -> true);

        // Filtro en tiempo real
        buscarNotaField.textProperty().addListener((obs, oldValue, newValue) -> {
            String filtro = (newValue == null) ? "" : newValue.toLowerCase();
            filtrada.setPredicate(nota -> {
                if (filtro.isEmpty()) return true;
                return (nota.getTitulo() != null && nota.getTitulo().toLowerCase().contains(filtro))
                        || String.valueOf(nota.getId()).contains(filtro)
                        || (nota.getContenido() != null && nota.getContenido().toLowerCase().contains(filtro));
            });
        });

        tablaNotas.setItems(filtrada);
    }

    //CARGAR NOTASSS
    // Lo llamaremos desde PacientesViewController
    public void setCitaContext(int idCitaRef) {
        this.idCitaRef = idCitaRef;
        cargarNotasDesdeBD();
    }

    private void cargarNotasDesdeBD() {
        listaNotas.clear();
        if (idCitaRef == null) return;

        List<Nota> notasBD = dao.getNotasPorCita(idCitaRef);
        for (Nota nBD : notasBD) {
            listaNotas.add(new Notas(
                    nBD.getIdNotas(),        // este será el idNotas real de la BD
                    nBD.getTitulo(),
                    nBD.getTextoNota(),
                    nBD.getFechaNota()
            ));
        }
    }
    //CARGAR NOTASSSS

    private void agregarBotonesAcciones() {
        Callback<TableColumn<Notas, Void>, TableCell<Notas, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(10, btnEditar, btnEliminar);

            {
                btnEditar.setStyle("-fx-background-color:#9ADDFF; -fx-text-fill:white; -fx-font-weight:bold;");
                btnEliminar.setStyle("-fx-background-color:#A9A9A9; -fx-text-fill:white; -fx-font-weight:bold;");

                btnEditar.setOnAction(e -> {
                    Notas nota = getTableView().getItems().get(getIndex());
                    abrirEditar(nota);
                });

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

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    //AGREGAR NOTA ACTUALIZADO
    @FXML
    private void AgregarNota() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/AgregarNotaView.fxml"));
            Parent root = loader.load();
            AgregarNotaController ctrl = loader.getController();

            // Al guardar: primero BD, luego la lista
            ctrl.setOnGuardar(nuevaNota -> {
                if (idCitaRef == null) {
                    mostrarAlerta("Error", "No se puede guardar la nota porque no se recibió el id de la cita.");
                    return;
                }

                // Guardar en BD
                Integer idGenerado = dao.crearNota(
                        idCitaRef,
                        nuevaNota.getTitulo(),
                        nuevaNota.getContenido(),
                        nuevaNota.getFecha()
                );

                if (idGenerado == null) {
                    mostrarAlerta("Error", "No se pudo guardar la nota en la base de datos.");
                    return;
                }

                // Actualizar el id de la nota en la tabla para que coincida con la BD
                nuevaNota.idProperty().set(idGenerado);

                listaNotas.add(nuevaNota);
                tablaNotas.refresh();
            }, this::nextId); // el nextId solo se usa para crear el objeto; luego lo sobreescribimos con el id real

            Stage stage = new Stage();
            stage.setTitle("Agregar Nota");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //AGREGAR NOTA ACTUALIZADO

    /* VIEJO AGREGAR NOTA
    @FXML
    private void AgregarNota() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/AgregarNotaView.fxml"));
            Parent root = loader.load();
            AgregarNotaController ctrl = loader.getController();

            // Al guardar: agrega a la tabla; el ID se genera aquí
            ctrl.setOnGuardar(nuevaNota -> {
                listaNotas.add(nuevaNota);
                tablaNotas.refresh();
            }, this::nextId);

            Stage stage = new Stage();
            stage.setTitle("Agregar Nota");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 VIEJO AGREGAR NOTA */

    //NUEVO ABRIR NOTA BD
    private void abrirEditar(Notas nota) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/EditarNotaView.fxml"));
            Parent root = loader.load();
            EditarNotaController ctrl = loader.getController();

            // Al guardar: actualizar en BD y luego refrescar tabla
            ctrl.setNota(nota, n -> {
                boolean ok = dao.actualizarNota(
                        n.getId(),
                        n.getTitulo(),
                        n.getContenido(),
                        n.getFecha()
                );
                if (!ok) {
                    mostrarAlerta("Error", "No se pudo actualizar la nota en la base de datos.");
                }
                tablaNotas.refresh();
            });

            Stage stage = new Stage();
            stage.setTitle("Editar Nota");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//NUEVO ABRIR NOTA BD


    /*  ABRIR NOTA ANTERIOR
    private void abrirEditar(Notas nota) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/EditarNotaView.fxml"));
            Parent root = loader.load();
            EditarNotaController ctrl = loader.getController();

            // Al guardar: refresca la tabla (es el mismo objeto editado)
            ctrl.setNota(nota, n -> tablaNotas.refresh());

            Stage stage = new Stage();
            stage.setTitle("Editar Nota");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     ABRIR NOTA ANTERIOR  */

    //ELIMINAR NOTA BD
    private void eliminarNota(Notas nota) {
        if (nota == null) return;

        boolean ok = dao.eliminarNota(nota.getId());
        if (!ok) {
            mostrarAlerta("Error", "No se pudo eliminar la nota en la base de datos.");
            return;
        }
        listaNotas.remove(nota);
    }
    // ELIMINAR NOTA BD

    //ELIMINAR NOTA ANTERIOR
    //private void eliminarNota(Notas nota) {
    //    listaNotas.remove(nota);
   // }



    // Genera un ID consecutivo simple
    private int nextId() {
        return listaNotas.stream().mapToInt(Notas::getId).max().orElse(0) + 1;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }



}
