package service;

import javafx.application.Platform;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Wouter Folkertsma
 */
public class ServerListenerService extends Thread {

    private ServerService serverService;
    private Scanner scanner;
    private LinkedList<String> queue;
    boolean mayRead = true;

    ServerListenerService(ServerService serverService, Scanner scanner, LinkedList<String> queue) {
        this.serverService = serverService;
        this.scanner = scanner;
        this.queue = queue;
        this.start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            try {
                if (scanner.hasNextLine()) {
                    String newLine = scanner.nextLine();
                    System.out.println(newLine);
                    if (mayRead) {
                        Platform.runLater(() -> this.serverService.handleResponse(newLine)); //DEze lustert en handled alle commandos
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
