package main;

import controller.ClientController;
import controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import model.Client;
import view.ClientView;
import view.LoginView;

public class GameClient extends Application {
    private LoginView loginView;
    private LoginController loginController;

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
        this.loginController = new LoginController();
        this.loginView = new LoginView(primaryStage, this.loginController);
        this.loginController.setGameClient(this);

        this.client = new Client();
        this.clientController = new ClientController(client);
        this.clientView = new ClientView(primaryStage, this.clientController);

        this.loginView.show();

        this.client.setPlayers(clientController.retrievePlayersFromServer());
        this.client.setGames(clientController.retrieveGameListFromServer());
    }

    public void login(String text) {
        this.loginView.hide();
        this.client.setUserName(text);
        this.clientView.show();
    }
}
