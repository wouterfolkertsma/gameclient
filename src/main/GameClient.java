package main;

import controller.ClientController;
import controller.LoginController;
import controller.TicTacToeController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.Challenger;
import model.Client;
import service.ServerService;
import view.ClientView;
import view.LoginView;
import view.TicTacToeView;

public class GameClient extends Application {
    private ServerService serverService;
    private LoginView loginView;
    private LoginController loginController;

    private TicTacToeView ticTacToeView;
    private TicTacToeController ticTacToeController;

    private Client client;
    private ClientView clientView;
    private ClientController clientController;

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

        this.ticTacToeController = new TicTacToeController();
        this.ticTacToeView = new TicTacToeView(primaryStage, this.ticTacToeController);

        this.client = new Client();
        this.clientController = new ClientController(client, serverService, this);
        this.clientView = new ClientView(primaryStage, this.clientController);
        this.clientController.setClientView(clientView);

//        this.ticTacToeView.show();
        this.loginView.show();
    }

    public void login(String text) {
        this.loginView.hide();
        this.client.setUserName(text);
        this.clientController.login();
        this.clientView.show();
    }

    public void incomingChallenge(Challenger challenger) {
        this.clientController.incomingChallenge(challenger);
    }
}
