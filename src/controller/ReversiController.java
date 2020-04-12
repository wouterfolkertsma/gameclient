package controller;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import model.Move;
import service.ServerService;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

// EEN AANTAL METHODES MOETEN AANGEPAST WORDEN AAN DE REGELS VAN REVERSI
public class ReversiController extends AbstractController{
    private char whoseTurn = 'W';
    private Cell[][] cell = new Cell[8][8];

    private ServerService serverService;
    private Boolean myTurn = false;
    private int WScore;
    private int BScore;

    @FXML
    private GridPane grid;

    public ReversiController(ServerService serverService) {
        this.serverService = serverService;
    }

    public void initialize() {
        int pos = 0;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                grid.add(cell[i][j] = new Cell(pos), j, i);
                pos += 1;
            }
        setStartPositions();

    }

    public void setStartPositions(){
        cell[3][3].setToken('W');
        cell[3][4].setToken('B');
        cell[4][3].setToken('B');
        cell[4][4].setToken('W');
    }

    public void handleOpponentTurn(Move move) {
        int number = move.getMove();
        int column = number % 8;
        int row = number / 8;

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
            setStyle("-fx-border-color: bldsasdaack");
            this.setPrefSize(2000, 2000);
            this.setOnMouseClicked(e -> handleMouseClick());
        }

        public char getToken() {
            return token;
        }

        public void setToken(char c) {
            token = c;
        }

        public void drawToken(char token) {
            for(int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (cell[i][j].getToken() == 'W') {
                        drawWhite();
                    } else if (cell[i][j].getToken() == 'B') {
                        drawBlack();
                    }
                }
            }
        }

        /* Handle a mouse click event */
        private void handleMouseClick() {
            if (token == ' ' && whoseTurn != ' ' && myTurn) {
                setToken(whoseTurn);
                drawToken(whoseTurn);
                makeMove(this.pos);
                checkGameStatus();
                this.setDisable(true);
                myTurn = false;
            }
        }

        private void drawWhite() {
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

        private void drawBlack() {
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
            ellipse.setFill(Color.BLACK);

            getChildren().add(ellipse);
        }
    }

    private void checkGameStatus() {
        if (checkIfWon(whoseTurn)) {
            System.out.print(whoseTurn + " won! The game is over\n");
            whoseTurn = ' '; // Game is over
        } else if (boardIsFull()) {
            System.out.print("Draw! The game is over\n");
            whoseTurn = ' '; // Game is over
        } else {
            whoseTurn = (whoseTurn == 'W') ? 'B' : 'W';
        }
    }

    private void makeMove(int pos) {
        if(isValidMove())
        this.serverService.makeMove(pos);
    }

    public void setMyTurn() {
        myTurn = true;

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

    public boolean boardIsFull() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (cell[i][j].getToken() == ' ')
                    return false;

        return true;
    }

    private void findLocations(char W, char B, HashMap<Integer, Integer> validLocations) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cell[i][j].getToken() == B) {
                    int I = i, J = j;
                    if (i - 1 >= 0 && j - 1 >= 0 && cell[i - 1][j - 1].getToken() == ' ') {
                        i = i + 1;
                        j = j + 1;
                        while (i < 7 && j < 7 && cell[i][j].getToken() == 'B') {
                            i++;
                            j++;
                        }
                        if (i <= 7 && j <= 7 && cell[i][j].getToken() == 'W') {
                            validLocations.put(I - 1, J - 1);
                        }
                    }
                    i = I;
                    j = J;
                    if (i - 1 >= 0 && cell[i - 1][j].getToken() == ' ') {
                        i = i + 1;
                        while (i < 7 && cell[i][j].getToken() == 'B') i++;
                        if (i < 7 && cell[i][j].getToken() == 'W') validLocations.put(I - 1, J);
                    }
                    i = I;
                    if (i - 1 >= 0 && j + 1 <= 7 && cell[i - 1][j + 1].getToken() == ' ') {
                        i = i + 1;
                        j = j - 1;
                        while (i < 7 && j > 0 && cell[i][j].getToken() == 'B') {
                            i++;
                            j++;
                        }
                        if (i <= 7 && j >= 0 && cell[i][j].getToken() == 'W') {
                            validLocations.put(I - 1, J + 1);
                        }
                    }
                    i = I;
                    j = J;
                    if (j - 1 >= 0 && cell[i][j - 1].getToken() == ' ') {
                        j = j + 1;
                        while (j < 7 && cell[i][j].getToken() == 'B') j++;
                        if (j <= 7 && cell[i][j].getToken() == 'W') validLocations.put(I, J - 1);
                    }
                    j = J;
                    if (j + 1 <= 7 && cell[i][j].getToken() == ' ') {
                        j = j - 1;
                        while (j > 0 && cell[i][j].getToken() == 'B') j--;
                        if (j >= 0 && cell[i][j].getToken() == 'W') validLocations.put(I, J + 1);
                    }
                    if (i + 1 <= 7 && j - 1 >= 0 && cell[i][j].getToken() == ' ') {
                        i = i - 1;
                        j = j + 1;
                        while (i > 0 && j < 7 && cell[i][j].getToken() == 'B') {
                            i--;
                            j++;
                        }
                        if (i >= 0 && j <= 7 && cell[i][j].getToken() == 'W') validLocations.put(I + 1, J - 1);
                    }
                    if (i + 1 <= 7 && cell[i][j].getToken() == ' ') {
                        i = i - 1;
                        while (i > 0 && cell[i][j].getToken() == 'B') i--;
                        if (i >= 0 && cell[i][j].getToken() == 'W') validLocations.put(I + 1, J);
                    }
                    i = I;
                    if (i + 1 <= 7 && j + 1 <= 7 && cell[i][j].getToken() == ' ') {
                        i = i - 1;
                        j = j - 1;
                        while (i > 0 && j > 0 && cell[i][j].getToken() == 'B') {
                            i--;
                            j--;
                        }
                        if (i >= 0 && j >= 0 && cell[i][j].getToken() == 'W') validLocations.put(I + 1, J + 1);
                    }
                    i = I;
                    j = J;
                }
            }
        }
    }

    public boolean isValidMove(){
        return false;
    }



    public boolean checkIfWon(char token) {
        for (int i = 0; i < 8; i++)
            if (cell[i][0].getToken() == token
                    && cell[i][1].getToken() == token
                    && cell[i][2].getToken() == token) {
                return true;
            }

        for (int j = 0; j < 8; j++)
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

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cell[i][j].getToken() == ' ') {

                    cell[i][j].setToken(whoseTurn);
                    int score = minimax(0, false);
                    cell[i][j].setToken(' ');

                    System.out.println(score);
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

        System.out.println("SCORE " + score);

        if (score == 10 || score == -10)
            return score;

        if (boardIsFull())
            return 0;

        if (maximise) {
            int bestScore = -1000;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
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
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
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
        for (int row = 0; row < 8; row++)
        {
            if (cell[row][0].getToken() == cell[row][1].getToken() && cell[row][1].getToken() == cell[row][2].getToken())
            {
                if (cell[row][0].getToken() == whoseTurn)
                    return +10;
                else if (cell[row][0].getToken() == getCurrentOpponent())
                    return -10;
            }
        }

        for (int col = 0; col < 8; col++)
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
        return (whoseTurn == 'W') ? 'B' : 'W';
    }
}
