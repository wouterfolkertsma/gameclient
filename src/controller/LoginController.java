package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import view.LoginView;

/**
 * Class LoginController handles all events for the login page.
 *
 * @author Wouter Folkertsma
 */
public class LoginController extends AbstractController {
    @FXML
    private TextField nameField;

    @FXML
    private Button submitButton;

    @FXML
    private LoginView loginView;

    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        Window owner = submitButton.getScene().getWindow();

        if(nameField.getText().isEmpty()) {
            showAlert(owner);
            return;
        }

        this.gameClient.login(nameField.getText());
    }

    private static void showAlert(Window owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Form Error!");
        alert.setHeaderText(null);
        alert.setContentText("Please enter your name");
        alert.initOwner(owner);
        alert.show();
    }
}
