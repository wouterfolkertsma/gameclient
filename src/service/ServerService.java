package service;

import main.GameClient;
import model.Challenger;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Class ServerService contains all the communication methods for the server.
 *
 * @author Wouter Folkertsma, Daniël Windstra, Anthonie Ooms
 */
public class ServerService {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 7789;

    private ServerListenerService serverListener;
    private GameClient gameClient;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private Socket socket = null;
    private Scanner scanner;
    private boolean mayRead = true;
    private ArrayList<String> responses = new ArrayList<>();
    private LinkedList<String> queue;

    public ServerService(GameClient gameClient) {
        this.gameClient = gameClient;
        this.queue = new LinkedList<>();

        try {
            socket = new Socket(IP_ADDRESS, PORT);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(socket.getInputStream());

            this.serverListener = new ServerListenerService(this, socket, scanner, queue);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * @return String
     */
    private ArrayList<String> readResponse() {
        ArrayList<String> responses = new ArrayList<>();
        try {
            TimeUnit.MILLISECONDS.sleep(50);

            while (this.queue.size() > 0) {
                String newLine = this.queue.poll();
                responses.add(newLine);
                System.out.println(newLine);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return responses;
    }

    private ArrayList<String> writeLine(String line) {
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
            this.handleOpponentTurn(newLine);
        } else if (newLine.contains("SVR GAME CHALLENGE CANCELLED")) {

        } else if (newLine.contains("SVR GAME CHALLENGE")) {
            this.incomingChallenge(newLine);
        }
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

    private void handleOpponentTurn(String line) {

    }

    public ArrayList<String> login(String userName) {
        return writeLine("login " + userName);
    }

    public void exit() {

    }

    public boolean getServerState() {
        boolean isReady;

        try {
            isReady = bufferedReader.ready();
        } catch (Exception e){
            return false;
        }

        return isReady;
    }

    public void retrievePlayers(){
        writeLine("get playerlist");
    }

    public void retrieveGameList(){
        writeLine("get gamelist");
    }

    /**
     * @param s
     * @return
     */
    public void subscribe(String s){
        writeLine("subscribe " + s);
    }

    public void matchStart(){
        HashMap<String, String> map = new HashMap<>();
    }

    public void playerTurn(){
    }

    public void makeMove(String s){
        writeLine("move " + s);
    }

    public void forfeit(){
        writeLine("forfeit");
    }

    public void receiveResult(){
    }

    public void challengePlayer(String player, String game){
        ArrayList<String> response = writeLine("challenge \"" + player + "\" \"" + game + "\"");

        for (String line : response) {
            System.out.println("NEW RESPONSE " + line);
        }
    }

    public void acceptChallenge(Challenger challenger) {
        writeLine("challenge accept " + challenger.getChallengeNumber());
    }

    public void getHelp() {
        writeLine("help");
    }

    public ArrayList<String> getPlayerList() {
        ArrayList<String> responseArray = writeLine("get playerlist");

        for (String response : responseArray) {
            if (response.contains("SVR PLAYERLIST")) {
                responseArray = getLastArgument(response);
            }
        }

        return responseArray;
    }

    private ArrayList<String> getLastArgument(String line) {
        ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(line.split("\\[")[1].split("]")[0].split(", ")));
        ArrayList<String> response = new ArrayList<>();

        for (String argument : arguments) {
            response.add(argument.substring(1, argument.length() - 1));
        }

        return response;
    }

    public ArrayList<String> getGamesList() {
        ArrayList<String> responseArray = writeLine("get gamelist");

        for (String response : responseArray) {
            if (response.contains("SVR GAMELIST")) {
                responseArray = getLastArgument(response);
            }
        }

        return responseArray;
    }
}
