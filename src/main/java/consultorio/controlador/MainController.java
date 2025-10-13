package consultorio.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnInicio, btnCitas, btnPacientes, btnAgendar, btnCalendario, btnAjustes, btnNotas;
    @FXML private Label lblUsuario;
    private Parent calendarioRoot = null;
    private FXMLLoader calendarioLoader = null;
    private Parent ajustesRoot = null;
    private FXMLLoader ajustesLoader = null;

    @FXML
    public void initialize() {
        System.out.println("MainController inicializado");

        // Solo cargar la vista de inicio por ahora
        cambiarVistaInicio();
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

    @FXML
    public void cambiarVistaCalendario(){
        try {
            // Cargar el archivo FXML de la vista de citas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/CalendarioView.fxml"));
            Parent vistaCitas = loader.load();

            // Reemplazar el contenido actual con la nueva vista
            contentArea.getChildren().setAll(vistaCitas);

            // Mantener el botón activo
            actualizarBotonActivo(btnCalendario);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cambiarVistaNotas(){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Notas.fxml"));
                Parent vistaNotas = loader.load();
                /*
                if (vistaNotas instanceof javafx.scene.layout.Region) {
                    ((javafx.scene.layout.Region) vistaNotas).prefWidthProperty().bind(contentArea.widthProperty());
                    ((javafx.scene.layout.Region) vistaNotas).prefHeightProperty().bind(contentArea.heightProperty());
                }
                */


                contentArea.getChildren().setAll(vistaNotas);
                actualizarBotonActivo(btnNotas);

            } catch (IOException e) {
                e.printStackTrace();
                mostrarVistaDeError("No se pudo cargar la vista de Notas.");
            }

        }

    @FXML
    private void cambiarVistaPacientes() {
        try {
            // Cargar el archivo FXML de la vista de citas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PacientesView.fxml"));
            Parent vistaCitas = loader.load();

            // Reemplazar el contenido actual con la nueva vista
            contentArea.getChildren().setAll(vistaCitas);

            // Mantener el botón activo
            actualizarBotonActivo(btnPacientes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cambiarVistaAjustes(){
        try {
            // Cargar solo la primera vez (cache)
            if (ajustesRoot == null) {
                // Ajusta la ruta según tu estructura de resources
                ajustesLoader = new FXMLLoader(getClass().getResource("/vista/Ajustes.fxml"));
                ajustesRoot = ajustesLoader.load();

                // Si quieres pasar la referencia del MainController al controlador de ajustes:
                Object ctrl = ajustesLoader.getController();
                if (ctrl instanceof consultorio.controlador.AjustesController) {
                    ((consultorio.controlador.AjustesController) ctrl).setMainController(this);
                }

                /* Hacer que la vista ocupe todo el contentArea */
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

            // Reemplazar el contenido del contentArea por la vista de ajustes
            contentArea.getChildren().setAll(ajustesRoot);
            actualizarBotonActivo(btnAjustes); // o el botón que corresponda para Ajustes

        } catch (NullPointerException npe) {
            npe.printStackTrace();
            System.err.println("Ruta FXML nula — revisa la ubicación de /vista/Ajustes.fxml");
            mostrarVistaDeError("Ajustes no encontrado (ruta FXML incorrecta)");
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarVistaDeError("Error inesperado al cargar ajustes");
        }
    }

    @FXML
    private void cambiarVistaAgendar() {
        try {
            // Cargar el archivo FXML de la vista de citas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/AgendarNuevaCitaView.fxml"));
            Parent vistaCitas = loader.load();

            // Reemplazar el contenido actual con la nueva vista
            contentArea.getChildren().setAll(vistaCitas);

            // Mantener el botón activo
            actualizarBotonActivo(btnAgendar);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarVistaDeError(String mensaje) {
        VBox vistaError = new VBox();
        vistaError.getChildren().add(new Label(mensaje));
        contentArea.getChildren().setAll(vistaError);
    }

    private void actualizarBotonActivo(Button botonActivo) {
        // Restablecer todos los botones
        Button[] botones = {btnInicio, btnCitas, btnCalendario, btnPacientes, btnAgendar, btnAjustes,btnNotas};
        for (Button boton : botones) {
            boton.getStyleClass().remove("nav-button-active");
        }

        // Establecer el botón activo
        if (botonActivo != null) {
            botonActivo.getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            // Aquí irá la lógica para volver al login
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
}