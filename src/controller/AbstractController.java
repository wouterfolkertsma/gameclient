package controller;

import main.GameClient;

public abstract class AbstractController {
    protected GameClient gameClient;

    public GameClient setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
        return this.gameClient;
    }

    public GameClient getGameClient(GameClient gameClient) {
        return this.gameClient;
    }
}
