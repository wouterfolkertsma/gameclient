package service;

import main.GameClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class ServerService contains all the communication methods for the server.
 *
 * @author Wouter Folkertsma
 */
public class ServerService {

    private GameClient gameClient;

    private PrintWriter toServer = null;
    private BufferedReader fromServer = null;
    private Socket socket = null;
    private boolean connected = false;

    public ServerService(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public void login(String s){
        while(!connected) {
            try {
                socket = new Socket("localhost", 7789);
                connected = true;
                toServer = new PrintWriter(socket.getOutputStream());
                fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                return;
            }

            toServer.println("login " + s);
            System.out.print(getServerResponse());
        }
    }

    public void exit(){
        try {
            toServer.println("bye");
            toServer.close();
            fromServer.close();
            socket.close();
        }
        catch(IOException e){
            return;
        }
    }

    public String getServerResponse(){
        String output = "";
        try{
            while(!fromServer.readLine().equals(null)){
                output += fromServer.readLine();
            }
        }catch (IOException e){
            return null;
        }
        return output;
    }

    public String retrievePlayers(){
        toServer.println("get playerlist");
        return getServerResponse();
    }

    public String retrieveGameList(){
        toServer.println("get gamelist");
        return getServerResponse();
    }

    public String subscribe(String s){
        toServer.println("subscribe " + s);
        return getServerResponse();
    }

    public void matchStart(){
        System.out.println();
    }

    public void playerTurn(){
        System.out.println();
    }

    public void makeMove(String s){
        toServer.println("move " + s);
    }

    public void forfeit(){
        toServer.println("forfeit");
    }

    public String receiveResult(){
        return getServerResponse();
    }

    public String challengePlayer(String s){
        toServer.println("challenge " + s);
        return getServerResponse();
    }

    public String receiveChallenge(){
        return getServerResponse();
    }

    public void acceptChallenge(){
        toServer.println("challenge accept");
    }

    public void getHelp(){
        toServer.println("help");
        System.out.print(getServerResponse());
    }







    /**
     * @return ArrayList deprecated, delete if not needed.
     */
//    public ArrayList<String> retrievePlayers() {
//        ArrayList<String> list = new ArrayList<String>();
//
//        list.add("Wouter");
//        list.add("Wouter1");
//        list.add("Wouter2");
//        list.add("Wouter3");
//
//        return list;
//    }

//
//    /**
//     * @return ArrayList
//     */
//    public ArrayList<String> retrieveGameList() {
//        ArrayList<String> list = new ArrayList<String>();
//
//        list.add("TestGame1");
//        list.add("TestGame2");
//
//        return list;
//    }

}
