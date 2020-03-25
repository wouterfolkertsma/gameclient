package view;

import controller.ClientController;
import javafx.stage.Stage;

public class ClientView extends AbstractView {
    public ClientView(Stage stage, ClientController clientController) {
        super(stage, clientController, "/resources/clientview.fxml");
    }
}
