package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import main.GameClient;
import model.Client;
import service.ServerService;
import view.ClientView;

/**
 * Class ClientController handles all events for the client page.
 *
 * @author Wouter Folkertsma
 */
public class ClientController extends AbstractController {
    private Client client;
    private ServerService serverService;
    private ClientView clientView;

    @FXML
    private ListView<String> playerList;

    /**
     * @param client Client
     */
    public ClientController(Client client, ServerService serverService, GameClient gameClient) {
        this.client = client;
        this.serverService = serverService;
        this.gameClient = gameClient;
    }

    public void login() {
        this.client.setPlayers(this.serverService.getPlayerList());
        this.client.getPlayers().forEach((player) -> this.playerList.getItems().add(player));
    }

    public void setClientView(ClientView clientView) {
         this.clientView = clientView;
    }
}
