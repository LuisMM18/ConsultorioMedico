package consultorio.controlador;

import consultorio.Rol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.time.LocalDate;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnInicio, btnCitas, btnPacientes, btnCalendario, btnAjustes;
    @FXML private Label lblUsuario;
    private Parent calendarioRoot = null;
    private FXMLLoader calendarioLoader = null;
    private Parent ajustesRoot = null;
    private FXMLLoader ajustesLoader = null;

    @FXML
    public void initialize() {
        actualizarBienvenida();

        // Solo cargar la vista de inicio por ahora
        cambiarVistaInicio();
    }

    public void actualizarBienvenida() {
        int rol = Rol.getInstance().getRol();
        String mensaje = switch (rol) {
            case 1 -> "Bienvenido Administrador";
            case 2 -> "Bienvenido Doctor";
            case 3 -> "Bienvenido Recepcionista";
            default -> "Bienvenido Usuario";
        };

        lblUsuario.setText(mensaje);
    }

    @FXML
    public void cambiarVistaInicio() {
        try {
            // Cargar el archivo FXML de la vista de citas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/InicioView.fxml"));
            Parent vistaCitas = loader.load();

            // Reemplazar el contenido actual con la nueva vista
            contentArea.getChildren().setAll(vistaCitas);

            // Mantener el botón activo
            actualizarBotonActivo(btnInicio);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cambiarVistaCitas() {
        try {
            // Cargar el archivo FXML de la vista de citas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/ListaVistaDia.fxml"));
            Parent vistaCitas = loader.load();

            // Reemplazar el contenido actual con la nueva vista
            contentArea.getChildren().setAll(vistaCitas);

            // Mantener el botón activo
            actualizarBotonActivo(btnCitas);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cambiarVistaCitasFecha(LocalDate fecha){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/ListaVistaDia.fxml"));
            Parent VistaCitas = loader.load();

            ListaVistaDiaController controller = loader.getController();
            if (controller != null) {
                controller.setFecha(fecha);
            }

            contentArea.getChildren().setAll(VistaCitas);

            actualizarBotonActivo(btnCitas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cambiarVistaCalendario(){
        try {

            if (calendarioRoot == null) {
                calendarioLoader = new FXMLLoader(getClass().getResource("/vista/CalendarioView.fxml"));
                calendarioRoot = calendarioLoader.load();

                // Pasar el MainController al controller del calendario
                Object ctrl = calendarioLoader.getController();
                if (ctrl instanceof consultorio.controlador.CalendarController) {
                    ((consultorio.controlador.CalendarController) ctrl).setMainController(this);
                }

                // Hacer que la vista ocupe todo el contentArea
                if (calendarioRoot instanceof javafx.scene.layout.Region) {
                    javafx.scene.layout.Region region = (javafx.scene.layout.Region) calendarioRoot;
                    region.prefWidthProperty().bind(contentArea.widthProperty());
                    region.prefHeightProperty().bind(contentArea.heightProperty());
                } else {
                    javafx.scene.layout.AnchorPane.setTopAnchor(calendarioRoot, 0.0);
                    javafx.scene.layout.AnchorPane.setBottomAnchor(calendarioRoot, 0.0);
                    javafx.scene.layout.AnchorPane.setLeftAnchor(calendarioRoot, 0.0);
                    javafx.scene.layout.AnchorPane.setRightAnchor(calendarioRoot, 0.0);
                }
            }
            // Mostrar la vista (cached)
            contentArea.getChildren().setAll(calendarioRoot);
            actualizarBotonActivo(btnCalendario);

        } catch (NullPointerException npe) {
            npe.printStackTrace();
            System.err.println("Ruta FXML nula — revisa la ubicación de /vista/CalendarioView.fxml");
            mostrarVistaDeError("Calendario no encontrado (ruta FXML incorrecta)");
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarVistaDeError("Error inesperado al cargar calendario");
        }
    }



    @FXML
    private void cambiarVistaPacientes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PacientesView.fxml"));
            Parent vistaCitas = loader.load();

            contentArea.getChildren().setAll(vistaCitas);

            actualizarBotonActivo(btnPacientes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void cambiarVistaAjustes(){
        if(Rol.getInstance().getRol() == 3){
            mostrarError("No cuenta con el rol necesario para acceder a esta ");
        }else {
            try {
                if (ajustesRoot == null) {
                    ajustesLoader = new FXMLLoader(getClass().getResource("/vista/Ajustes.fxml"));
                    ajustesRoot = ajustesLoader.load();

                    Object ctrl = ajustesLoader.getController();
                    if (ctrl instanceof consultorio.controlador.AjustesController) {
                        ((consultorio.controlador.AjustesController) ctrl).setMainController(this);
                    }

                    if (ajustesRoot instanceof javafx.scene.layout.Region) {
                        javafx.scene.layout.Region region = (javafx.scene.layout.Region) ajustesRoot;
                        region.prefWidthProperty().bind(contentArea.widthProperty());
                        region.prefHeightProperty().bind(contentArea.heightProperty());
                    } else {
                        javafx.scene.layout.AnchorPane.setTopAnchor(ajustesRoot, 0.0);
                        javafx.scene.layout.AnchorPane.setBottomAnchor(ajustesRoot, 0.0);
                        javafx.scene.layout.AnchorPane.setLeftAnchor(ajustesRoot, 0.0);
                        javafx.scene.layout.AnchorPane.setRightAnchor(ajustesRoot, 0.0);
                    }
                }

                contentArea.getChildren().setAll(ajustesRoot);
                actualizarBotonActivo(btnAjustes);

            } catch (NullPointerException npe) {
                npe.printStackTrace();
                System.err.println("Ruta FXML nula — revisa la ubicación de /vista/Ajustes.fxml");
                mostrarVistaDeError("Ajustes no encontrado (ruta FXML incorrecta)");
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarVistaDeError("Error inesperado al cargar ajustes");
            }
        }
    }


    private void mostrarVistaDeError(String mensaje) {
        VBox vistaError = new VBox();
        vistaError.getChildren().add(new Label(mensaje));
        contentArea.getChildren().setAll(vistaError);
    }

    private void actualizarBotonActivo(Button botonActivo) {
        Button[] botones = {btnInicio, btnCitas, btnCalendario, btnPacientes, btnAjustes};
        for (Button boton : botones) {
            boton.getStyleClass().remove("nav-button-active");
        }

        if (botonActivo != null) {
            botonActivo.getStyleClass().add("nav-button-active");
        }
    }

    private void checarRol(){

    }

    @FXML
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Loginview.fxml"));
            Parent root = (Parent) loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login - Consultorio Medico");
            stage.setScene(new Scene(root,1200, 800));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.hide();
            System.out.println("Cerrando sesión...");

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
    }
    void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}