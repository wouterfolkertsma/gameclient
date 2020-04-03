package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import service.ServerService;
import view.LoginView;

import java.util.ArrayList;

/**
 * Class LoginController handles all events for the login page.
 *
 * @author Wouter Folkertsma
 */
public class LoginController extends AbstractController {
    private ServerService serverService;

    @FXML
    private TextField nameField;

    @FXML
    private Button submitButton;

    @FXML
    private LoginView loginView;

    public LoginController(ServerService serverService) {
        this.serverService = serverService;
    }

    @SuppressWarnings("unused")
    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        Window owner = submitButton.getScene().getWindow();

        if(nameField.getText().isEmpty()) {
            showAlert(owner, "Please fill in your name!");
            return;
        }

        ArrayList<String> response = this.serverService.login(nameField.getText());

        if (response.size() < 1) {
            showAlert(owner, "Could not connect to server!");
            return;
        }

        this.gameClient.login(nameField.getText());
    }

    private static void showAlert(Window owner, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}
