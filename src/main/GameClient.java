package main;

import controller.ClientController;
import controller.LoginController;
import controller.ReversiController;
import controller.TicTacToeController;
import javafx.application.Application;
import javafx.stage.Stage;
import model.*;
import service.ServerService;
import view.ClientView;
import view.LoginView;
import view.ReversiView;
import view.TicTacToeView;

public class GameClient extends Application {
    private ServerService serverService;
    private LoginView loginView;
    private LoginController loginController;

    private TicTacToeView ticTacToeView;
    private TicTacToeController ticTacToeController;

    private ReversiView reversiView;
    private ReversiController reversiController;

    private Client client;
    private ClientView clientView;
    private ClientController clientController;

    private String currentGame;
    private String[] address;


    /**
     * @param args String[]
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param primaryStage Stage
     */
    public void start(Stage primaryStage) throws Exception {
        this.serverService = new ServerService(this);

        this.loginController = new LoginController(serverService);
        this.loginView = new LoginView(primaryStage, this.loginController);
        this.loginController.setGameClient(this);

        this.ticTacToeController = new TicTacToeController(serverService, this);
        this.ticTacToeView = new TicTacToeView(primaryStage, this.ticTacToeController);

        this.reversiController = new ReversiController(serverService);
        this.reversiView = new ReversiView(primaryStage, this.reversiController);

        this.client = new Client();
        this.clientController = new ClientController(client, serverService, this);

        this.clientView = new ClientView(primaryStage, this.clientController);
        this.clientController.setClientView(clientView);

        this.loginView.show();
    }

    public void login(String text, String address) {
        this.address = getIPAddressAndPort(address);

        this.serverService.establishConnection(this.address[0], Integer.parseInt(this.address[1]));
        this.loginView.hide();

        this.client.setUserName(text);
        this.client.setAddress(address);
        this.clientController.login();

        this.clientView.show();
    }

    public void incomingChallenge(Challenger challenger) {
        this.clientController.incomingChallenge(challenger);
    }

    public void startGame(Game game, boolean isMultiplayer) {
        switch (game.getGameType()) {
            case GameType.TIC_TAC_TOE:
                this.currentGame = GameType.TIC_TAC_TOE;
                this.startTicTacToe(isMultiplayer);
                break;
            case GameType.REVERSI:
                this.currentGame = GameType.REVERSI;
                this.startReversi(isMultiplayer);
                break;
        }
    }

    public void startTicTacToe(boolean isMultiplayer) {
        this.clientView.hide();
        this.ticTacToeView.show();
        this.ticTacToeController.setMultiplayer(isMultiplayer);
    }

    public void startReversi(boolean isMultiplayer) {
        this.clientView.hide();
        this.reversiView.show();
        this.reversiController.setMultiplayer(isMultiplayer);
    }

    public void handleMove(Move move) {
        if (this.currentGame.equals(GameType.TIC_TAC_TOE))
            this.ticTacToeController.handleOpponentTurn(move);
        if(this.currentGame.equals(GameType.REVERSI))
            this.reversiController.handleOpponentTurn(move);
    }

    public void yourTurn() {
        if (this.currentGame.equals(GameType.TIC_TAC_TOE))
            this.ticTacToeController.setMyTurn();
        if (this.currentGame.equals(GameType.REVERSI))
            this.reversiController.setMyTurn();
    }

    public String[] getIPAddressAndPort(String address) {
        String[] addressArray;
        String stripped = address.replace(" ", "");
        addressArray = stripped.split(",");

        return addressArray;
    }

    public void endTicTactToe() {
        this.ticTacToeView.hide();
        this.clientView.show();
    }
}
