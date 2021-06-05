package json.protocol;

import client.model.ClientModel;

/**
 * @author Mohamad
 */
public class TimerStartedBody implements ServerMessageAction<TimerStartedBody> {

    public TimerStartedBody() {

    }

    @Override
    public void triggerAction(ClientModel client, TimerStartedBody bodyObject, client.model.MessageHandler messageHandler) {
        messageHandler.handleTimerStarted(client, bodyObject);
    }
}
