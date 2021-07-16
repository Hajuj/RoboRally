package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class GameFinishedBody implements ServerMessageAction<GameFinishedBody> {

    @Expose
    private int clientID;

    public GameFinishedBody(int clientID) {
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(ClientModel client, GameFinishedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleGameFinished(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }
}
