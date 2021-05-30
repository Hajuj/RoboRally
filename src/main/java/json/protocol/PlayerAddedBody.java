package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

/**
 * @author Mohamad
 */
public class PlayerAddedBody implements ServerMessageAction<PlayerAddedBody> {
    @Expose
    private int clientID;
    @Expose
    private String name;
    @Expose
    private int figure;

    public PlayerAddedBody(int clientID, String name, int figure) {
        this.clientID = clientID;
        this.name = name;
        this.figure = figure;
    }

    @Override
    public void triggerAction(ClientModel client, ClientModelReaderThread readerThread, PlayerAddedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handlePlayerAdded(client, readerThread, bodyObject);
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
