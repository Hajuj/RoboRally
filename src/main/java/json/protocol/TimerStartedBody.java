package json.protocol;

import server.ClientHandler;
import server.MessageHandler;
import server.Server;

/**
 * @author Mohamad
 */
public class TimerStartedBody implements ClientMessageAction<TimerStartedBody> {

    public TimerStartedBody() {

    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, TimerStartedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleTimerStarted(server, clientHandler, bodyObject);
    }
}
