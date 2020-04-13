package controller;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import model.Move;
import service.ServerService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// EEN AANTAL METHODES MOETEN AANGEPAST WORDEN AAN DE REGELS VAN REVERSI
public class ReversiController extends AbstractController{
    private char whoseTurn = 'W';
    private Cell[][] cell = new Cell[8][8];
    private HashMap<Integer, Integer> validLocations;

    private ServerService serverService;
    private Boolean myTurn = false;
    private int WScore;
    private int BScore;
    private int remaining;

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

    public class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + "]";
        }

        @Override
        public boolean equals(Object o) {
            return o.hashCode() == this.hashCode();
        }

        @Override
        public int hashCode() {
            return Integer.parseInt(x + "" + y);
        }
    }

    public int gameResult(Set<Point> whitePlaceableLocations, Set<Point> blackPlaceableLocations){
        updateScores();
        if(remaining == 0){
            if(WScore > BScore) return 1;
            else if(BScore > WScore) return -1;
            else return 0; //Draw
        }
        if(WScore==0 || BScore == 0){
            if(WScore > 0) return 1;
            else if(BScore > 0) return -1;
        }
        if(whitePlaceableLocations.isEmpty() && blackPlaceableLocations.isEmpty()){
            if(WScore > BScore) return 1;
            else if(BScore > WScore) return -1;
            else return 0; //Draw
        }
        return -2;
    }

    private void makeMove(int pos) {
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

    public boolean cellIsFull() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (cell[i][j].getToken() == ' ')
                    return false;

        return true;
    }

        private void findPlaceableLocations(char player, char opponent, HashSet<Point> placeablePositions){
            for(int i=0;i<8;++i) {
                for(int j=0;j<8;++j) {
                    if(cell[i][j].getToken() == opponent) {
                        int I = i, J = j;
                        if(i-1>=0 && j-1>=0 && cell[i-1][j-1].getToken() == ' ') {
                            i = i+1;
                            j = j+1;
                            while(i<7 && j<7 && cell[i][j].getToken() == opponent) {
                                i++;
                                j++;
                            }
                            if (i <= 7 && j <= 7 && cell[i][j].getToken() == player) {
                                placeablePositions.add(new Point(I-1, J-1));
                            }
                        }
                        i=I;j=J;
                        if(i-1>=0 && cell[i-1][j].getToken() == ' '){
                            i = i+1;
                            while(i<7 && cell[i][j].getToken() == opponent) i++;
                            if(i<=7 && cell[i][j].getToken() == player) placeablePositions.add(new Point(I-1, J));
                        }
                        i=I;
                        if(i-1>=0 && j+1<=7 && cell[i-1][j+1].getToken() == ' '){
                            i = i+1; j = j-1;
                            while(i<7 && j>0 && cell[i][j].getToken() == opponent){i++;j--;}
                            if(i<=7 && j>=0 && cell[i][j].getToken() == player) placeablePositions.add(new Point(I-1, J+1));
                        }
                        i=I;j=J;
                        if(j-1>=0 && cell[i][j-1].getToken() == ' '){
                            j = j+1;
                            while(j<7 && cell[i][j].getToken() == opponent)j++;
                            if(j<=7 && cell[i][j].getToken() == player) placeablePositions.add(new Point(I, J-1));
                        }
                        j=J;
                        if(j+1<=7 && cell[i][j+1].getToken() == ' '){
                            j=j-1;
                            while(j>0 && cell[i][j].getToken() == opponent)j--;
                            if(j>=0 && cell[i][j].getToken() == player) placeablePositions.add(new Point(I, J+1));
                        }
                        j=J;
                        if(i+1<=7 && j-1>=0 && cell[i+1][j-1].getToken() == ' '){
                            i=i-1;j=j+1;
                            while(i>0 && j<7 && cell[i][j].getToken() == opponent){i--;j++;}
                            if(i>=0 && j<=7 && cell[i][j].getToken() == player) placeablePositions.add(new Point(I+1, J-1));
                        }
                        i=I;j=J;
                        if(i+1 <= 7 && cell[i+1][j].getToken() == ' '){
                            i=i-1;
                            while(i>0 && cell[i][j].getToken() == opponent) i--;
                            if(i>=0 && cell[i][j].getToken() == player) placeablePositions.add(new Point(I+1, J));
                        }
                        i=I;
                        if(i+1 <= 7 && j+1 <=7 && cell[i+1][j+1].getToken() == ' '){
                            i=i-1;j=j-1;
                            while(i>0 && j>0 && cell[i][j].getToken() == opponent){i--;j--;}
                            if(i>=0 && j>=0 && cell[i][j].getToken() == player)placeablePositions.add(new Point(I+1, J+1));
                        }
                        i=I;j=J;
                    }
                }
            }
        }


        public void placeMove(Point p, char player, char opponent){
            int i = p.x, j = p.y;
            cell[i][j].setToken(player);
            int I = i, J = j;

            if(i-1>=0 && j-1>=0 && cell[i-1][j-1].getToken() == opponent){
                i = i-1; j = j-1;
                while(i>0 && j>0 && cell[i][j].getToken() == opponent){i--;j--;}
                if(i>=0 && j>=0 && cell[i][j].getToken() == player) {while(i!=I-1 && j!=J-1)cell[++i][++j].setToken(player);}
            }
            i=I;j=J;
            if(i-1>=0 && cell[i-1][j].getToken() == opponent){
                i = i-1;
                while(i>0 && cell[i][j].getToken() == opponent) i--;
                if(i>=0 && cell[i][j].getToken() == player) {while(i!=I-1)cell[++i][j].setToken(player);}
            }
            i=I;
            if(i-1>=0 && j+1<=7 && cell[i-1][j+1].getToken() == opponent){
                i = i-1; j = j+1;
                while(i>0 && j<7 && cell[i][j].getToken() == opponent){i--;j++;}
                if(i>=0 && j<=7 && cell[i][j].getToken() == player) {while(i!=I-1 && j!=J+1)cell[++i][--j].setToken(player);;}
            }
            i=I;j=J;
            if(j-1>=0 && cell[i][j-1].getToken() == opponent){
                j = j-1;
                while(j>0 && cell[i][j].getToken() == opponent)j--;
                if(j>=0 && cell[i][j].getToken() == player) {while(j!=J-1)cell[i][++j].setToken(player);;}
            }
            j=J;
            if(j+1<=7 && cell[i][j+1].getToken() == opponent){
                j=j+1;
                while(j<7 && cell[i][j].getToken() == opponent)j++;
                if(j<=7 && cell[i][j].getToken() == player) {while(j!=J+1)cell[i][--j].setToken(player);;}
            }
            j=J;
            if(i+1<=7 && j-1>=0 && cell[i+1][j-1].getToken() == opponent){
                i=i+1;j=j-1;
                while(i<7 && j>0 && cell[i][j].getToken() == opponent){i++;j--;}
                if(i<=7 && j>=0 && cell[i][j].getToken() == player) {while(i!=I+1 && j!=J-1)cell[--i][++j].setToken(player); }
            }
            i=I;j=J;
            if(i+1 <= 7 && cell[i+1][j].getToken() == opponent){
                i=i+1;
                while(i<7 && cell[i][j].getToken() == opponent) i++;
                if(i<=7 && cell[i][j].getToken() == player) {while(i!=I+1)cell[--i][j].setToken(player);;}
            }
            i=I;

            if(i+1 <= 7 && j+1 <=7 && cell[i+1][j+1].getToken() == opponent){
                i=i+1;j=j+1;
                while(i<7 && j<7 && cell[i][j].getToken() == opponent){i++;j++;}
                if(i<=7 && j<=7 && cell[i][j].getToken() == player)while(i!=I+1 && j!=J+1)cell[--i][--j].setToken(player);}
        }
    }

    public void updateScores() {
        WScore = 0; BScore = 0; remaining = 0;
        for(int i=0;i<8;++i){
            for(int j=0;j<8;++j){
                if(board[i][j]=='W')WScore++;
                else if(board[i][j]=='B')BScore++;
                else remaining++;
            }
        }
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

        if (cellIsFull())
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
