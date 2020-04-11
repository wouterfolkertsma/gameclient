package view;

import controller.ReversiController;
import javafx.stage.Stage;

public class ReversiView extends AbstractView {
    public ReversiView(Stage stage, ReversiController reversiController) {
        super(stage, reversiController, "/resources/reversiview.fxml");
    }
}
