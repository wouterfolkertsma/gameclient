package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Window;
import main.GameClient;
import model.Challenger;
import model.Client;
import model.Game;
import model.GameType;
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
    private boolean isMultiPlayer = false;
    private String currentGame;
    private String currentPlayer;


    @FXML
    private ListView<String> playerList;

    @FXML
    private ListView<String> gamesList;

    @FXML
    private Button challengeButton;

    @FXML
    private Button startSinglePlayerButton;

    @FXML
    private Button singlePlayerButton;

    @FXML
    private Button multiPlayerButton;

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
        currentGame = this.gamesList.getSelectionModel().getSelectedItem();

        if (startSinglePlayerButton == actionEvent.getTarget()) {
            Game game = new Game();
            game.setGameType(currentGame);
            this.gameClient.startGame(game, false);
        } else if (challengeButton == actionEvent.getTarget()){
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
        this.client.setPlayers(this.serverService.getPlayerList());
        this.client.setGames(this.serverService.getGamesList());

        this.enableMultiplayer(false);

        this.client.getGames().forEach((game) -> this.gamesList.getItems().add(game));
        this.client.getPlayers().forEach((player) -> this.playerList.getItems().add(player));
    }

    private void enableMultiplayer(boolean enable) {
        this.isMultiPlayer = enable;
        this.playerList.setVisible(enable);
        this.playerList.setManaged(enable);
        this.startSinglePlayerButton.setVisible(!enable);
        this.startSinglePlayerButton.setManaged(!enable);
        this.challengeButton.setVisible(enable);
        this.challengeButton.setManaged(enable);
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
}
