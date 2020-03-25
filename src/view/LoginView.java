package view;

import controller.LoginController;
import javafx.stage.Stage;

public class LoginView extends AbstractView {
    public LoginView(Stage stage, LoginController loginController) {
        super(stage, loginController, "/resources/loginview.fxml");
    }
}
