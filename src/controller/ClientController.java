package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import main.GameClient;
import model.Challenger;
import model.Client;
import model.Game;
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
    private boolean isMultiPlayer = true;
    private String currentGame;
    private String currentPlayer;

    @FXML
    private ListView<String> playerList;

    @FXML
    private ListView<String> gamesList;

    @FXML
    private Button challengeButton;

    @FXML
    private Button singleVsBot;

    @FXML
    private Button singleVsSelf;

    @FXML
    private Button singlePlayerButton;

    @FXML
    private Button multiPlayerButton;

    @FXML
    private CheckBox isBot;

    /**
     * @param client Client
     */
    public ClientController(Client client, ServerService serverService, GameClient gameClient) {
        this.client = client;
        this.serverService = serverService;
        this.gameClient = gameClient;
    }

    public void handleCheckboxAction(ActionEvent actionEvent) {
        this.serverService.setIsBot(isBot.isSelected());
    }

    @SuppressWarnings("unused")
    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        Window owner = challengeButton.getScene().getWindow();
        currentGame = this.gamesList.getSelectionModel().getSelectedItem();

        Game game = new Game();
        game.setGameType(currentGame);

        if (singleVsBot == actionEvent.getTarget()) {
            this.gameClient.startGame(game, false, true);
        }
        else if (singleVsSelf == actionEvent.getTarget()){
            this.gameClient.startGame(game, false, false);
        }
        else if (challengeButton == actionEvent.getTarget()){
            currentPlayer = this.playerList.getSelectionModel().getSelectedItem();
            this.serverService.challengePlayer(currentPlayer, currentGame);
        }
    }

    public void handleTabAction(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getTarget();

        if (button == multiPlayerButton) {
            this.enableMultiplayer(true);
        } else if (button == singlePlayerButton) {
            this.enableMultiplayer(false);
        }
    }

    public void login() {
        this.serverService.login(this.client.getUserName());
        this.serverService.getPlayerList();
        this.serverService.getGamesList();

        this.enableMultiplayer(false);
    }

    private void enableMultiplayer(boolean enable) {
        this.isMultiPlayer = enable;
        this.playerList.setVisible(enable);
        this.playerList.setManaged(enable);
        this.singleVsBot.setVisible(!enable);
        this.singleVsBot.setManaged(!enable);
        this.singleVsSelf.setVisible(!enable);
        this.singleVsSelf.setManaged(!enable);
        this.challengeButton.setVisible(enable);
        this.challengeButton.setManaged(enable);
        this.isBot.setVisible(enable);
        this.isBot.setManaged(enable);
    }

    public void setClientView(ClientView clientView) {
         this.clientView = clientView;
    }

    public void incomingChallenge(Challenger challenger) {
        String message = challenger.getChallenger() + " has challenged you to play " + challenger.getGameType() + ". Do you accept?";
        Alert alert = this.showPrompt(challengeButton.getScene().getWindow(), message, "INCOMING");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                this.serverService.acceptChallenge(challenger);
            }
        });
    }

    private Alert showPrompt(Window window, String message, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(window);

        return alert;
    }

    public void setPlayerList(ArrayList<String> playerList) {
        this.client.setPlayers(playerList);
        this.client.getPlayers().forEach((player) -> this.playerList.getItems().add(player));
    }

    public void setGamesList(ArrayList<String> gamesList) {
        this.client.setGames(gamesList);
        this.client.getGames().forEach((game) -> this.gamesList.getItems().add(game));
    }
}
