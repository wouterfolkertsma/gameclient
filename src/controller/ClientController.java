package controller;

import model.Client;

import java.util.ArrayList;

public class ClientController extends AbstractController {
    private Client client;

    /**
     * @param client Client
     */
    public ClientController(Client client) {
        this.client = client;
    }

    /**
     * @return ArrayList
     */
    public ArrayList retrievePlayersFromServer() {
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
    public ArrayList retrieveGameListFromServer() {
        ArrayList<String> list = new ArrayList<String>();

        list.add("TestGame1");
        list.add("TestGame2");

        return list;
    }

    /**
     * @return ArrayList
     */
    public ArrayList getPlayerList() {
        return this.client.getPlayers();
    }
}
