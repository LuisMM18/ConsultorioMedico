package consultorio.controlador;

import consultorio.DAO;
import consultorio.Rol;
import consultorio.model.CitaCalendario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador de la vista InicioView.fxml
 * Muestra estadísticas generales y las citas del día actual.
 */
public class InicioController {

    @FXML
    private VBox citasHoyBox;
    @FXML
    private VBox atendidasHoyBox;
    @FXML
    private VBox pendientesHoyBox;
    @FXML
    private TableView<CitaData> tablaCitasHoy;
    @FXML
    private Label lblBienvenida;


    private final DAO dao = new DAO();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
        actualizarBienvenida();
    }
    public void actualizarBienvenida() {
        int rol = Rol.getInstance().getRol();
        String mensaje = switch (rol) {
            case 1 -> "Bienvenido Administrador";
            case 2 -> "Bienvenido Doctor";
            case 3 -> "Bienvenido Recepcionista";
            default -> "Bienvenido Usuario";
        };

        lblBienvenida.setText(mensaje);
    }



    private void configurarTabla() {
        TableColumn<CitaData, String> horaCol = new TableColumn<>("Hora");
        TableColumn<CitaData, String> pacienteCol = new TableColumn<>("Paciente");
        TableColumn<CitaData, String> medicoCol = new TableColumn<>("Médico");
        TableColumn<CitaData, String> estadoCol = new TableColumn<>("Estado");

        horaCol.setCellValueFactory(new PropertyValueFactory<>("hora"));
        pacienteCol.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        medicoCol.setCellValueFactory(new PropertyValueFactory<>("medico"));
        estadoCol.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaCitasHoy.getColumns().setAll(horaCol, pacienteCol, medicoCol, estadoCol);
    }

    @FXML
    private void cargarDatos() {
        LocalDate hoy = LocalDate.now();
        List<CitaCalendario> citas = dao.getCitasCalendarioForDate(hoy);

        // Hora actual
        LocalDateTime ahora = LocalDateTime.now();

        // Contadores
        int atendidas = 0;
        int pendientes = 0;

        // Convertir a ObservableList<CitaData> mapeando los campos necesarios
        ObservableList<CitaData> datosTabla = FXCollections.observableArrayList();

        for (CitaCalendario c : citas) {
            // Obtener strings preparados
            String hora = c.getFechaHora().toLocalTime().toString(); // o formatea como quieras
            String paciente = c.getPacienteNombre() != null ? c.getPacienteNombre() : "—";
            String medico = c.getUsuarioNombre() != null ? c.getUsuarioNombre() : "—";

            // Lógica de estado: si la cita +1h ya pasó -> Atendida, si la cita está en el futuro -> Pendiente
            String estado;
            if (c.getFechaHora().plusHours(1).isBefore(ahora) || c.getFechaHora().plusHours(1).isEqual(ahora)) {
                estado = "Atendida";
                atendidas++;
            } else if (c.getFechaHora().isAfter(ahora)) {
                estado = "Pendiente";
                pendientes++;
            } else {
                // Caso intermedio: la cita está ocurriendo (por ejemplo, en la franja de la hora)
                // Puedes decidir considerarla atendida o pendiente; aquí la dejamos como "En curso"
                estado = "En curso";
            }

            datosTabla.add(new CitaData(hora, paciente, medico, estado));
        }

        // Actualizar contadores en los VBoxes
        actualizarVBox(citasHoyBox, citas.size());
        actualizarVBox(atendidasHoyBox, atendidas);
        actualizarVBox(pendientesHoyBox, pendientes);

        // Poner los datos en la tabla (ahora con el tipo correcto)
        tablaCitasHoy.setItems(datosTabla);
    }


    private void actualizarVBox(VBox box, int valor) {
        if (box.getChildren().size() >= 2 && box.getChildren().get(1) instanceof Label) {
            Label valorLabel = (Label) box.getChildren().get(1);
            valorLabel.setText(String.valueOf(valor));
        }
    }

    /**
     * Clase interna para representar los datos que se muestran en la tabla.
     */
    public static class CitaData {
        private final String hora;
        private final String paciente;
        private final String medico;
        private final String estado;

        public CitaData(String hora, String paciente, String medico, String estado) {
            this.hora = hora;
            this.paciente = paciente;
            this.medico = medico;
            this.estado = estado;
        }

        public String getHora() { return hora; }
        public String getPaciente() { return paciente; }
        public String getMedico() { return medico; }
        public String getEstado() { return estado; }
    }
}
