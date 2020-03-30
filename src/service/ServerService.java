package service;

import main.GameClient;

import java.util.ArrayList;

/**
 * Class ServerService contains all the communication methods for the server.
 *
 * @author Wouter Folkertsma
 */
public class ServerService {
    private GameClient gameClient;

    public ServerService(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    /**
     * @return ArrayList
     */
    public ArrayList<String> retrievePlayers() {
        ArrayList<String> list = new ArrayList<String>();

        list.add("Wouter");
        list.add("Wouter1");
        list.add("Wouter2");
        list.add("Wouter3");

        return list;
    }


    /**
     * @return ArrayList
     */
    public ArrayList<String> retrieveGameList() {
        ArrayList<String> list = new ArrayList<String>();

        list.add("TestGame1");
        list.add("TestGame2");

        return list;
    }
}
