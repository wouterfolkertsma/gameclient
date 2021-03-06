package service;

import main.GameClient;
import model.Challenger;
import model.Game;
import model.Move;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * Class ServerService contains all the communication methods for the server.
 *
 * @author Wouter Folkertsma, Daniël Windstra
 */
public class ServerService {

    private ServerListenerService serverListener;
    private GameClient gameClient;
    private BufferedWriter bufferedWriter;
    private Socket socket = null;
    private Scanner scanner;
    private LinkedList<String> queue;
    private boolean isBot = true;

    private static String IP_ADDRESS;
    private static int PORT;

    public ServerService(GameClient gameClient) {
        this.gameClient = gameClient;
        this.queue = new LinkedList<>();
    }

    public void establishConnection(String IP_ADDRESS, int PORT) {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            scanner = new Scanner(socket.getInputStream());

            Timer timer = new Timer();
            timer.schedule(new KeepAlive(this), 0, 5000);

            this.serverListener = new ServerListenerService(this, scanner, queue);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void setIPAdress(String address){
        IP_ADDRESS = address;
    }

    public void setPort(int port){
        PORT = port;
    }

    public String getIpAddress(){
        return IP_ADDRESS;
    }

    public int getPORT(){
        return PORT;
    }

    public void setIsBot(boolean isBot) {
        this.isBot = isBot;
    }

    /**
     * @return String
     */
    private ArrayList<String> readResponse() {
        ArrayList<String> responses = new ArrayList<>();
        try {
            while (this.queue.size() > 0) {
                String newLine = this.queue.poll();
                responses.add(newLine);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return responses;
    }

    private void writeLine(String line) {
        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception exception) {
           //
        }
    }

    private ArrayList<String> writeLineAndRead(String line) {
        serverListener.mayRead = false;

        ArrayList<String> response = new ArrayList<>();

        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            response = this.readResponse();
        } catch (Exception exception) {
            response.add(exception.getMessage());
        }

        serverListener.mayRead = true;

        return response;
    }

    void handleResponse(String newLine) {
        if (newLine.contains("SVR GAME YOURTURN")) {
            this.yourTurn(newLine);
        } else if (newLine.contains("SVR GAME CHALLENGE CANCELLED")) {

        } else if (newLine.contains("SVR GAME CHALLENGE")) {
            this.incomingChallenge(newLine);
        } else if (newLine.contains("SVR GAME MATCH")) {
            this.newMatch(newLine);
        } else if (newLine.contains("SVR GAME MOVE")) {
            this.handleMove(newLine);
        } else if (newLine.contains("SVR PLAYERLIST")) {
            this.gameClient.setPlayerList(getLastArgument(newLine));
        } else if (newLine.contains("SVR GAMELIST")) {
            this.gameClient.setGameList(getLastArgument(newLine));
        } else if (newLine.contains("SVR GAME WIN") || newLine.contains("SVR GAME LOSS")) {
            this.gameClient.resetGame();
        }
    }

    private void yourTurn(String newLine) {
        this.gameClient.yourTurn();
    }

    private void handleMove(String newLine) {
        ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(newLine.split("\\{")[1].split("}")[0].split(", ")));
        Move move = new Move();

        for (String argument : arguments) {
            String key = argument.split(": ")[0];
            String value = argument.split(": ")[1].substring(1, argument.split(": ")[1].length() - 1);

            switch (key) {
                case "PLAYER": move.setPlayer(value); break;
                case "MOVE": move.setMove(parseInt(value)); break;
                case "DETAILS": move.setDetails(value); break;
            }
        }

        this.gameClient.handleMove(move);
    }

    private void newMatch(String newLine) {
        ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(newLine.split("\\{")[1].split("}")[0].split(", ")));
        Game game = new Game();

        for (String argument : arguments) {
            String key = argument.split(": ")[0];
            String value = argument.split(": ")[1].substring(1, argument.split(": ")[1].length() - 1);

            switch (key) {
                case "PLAYERTOMOVE": game.setPlayerToMove(value); break;
                case "GAMETYPE": game.setGameType(value); break;
                case "OPPONENT": game.setOpponent(value); break;
            }
        }

        this.gameClient.startGame(game, true, isBot);
    }

    private void incomingChallenge(String newLine) {
        ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(newLine.split("\\{")[1].split("}")[0].split(", ")));
        Challenger challenger = new Challenger();

        for (String argument : arguments) {
            String key = argument.split(": ")[0];
            String value = argument.split(": ")[1].substring(1, argument.split(": ")[1].length() - 1);

            switch (key) {
                case "CHALLENGER": challenger.setChallenger(value);
                case "CHALLENGENUMBER": challenger.setChallengeNumber(value);
                case "GAMETYPE": challenger.setGameType(value);
            }
        }

        this.gameClient.incomingChallenge(challenger);
    }

    public void challengePlayer(String player, String game){
        writeLine("challenge \"" + player + "\" \"" + game + "\"");
    }

    public void acceptChallenge(Challenger challenger) {
        writeLine("challenge accept " + challenger.getChallengeNumber());
    }

    private ArrayList<String> getLastArgument(String line) {
        ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(line.split("\\[")[1].split("]")));
        ArrayList<String> response = new ArrayList<>();

        if (arguments.size() < 1) {
            return response;
        }

        String[] list = arguments.get(0).split(", ");

        for (String argument : list) {
            response.add(argument.substring(1, argument.length() - 1));
        }

        return response;
    }

    public void getPlayerList() {
        writeLine("get playerlist");
    }

    public void getGamesList() {
        writeLine("get gamelist");
    }

    public void login(String userName) {
        writeLine("login " + userName);
    }

    public void makeMove(int position){
        writeLine("move " + position);
    }
}
