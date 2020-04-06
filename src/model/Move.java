package model;

/**
 * Class State which stores all the data for the client.
 *
 * @author Wouter Folkertsma
 */
public class Move extends AbstractModel {
    private String player;
    private int move;
    private String details;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
