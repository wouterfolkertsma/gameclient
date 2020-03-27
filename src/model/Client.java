package model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class GameClient which stores all the data for the client.
 *
 * @author Wouter Folkertsma
 */
public class Client {
    private ArrayList<String> games = new ArrayList<>();
    private ArrayList<String> players = new ArrayList<>();
    private String userName;

    /**
     * @param games ArrayList
     * @return ArrayList
     */
    public ArrayList<String> setGames(ArrayList<String> games) {
        this.games = games;
        return this.games;
    }

    /**
     * @return ArrayList
     */
    public ArrayList<String> getGames() {
        return this.games;
    }

    /**
     * @param players ArrayList
     * @return ArrayList
     */
    public ArrayList<String> setPlayers(ArrayList<String> players) {
        this.players = players;
        return this.players;
    }

    /**
     * @return ArrayList
     */
    public ArrayList<String> getPlayers() {
        return this.players;
    }

    public String setUserName(String userName) {
        this.userName = userName;
        return userName;
    }

    public String getUserName() {
        return this.userName;
    }
}
