package controller;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import main.GameClient;
import model.Move;
import service.ServerService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author Wouter Folkertsma
 *         Daniel Windstra
 */
public class ReversiController extends AbstractController{
    private char whoseTurn = 'B';
    private Cell[][] cell = new Cell[8][8];

    private ServerService serverService;
    private Boolean myTurn = false;
    private int WScore;
    private int BScore;
    private int remaining;

    @FXML
    private GridPane grid;

    public ReversiController(ServerService serverService, GameClient gameClient) {
        this.serverService = serverService;
        this.gameClient = gameClient;
    }

    public void initialize() {
        int pos = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid.add(cell[i][j] = new Cell(pos, i, j), j, i);
                pos += 1;
            }
        }

        setStartPositions();
    }

    public void setStartPositions() {
        cell[3][3].setToken('W');
        cell[3][3].drawWhite();

        cell[3][4].setToken('B');
        cell[3][4].drawBlack();

        cell[4][3].setToken('B');
        cell[4][3].drawBlack();

        cell[4][4].setToken('W');
        cell[4][4].drawWhite();
    }

    public void handleOpponentTurn(Move move) {
        int number = move.getMove();
        int column = number % 8;
        int row = number / 8;

        Cell currentCell = cell[row][column];
        placeMove(currentCell.pos, whoseTurn, getOpponent());
        checkGameStatus();
    }

    public void setMultiplayer(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;
    }

    public void setMyTurn() {
        myTurn = true;

        if (isBot) {
            try {
                TimeUnit.MILLISECONDS.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            makeAImove();
            checkGameStatus();
        }

        myTurn = false;
    }

    public void resetGame() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell currentCell = cell[i][j];
                currentCell.setToken(' ');
                currentCell.getChildren().clear();
            }
        }

        setStartPositions();
        whoseTurn = 'B';

        this.gameClient.endReversi();
    }

    public class Cell extends Pane {
        private char token = ' ';
        private int pos;
        private int i;
        private int j;

        public Cell(int pos, int i, int j) {
            this.pos = pos;
            this.i = i;
            this.j = j;
            setStyle("-fx-border-color: black");
            this.setPrefSize(2000, 2000);
            this.setOnMouseClicked(e -> handleMouseClick());
        }

        public char getToken() {
            return token;
        }

        public void setToken(char c) {
            token = c;
        }

        public void drawAndsetToken(char c) {
            token = c;
            drawToken(c);
        }

        public void drawToken(char token) {
            if (token == 'W') {
                drawWhite();
            } else if (token == 'B') {
                drawBlack();
            }
        }

        /* Handle a mouse click event */
        private void handleMouseClick() {
            if (!isMultiplayer && token == ' ' && whoseTurn != ' ' && isValidMove(i, j)) {
                placeMove(pos, whoseTurn, getOpponent());
                checkGameStatus();
                this.setDisable(true);

                if (isBot) {
                    makeAImove();
                    checkGameStatus();
                }
            }

            else if (isMultiplayer && token == ' ' && whoseTurn != ' ' && myTurn) {
                placeMove(pos, whoseTurn, getOpponent());
                checkGameStatus();
                makeMove(pos);
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

    private void makeAImove() {
        Cell bestCell = evaluate();
        if (bestCell != null) {
            placeMove(bestCell.pos, whoseTurn, getOpponent());

            if (isMultiplayer) {
                makeMove(bestCell.pos);
            }
        }
    }

    private void makeMove(int pos) {
        this.serverService.makeMove(pos);
    }

    private void checkGameStatus() {
        if (getValidMoves().isEmpty()){
            getWinner();
            resetGame();

        } else {
            whoseTurn = (whoseTurn == 'B') ? 'W' : 'B';
        }
    }

    private void getWinner(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j<8; j++){
                if(cell[i][j].getToken() == 'W')
                    WScore++;
                else
                    BScore++;
            }
        }
        if(WScore > BScore)
            System.out.println("White is the winner!");
        else
            System.out.println("Black is the Winner!");

        WScore = 0;
        BScore = 0;
    }

    // doet een zet en keert daarbij de token van alle andere stenen om. gebaseerd op coördinaat.
    public void placeMove(int pos, char player, char opponent){
        int i = pos / 8,
            j = pos % 8;

        cell[i][j].drawAndsetToken(player);

        int I = i, J = j;

        if (i-1>=0 && j-1>=0 && cell[i-1][j-1].getToken() == opponent){
            i = i-1; j = j-1;
            while(i>0 && j>0 && cell[i][j].getToken() == opponent){i--;j--;}
            if(i>=0 && j>=0 && cell[i][j].getToken() == player) {
                while(i!=I-1 && j!=J-1)
                    cell[++i][++j].drawAndsetToken(player);
            }
        }

        i=I; j=J;

        if(i-1>=0 && cell[i-1][j].getToken() == opponent){
            i = i-1;
            while(i>0 && cell[i][j].getToken() == opponent) i--;
            if(i>=0 && cell[i][j].getToken() == player) {
                while(i!=I-1)
                    cell[++i][j].drawAndsetToken(player);
            }
        }

        i=I;

        if(i-1>=0 && j+1<=7 && cell[i-1][j+1].getToken() == opponent){
            i = i-1; j = j+1;
            while(i>0 && j<7 && cell[i][j].getToken() == opponent){
                i--;
                j++;
            }
            if(i>=0 && j<=7 && cell[i][j].getToken() == player) {
                while (i != I - 1 && j != J + 1)
                    cell[++i][--j].drawAndsetToken(player);
            }
        }
        i=I;j=J;
        if(j-1>=0 && cell[i][j-1].getToken() == opponent){
            j = j-1;
            while(j>0 && cell[i][j].getToken() == opponent)
                    j--;
            if(j>=0 && cell[i][j].getToken() == player) {while(j!=J-1)
                cell[i][++j].drawAndsetToken(player);
            }
        }
        j=J;
        if(j+1<=7 && cell[i][j+1].getToken() == opponent){
            j=j+1;
            while(j<7 && cell[i][j].getToken() == opponent)
                j++;
            if(j<=7 && cell[i][j].getToken() == player) {
                while(j!=J+1)cell[i][--j].drawAndsetToken(player);
            }
        }
        j=J;
        if(i+1<=7 && j-1>=0 && cell[i+1][j-1].getToken() == opponent){
            i=i+1;j=j-1;
            while(i<7 && j>0 && cell[i][j].getToken() == opponent){
                i++;
                j--;
            }
            if(i<=7 && j>=0 && cell[i][j].getToken() == player) {while(i!=I+1 && j!=J-1)
                cell[--i][++j].drawAndsetToken(player);
            }
        }
        i=I;j=J;
        if(i+1 <= 7 && cell[i+1][j].getToken() == opponent){
            i=i+1;
            while(i<7 && cell[i][j].getToken() == opponent)
                i++;
            if(i<=7 && cell[i][j].getToken() == player) {
                while(i!=I+1)
                    cell[--i][j].drawAndsetToken(player);
            }
        }
        i=I;

        if(i+1 <= 7 && j+1 <=7 && cell[i+1][j+1].getToken() == opponent){
            i=i+1;j=j+1;
            while(i<7 && j<7 && cell[i][j].getToken() == opponent){i++;j++;}
            if(i<=7 && j<=7 && cell[i][j].getToken() == player)
                while(i!=I+1 && j!=J+1)
                    cell[--i][--j].drawAndsetToken(player);
        }
    }

    private char getOpponent() {
        return (whoseTurn == 'W') ? 'B' : 'W';
    }

    private ArrayList<Cell> getValidMoves() {
        ArrayList<Cell> validMoves = new ArrayList<>();

        for(int i = 0; i < cell.length; i++) {
            for(int j = 0; j < cell[0].length; j++) {
                Cell currentCell = cell[i][j];

                //if this spot is empty check to see if its a valid move spot, if it is, return true
                if(currentCell.getToken() == ' ' && isValidMove(i, j)) {
                    validMoves.add(currentCell);
                }
            }
        }

        return validMoves;
    }

    private Cell evaluate() {
        int bestScore = -10000;
        Cell bestCell = null;

        int[][] scoreBoard = {
                {1000, -100,  150,  100,  100,  150, -100, 1000},
                {-100, -200,   20,   20,   20,   20, -200, -100},
                { 150,   20,   15,   15,   15,   15,   20,  150},
                { 100,   20,   15,   10,   10,   15,   20,  100},
                { 100,   20,   15,   10,   10,   15,   20,  100},
                { 150,   20,   15,   15,   15,   15,   20,  150},
                {-100, -200,   20,   20,   20,   20, -200, -100},
                {1000, -100,  150,  100,  100,  150, -100, 1000}};

        ArrayList<Cell> validMoves = getValidMoves();

        for (Cell move: validMoves) {
            if (bestScore < scoreBoard[move.i][move.j]) {
                bestScore = scoreBoard[move.i][move.j];
                bestCell = move;
            }
        }

        return bestCell;
    }

    private boolean isValidMove(int x, int y) {
        return
            checkDirection(x, y, -1, 0) |  //left
            checkDirection(x, y, -1, -1) | //diagonal up-left
            checkDirection(x, y, 0, -1) |  //up
            checkDirection(x, y, 1, -1) |  //diagonal up-right
            checkDirection(x, y, 1, 0) |   //right
            checkDirection(x, y, 1, 1) |   //diagonal down-right
            checkDirection(x, y, 0, 1) |   //down
            checkDirection(x, y, -1, 1);   //diagonal down-left
    }

    private boolean checkDirection(int x, int y, int xDirection, int yDirection) {
        int currentX = x;
        int currentY = y;
        currentX += xDirection;
        currentY += yDirection;

        while(currentX >= 0 && currentY >= 0 && currentX < cell.length && currentY < cell[0].length) {
            Cell currentCell = cell[currentX][currentY];
            if(isWithinOneSpace(x, y, currentX, currentY)) {
                if(currentCell.getToken() == ' ' || currentCell.getToken() == whoseTurn) return false;
            } else {
                if (currentCell.getToken() == ' ') return false;
                if (currentCell.getToken() == whoseTurn) return true;
            }

            currentX += xDirection;
            currentY += yDirection;
        }

        return false;
    }

    private boolean isWithinOneSpace(int disc1X, int disc1Y, int disc2X, int disc2Y) {
        if((disc1X == disc2X ||disc1X == disc2X - 1 || disc1X == disc2X + 1) &&
                (disc1Y == disc2Y ||disc1Y == disc2Y - 1 ||disc1Y == disc2Y + 1)) {

            return true;
        }

        return false;
    }
}
