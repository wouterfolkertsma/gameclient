package service;

import main.GameClient;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class ServerService contains all the communication methods for the server.
 *
 * @author Wouter Folkertsma, Daniël Windstra, Anthonie Ooms
 */
public class ServerService extends Thread {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 7789;

    private GameClient gameClient;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private Socket socket = null;
    private boolean connected = false;
    private Scanner scanner;

    public ServerService(GameClient gameClient) {
        this.gameClient = gameClient;
        this.start();
    }

    @Override
    public void run() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(socket.getInputStream());
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

        while (true) {
            try {
                if (scanner.hasNextLine()) {
                    String newLine = scanner.nextLine();
                    if (!newLine.equals("OK")) {
                        this.handleResponse(newLine);
                    }
                    System.out.println(newLine);
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private void handleResponse(String newLine) {
        if (newLine.contains("SVR GAME YOURTURN")) {
            this.handleOpponentTurn(newLine);
        } else if (newLine.contains("SVR GAME LOSS")) {

        } else if (newLine.contains("SVR GAME LOSS")) {

        } else if (newLine.contains("SVR GAME LOSS")) {

        } else if (newLine.contains("SVR GAME LOSS")) {

        } else if (newLine.contains("SVR GAME LOSS")) {

        } else if (newLine.contains("SVR GAME LOSS")) {

        } else if (newLine.contains("SVR GAME LOSS")) {

        }
    }

    private void handleOpponentTurn(String line) {

    }

    public void login(String userName) {
        System.out.println("Loggin in!");
        writeLine("login Test");
        writeLine("get playerlist");
        writeLine("subscribe Tic-tac-toe");
    }

    public void exit() {
        try {
            writeLine("bye");
            bufferedWriter.close();
            bufferedReader.close();
            socket.close();
            connected = false;
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean getServerState(){
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
        System.out.println();
    }

    public void playerTurn(){
        System.out.println();
    }

    public void makeMove(String s){
        writeLine("move " + s);
    }

    public void forfeit(){
        writeLine("forfeit");
    }

    public void receiveResult(){
    }

    public void challengePlayer(String s){
        writeLine("challenge " + s);
    }

    public void receiveChallenge() {
    }

    public void acceptChallenge(){
        writeLine("challenge accept");
    }

    public void getHelp() {
        writeLine("help");
    }

    private void writeLine(String line) {
        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
