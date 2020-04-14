package view;

import controller.ReversiController;
import javafx.stage.Stage;

/**
 * @author Wouter Folkertsma
 */
public class ReversiView extends AbstractView {
    public ReversiView(Stage stage, ReversiController reversiController) {
        super(stage, reversiController, "/resources/reversiview.fxml", 800, 800);
    }
}
