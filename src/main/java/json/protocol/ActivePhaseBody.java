package json.protocol;

import client.model.ClientModel;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

/**
 * @author Mohamad
 */
public class ActivePhaseBody implements ServerMessageAction<ActivePhaseBody> {
    @Expose
    private int phase;

    public ActivePhaseBody(int phase) {
        this.phase = phase;
    }

    @Override
    public void triggerAction(ClientModel client, ActivePhaseBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleActivePhase(client, bodyObject);
    }

    public int getPhase() {
        return phase;
    }
}
