package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import main.GameClient;
import model.Move;
import service.ServerService;

import java.util.concurrent.TimeUnit;

/**
 * @author Wouter Folkertma
 *         Djurre Eikema
 */
public class TicTacToeController extends AbstractController {
    private char whoseTurn = 'X';
    private Cell[][] cell = new Cell[3][3];
    private ServerService serverService;
    private Boolean myTurn = false;

    @FXML
    private GridPane grid;

    public TicTacToeController(ServerService serverService, GameClient gameClient) {
        this.serverService = serverService;
        this.gameClient = gameClient;
    }

    public void initialize() {
        int pos = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid.add(cell[i][j] = new Cell(pos), j, i);
                pos += 1;
            }
        }
    }

    public void handleOpponentTurn(Move move) {
        int number = move.getMove();
        int column = number % 3;
        int row = number / 3;

        Cell currentCell = cell[row][column];
        currentCell.setToken(whoseTurn);
        currentCell.drawToken(whoseTurn);
        checkGameStatus();
    }

    public class Cell extends Pane {
        private char token = ' ';
        private int pos;

        public Cell(int pos) {
            this.pos = pos;
            setStyle("-fx-border-color: black");
            this.setPrefSize(2000, 2000);
            this.setOnMouseClicked(e -> handleMouseClick());
        }

        char getToken() {
            return token;
        }

        public void setToken(char c) {
            token = c;
        }

        public void drawToken(char token) {
            System.out.println("DRAWING ON " + this.pos);
            if (token == 'X') {
                drawX();
            } else if (token == 'O') {
                drawO();
            }
        }

        /* Handle a mouse click event */
        private void handleMouseClick() {
            System.out.println(myTurn);
            if (!isMultiplayer && token == ' ' && whoseTurn != ' ') {
                setToken(whoseTurn);
                drawToken(whoseTurn);
                checkGameStatus();
                this.setDisable(true);

                if (isBot) {
                    Cell bestCell = calculateBestMove();
                    bestCell.setToken(whoseTurn);
                    bestCell.drawToken(whoseTurn);
                    checkGameStatus();
                }
            }
            else if (isMultiplayer && token == ' ' && whoseTurn != ' ' && myTurn) {
                setToken(whoseTurn);
                drawToken(whoseTurn);
                checkGameStatus();
                makeMove(pos);
                this.setDisable(true);

                myTurn = false;
            }
        }

        private void drawX() {
            Line line1 = new Line(10, 10, this.getWidth() - 10, this.getHeight() - 10);
            line1.endXProperty().bind(this.widthProperty().subtract(10));
            line1.endYProperty().bind(this.heightProperty().subtract(10));
            Line line2 = new Line(10, this.getHeight() - 10, this.getWidth() - 10, 10);
            line2.startYProperty().bind(this.heightProperty().subtract(10));
            line2.endXProperty().bind(this.widthProperty().subtract(10));

            this.getChildren().addAll(line1, line2);
        }

        private void drawO() {
            Ellipse ellipse = new Ellipse(this.getWidth() / 2,
                    this.getHeight() / 2, this.getWidth() / 2 - 10,
                    this.getHeight() / 2 - 10);
            ellipse.centerXProperty().bind(
                    this.widthProperty().divide(2));
            ellipse.centerYProperty().bind(
                    this.heightProperty().divide(2));
            ellipse.radiusXProperty().bind(
                    this.widthProperty().divide(2).subtract(10));

            ellipse.radiusYProperty().bind(
                    this.heightProperty().divide(2).subtract(10));
            ellipse.setStroke(Color.BLACK);
            ellipse.setFill(Color.WHITE);

            getChildren().add(ellipse);
        }
    }

    private void checkGameStatus() {
        if (checkIfWon(whoseTurn)) {
            System.out.print(whoseTurn + " won! The game is over\n");
            whoseTurn = ' '; // Game is over
            this.resetGame();
        } else if (boardIsFull()) {
            System.out.print("Draw! The game is over\n");
            whoseTurn = ' '; // Game is over
            this.resetGame();
        } else {
            whoseTurn = (whoseTurn == 'X') ? 'O' : 'X';
        }
    }

    public void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell currentCell = cell[i][j];
                currentCell.setToken(' ');
                currentCell.getChildren().clear();
            }
        }

        whoseTurn = 'X';

        this.gameClient.endTicTactToe();
    }

    private void makeMove(int pos) {
        this.serverService.makeMove(pos);
    }

    public void setMyTurn() {
        myTurn = true;

        if (isBot) {
            try {
                TimeUnit.MILLISECONDS.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Cell bestCell = calculateBestMove();
            bestCell.setToken(whoseTurn);
            bestCell.drawToken(whoseTurn);
            makeMove(bestCell.pos);
            checkGameStatus();
            bestCell.setDisable(true);

            myTurn = false;
        }
    }

    public boolean boardIsFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (cell[i][j].getToken() == ' ')
                    return false;

        return true;
    }

    public boolean checkIfWon(char token) {
        for (int i = 0; i < 3; i++)
            if (cell[i][0].getToken() == token
                    && cell[i][1].getToken() == token
                    && cell[i][2].getToken() == token) {
                return true;
            }

        for (int j = 0; j < 3; j++)
            if (cell[0][j].getToken() == token
                    && cell[1][j].getToken() == token
                    && cell[2][j].getToken() == token) {
                return true;
            }

        if (cell[0][0].getToken() == token
                && cell[1][1].getToken() == token
                && cell[2][2].getToken() == token) {
            return true;
        }

        if (cell[0][2].getToken() == token
                && cell[1][1].getToken() == token
                && cell[2][0].getToken() == token) {
            return true;
        }

        return false;

    }

    public Cell calculateBestMove() {
        int bestScore = -1000;
        Cell bestCell = cell[0][0];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cell[i][j].getToken() == ' ') {

                    cell[i][j].setToken(whoseTurn);
                    int score = minimax(0, false);
                    cell[i][j].setToken(' ');

                    if (score > bestScore) {
                        bestScore = score;
                        bestCell = cell[i][j];
                    }

                }
            }
        }

        return bestCell;
    }

    private int minimax(int depth, boolean maximise) {
        int score = evaluate();
        if (score == 10 || score == -10)
            return score;

        if (boardIsFull())
            return 0;

        if (maximise) {
            int bestScore = -1000;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (cell[i][j].getToken() == ' ') {
                        cell[i][j].setToken(whoseTurn);
                        bestScore = Math.max(bestScore, minimax(depth + 1, false));
                        cell[i][j].setToken(' ');
                    }
                }
            }

            return bestScore;
        } else {
            int bestScore = 1000;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (cell[i][j].getToken() == ' ') {
                        cell[i][j].setToken(getCurrentOpponent());
                        bestScore = Math.min(bestScore, minimax( depth + 1, true));
                        cell[i][j].setToken(' ');
                    }
                }
            }

            return bestScore;
        }
    }

    private int evaluate()
    {
        for (int row = 0; row < 3; row++)
        {
            if (cell[row][0].getToken() == cell[row][1].getToken() && cell[row][1].getToken() == cell[row][2].getToken())
            {
                if (cell[row][0].getToken() == whoseTurn)
                    return +10;
                else if (cell[row][0].getToken() == getCurrentOpponent())
                    return -10;
            }
        }

        for (int col = 0; col < 3; col++)
        {
            if (cell[0][col].getToken() == cell[1][col].getToken() &&
                    cell[1][col].getToken() == cell[2][col].getToken())
            {
                if (cell[0][col].getToken() == whoseTurn)
                    return +10;

                else if (cell[0][col].getToken() == getCurrentOpponent())
                    return -10;
            }
        }

        if (cell[0][0].getToken() == cell[1][1].getToken() && cell[1][1].getToken() == cell[2][2].getToken())
        {
            if (cell[0][0].getToken() == whoseTurn)
                return +10;
            else if (cell[0][0].getToken() == getCurrentOpponent())
                return -10;
        }

        if (cell[0][2].getToken() == cell[1][1].getToken() && cell[1][1].getToken() == cell[2][0].getToken())
        {
            if (cell[0][2].getToken() == whoseTurn)
                return +10;
            else if (cell[0][2].getToken() == getCurrentOpponent())
                return -10;
        }

        return 0;
    }

    private char getCurrentOpponent() {
        return (whoseTurn == 'X') ? 'O' : 'X';
    }
}
