package view;

import controller.TicTacToeController;
import javafx.stage.Stage;

public class TicTacToeView extends AbstractView {
    public TicTacToeView(Stage stage, TicTacToeController ticTacToeController) {
        super(stage, ticTacToeController, "/resources/tictactoeview.fxml", 800, 800);
    }
}
