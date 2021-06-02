package json.protocol;

import client.model.ClientModel;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

/**
 * @author Mohamad
 */
public class PlayerAddedBody implements ServerMessageAction<PlayerAddedBody> {
    @Expose
    private final int clientID;
    @Expose
    private final String name;
    @Expose
    private final int figure;

    public PlayerAddedBody(int clientID, String name, int figure) {
        this.clientID = clientID;
        this.name = name;
        this.figure = figure;
    }

    @Override
    public void triggerAction(ClientModel client, PlayerAddedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handlePlayerAdded(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }

    public String getName() {
        return name;
    }

    public int getFigure() {
        return figure;
    }
}
