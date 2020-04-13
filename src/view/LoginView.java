package view;

import controller.LoginController;
import javafx.stage.Stage;

/**
 * Class LoginView building the login page
 *
 * @author Wouter Folkertsma
 */
public class LoginView extends AbstractView {
    public LoginView(Stage stage, LoginController loginController) {
        super(stage, loginController, "/resources/loginview.fxml", 800, 500);
    }
}
