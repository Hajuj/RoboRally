package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class PlayerStatusBody implements ServerMessageAction<PlayerStatusBody> {

    @Expose
    private final int clientID;
    @Expose
    private final boolean ready;

    public PlayerStatusBody(int clientID, boolean ready) {
        this.clientID = clientID;
        this.ready = ready;
    }


    @Override
    public void triggerAction(ClientModel client, PlayerStatusBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handlePlayerStatus(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }

    public boolean isReady() {
        return ready;
    }
}
