package model;

import java.util.ArrayList;

public class Client {
    private ArrayList games = new ArrayList<>();
    private ArrayList players = new ArrayList<>();
    private String userName;

    /**
     * @param games ArrayList
     * @return ArrayList
     */
    public ArrayList setGames(ArrayList games) {
        this.games = games;
        return this.games;
    }

    /**
     * @return ArrayList
     */
    public ArrayList getGames() {
        return this.games;
    }

    /**
     * @param players ArrayList
     * @return ArrayList
     */
    public ArrayList setPlayers(ArrayList players) {
        this.players = players;
        return this.players;
    }

    /**
     * @return ArrayList
     */
    public ArrayList getPlayers() {
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
