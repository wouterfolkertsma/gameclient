package service;

import main.GameClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class ServerService contains all the communication methods for the server.
 *
 * @author Wouter Folkertsma, Daniël Windstra, Anthonie Ooms
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
        InetSocketAddress address = new InetSocketAddress("localhost", 7789);

        while(!connected) {
            try {

//                    socket = new Socket("localhost", 7789);

                socket.connect(address);
                socket.setKeepAlive(true);
                toServer = new PrintWriter(socket.getOutputStream());
                fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                connected = true;
                toServer.println("login " + s);


            } catch (IOException e) {
                return;
            }

        }
        getServerResponse();
    }

        public void exit(){
            try {
                toServer.println("bye");
                toServer.close();
                fromServer.close();
                socket.close();
                connected = false;
            }
            catch(IOException e){
                return;
            }
        }

    public String getServerResponse() {
        StringBuilder sb = new StringBuilder();
        String line;
        ArrayList<String> allLines = new ArrayList<>();

        try {
            line = fromServer.readLine();

            while(line != null) {
                sb.append(line).append("\n");
                line = null;

                if (fromServer.ready()) {
                    line = fromServer.readLine();
                    allLines.add(line);
                }
            }
            System.out.println(allLines.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }



        return sb.toString();
    }

    public boolean getServerState(){
        boolean isReady;
        try{
            isReady = fromServer.ready();
        }catch (Exception e){
            return false;
        }
        return isReady;
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
        HashMap<String, String> map = new HashMap<>();
        getServerResponse();
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








}
