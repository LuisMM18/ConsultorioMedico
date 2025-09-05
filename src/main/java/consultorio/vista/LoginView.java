package consultorio.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends BorderPane {

    public LoginView() {
        initializeUI();
    }

    private void initializeUI() {
        // --- Logo y título superior ---
        Label title = new Label("Salud Integral Logo Design.png");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        // Logo
        ImageView logo = new ImageView(new Image(
                "Salud Integral Logo Design.png"
        ));
        logo.setFitHeight(60);
        logo.setFitWidth(60);

        VBox topBox = new VBox(10, logo, title);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20, 0, 20, 0));

        // --- Formulario de login ---
        Label formTitle = new Label("Consultorio Médico");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label userLabel = new Label("Usuario:");
        TextField userField = new TextField();
        userField.setPromptText("Usuario");

        Label passLabel = new Label("Contraseña:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Contraseña");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(120);
        loginButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold;");

        // Icono del doctor
        ImageView doctorIcon = new ImageView(new Image("https://img.icons8.com/ios-filled/100/4a90e2/doctor-male.png"));
        doctorIcon.setFitWidth(60);
        doctorIcon.setFitHeight(60);

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


    public Button getLoginButton() {

        return null;
    }
}