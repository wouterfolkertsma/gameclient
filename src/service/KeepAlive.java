package service;

import java.util.TimerTask;

/**
 * @author Wouter Folkertsma
 */
public class KeepAlive extends TimerTask {
    private ServerService serverService;

    KeepAlive(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    public void run() {
        this.serverService.getPlayerList();
    }
}
