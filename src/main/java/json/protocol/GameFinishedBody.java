package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

public class GameFinishedBody implements ServerMessageAction<GameFinishedBody> {

    @Expose
    private int clientID;

    @Override
    public void triggerAction (ClientModel client, GameFinishedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleGameFinished(client, bodyObject);
    }

    public GameFinishedBody (int clientID) {
        this.clientID = clientID;
    }

    public int getClientID () {
        return clientID;
    }
}
