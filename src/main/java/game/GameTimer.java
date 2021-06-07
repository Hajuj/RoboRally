package game;

import json.protocol.CardsYouGotNowBody;
import json.protocol.TimerEndedBody;
import server.Server;

import json.JSONMessage;
import json.protocol.TimerStartedBody;

import java.util.Timer;
import java.util.TimerTask;

public class GameTimer {
    private Timer timer = new Timer();
    private Server server;

    public GameTimer(Server server) {
        this.server = server;
    }

    public void startTimer() {
        server.getCurrentGame().setTimerOn(true);
        JSONMessage jsonMessage = new JSONMessage("TimerStarted", new TimerStartedBody());
        server.getCurrentGame().sendToAllPlayers(jsonMessage);
        timer.schedule(new RemindTask(), 30000);
    }

    public void timerEnded() {
        server.getCurrentGame().setTimerOn(false);
        server.getCurrentGame().sendToAllPlayers(new JSONMessage("TimerEnded", new TimerEndedBody(server.getCurrentGame().tooLateClients())));
        for (int i : server.getCurrentGame().tooLateClients()) {
            Player player = server.getPlayerWithID(i);
            JSONMessage jsonMessage = new JSONMessage("CardsYouGotNow", new CardsYouGotNowBody(player.drawBlind()));
            server.sendMessage(jsonMessage, server.getConnectionWithID(player.getPlayerID()).getWriter());
        }
        server.getCurrentGame().setActivePhaseOn(false);
        server.getCurrentGame().setActivePhase(3);
        timer.cancel();
    }


    class RemindTask extends TimerTask {
        public void run() {
            timerEnded();
            timer.cancel();
            timer.purge();
        }
    }
}
