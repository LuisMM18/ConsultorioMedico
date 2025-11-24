package consultorio;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
//PRUEBAPUSH
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlUrl = getClass().getResource("/vista/Loginview.fxml");

        if (fxmlUrl == null) {
            System.err.println("No se pudo encontrar el archivo FXML");
            fxmlUrl = getClass().getResource("/vista/LoginView.fxml");
        }

        if (fxmlUrl == null) {
            throw new RuntimeException("No se puede encontrar el archivo LoginView.fxml");
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Login - Consultorio Medico");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
