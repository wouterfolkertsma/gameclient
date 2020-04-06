package model;

/**
 * Class State which stores all the data for the client.
 *
 * @author Wouter Folkertsma
 */
public class Game extends AbstractModel {
    private String playerToMove;
    private String opponent;
    private String gameType;

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getPlayerToMove() {
        return playerToMove;
    }

    public void setPlayerToMove(String playerToMove) {
        this.playerToMove = playerToMove;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }
}
