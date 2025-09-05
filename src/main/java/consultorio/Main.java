package consultorio;

import consultorio.vista.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Crear instancia del LoginView
        LoginView loginView = new LoginView();

        // Crear la escena con el LoginView
        Scene scene = new Scene(loginView, 900, 600);

        stage.setTitle("Login - Consultorio Médico");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}