package json.protocol;

import client.model.ClientModel;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

/**
 * @author Mohamad
 */
public class CurrentPlayerBody implements ServerMessageAction<CurrentPlayerBody> {
    @Expose
    private int clientID;

    public CurrentPlayerBody(int clientID) {
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(ClientModel client, CurrentPlayerBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleCurrentPlayer(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }
}
