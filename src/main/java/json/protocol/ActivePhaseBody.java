package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class ActivePhaseBody implements ServerMessageAction<ActivePhaseBody> {
    @Expose
    private final int phase;

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
