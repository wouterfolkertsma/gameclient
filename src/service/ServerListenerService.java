package service;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ServerListenerService extends Thread {

    private ServerService serverService;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Scanner scanner;
    private LinkedList<String> queue;
    boolean mayRead = true;

    ServerListenerService(ServerService serverService, Socket socket, Scanner scanner, LinkedList<String> queue) {
        this.serverService = serverService;
        this.socket = socket;
        this.scanner = scanner;
        this.queue = queue;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

        this.start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            try {
                if (scanner.hasNextLine()) {
                    String newLine = scanner.nextLine();
                    if (mayRead) {
                        Platform.runLater(() -> this.serverService.handleResponse(newLine));
                    } else {
                        this.queue.add(newLine);
                    }
                }
            } catch (Exception exception) {
                System.out.println(exception.toString());
            }
        }
    }
}
