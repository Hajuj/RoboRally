package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class ShuffleCodingBody implements ServerMessageAction<ShuffleCodingBody> {
    @Expose
    private int clientID;

    public ShuffleCodingBody(int clientID) {
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(ClientModel client, ShuffleCodingBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleShuffleCoding(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }
}
