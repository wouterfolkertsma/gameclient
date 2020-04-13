package controller;

import main.GameClient;

public abstract class AbstractController {
    GameClient gameClient;
    boolean isMultiplayer;
    protected boolean isBot;

    public GameClient setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
        return this.gameClient;
    }

    public GameClient getGameClient(GameClient gameClient) {
        return this.gameClient;
    }

    public void setGameState(boolean isMultiplayer, boolean isBot) {
        this.isMultiplayer = isMultiplayer;
        this.isBot = isBot;
    }
}
