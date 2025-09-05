package consultorio.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.InputStream;

public class LoginView extends BorderPane {

    private Button loginButton;

    public LoginView() {
        initializeUI();
    }

    private void initializeUI() {
        // Crear el botón de login primero para poder referenciarlo
        loginButton = new Button("Login");
        loginButton.setPrefWidth(120);
        loginButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold;");




        // Logo - cargar desde recursos
        ImageView logo = createImageView("/imagenes/Logo.png", 300, 300);

        VBox topBox = new VBox(10, logo);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20, 5, 10, 5));

        // --- Formulario de login ---
        Label formTitle = new Label("Consultorio Médico");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 25));

        Label userLabel = new Label("Usuario:");
        TextField userField = new TextField();
        userField.setPromptText("Usuario");

        Label passLabel = new Label("Contraseña:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Contraseña");

        // Icono del doctor - cargar desde recursos
        ImageView doctorIcon = createImageView("/imagenes/doctor.png", 100, 100);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.add(userLabel, 0, 0);
        formGrid.add(userField, 1, 0);
        formGrid.add(passLabel, 0, 1);
        formGrid.add(passField, 1, 1);
        formGrid.add(loginButton, 1, 2);
        formGrid.setAlignment(Pos.CENTER_LEFT);

        HBox formWithIcon = new HBox(30, formGrid, doctorIcon);
        formWithIcon.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(15, formTitle, formWithIcon);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        centerBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: white;");

        // --- Layout principal ---
        this.setTop(topBox);
        this.setCenter(centerBox);
        this.setStyle("-fx-background-color: #f8f9fa;");
    }

    private ImageView createImageView(String resourcePath, double width, double height) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream != null) {
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);
                return imageView;
            } else {
                System.err.println("No se pudo cargar la imagen: " + resourcePath);
                // Crear un ImageView vacío como fallback
                return new ImageView();
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + resourcePath);
            e.printStackTrace();
            return new ImageView();
        }
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public TextField getUserField() {

        return null;
    }

    public PasswordField getPassField() {

        return null;
    }
}