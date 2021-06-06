package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Mohamad
 */
public class TimerEndedBody implements ServerMessageAction<TimerEndedBody> {
    @Expose
    private ArrayList<Integer> clientIDs;

    public TimerEndedBody(ArrayList<Integer> clientIDs) {
        this.clientIDs = clientIDs;
    }

    @Override
    public void triggerAction(ClientModel client, TimerEndedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleTimerEnded(client, bodyObject);
    }

    public ArrayList<Integer> getClientIDs() {
        return clientIDs;
    }
}
