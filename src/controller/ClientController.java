package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Window;
import main.GameClient;
import model.Client;
import service.ServerService;
import view.ClientView;

import java.util.ArrayList;

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

    @FXML
    private ListView<String> gamesList;

    @FXML
    private Button challengeButton;

    /**
     * @param client Client
     */
    public ClientController(Client client, ServerService serverService, GameClient gameClient) {
        this.client = client;
        this.serverService = serverService;
        this.gameClient = gameClient;
    }


    @SuppressWarnings("unused")
    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        Window owner = challengeButton.getScene().getWindow();
        String currentGame = this.gamesList.getSelectionModel().getSelectedItem();
        String currentPlayer = this.playerList.getSelectionModel().getSelectedItem();

        this.serverService.challengePlayer(currentPlayer, currentGame);
        System.out.println("Challenging: " + currentPlayer + " for " + currentGame);
    }

    public void login() {
        this.client.setPlayers(this.serverService.getPlayerList());
        this.client.setGames(this.serverService.getGamesList());
        this.client.getGames().forEach((game) -> this.gamesList.getItems().add(game));
        this.client.getPlayers().forEach((player) -> this.playerList.getItems().add(player));
    }

    public void setClientView(ClientView clientView) {
         this.clientView = clientView;
    }
}
