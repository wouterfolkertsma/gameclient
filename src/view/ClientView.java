package view;

import controller.ClientController;
import javafx.stage.Stage;

/**
 * Class ClientView building the login page
 *
 * @author Wouter Folkertsma
 */
public class ClientView extends AbstractView {
    public ClientView(Stage stage, ClientController clientController) {
        super(stage, clientController, "/resources/clientview.fxml", 800, 500);
    }
}
