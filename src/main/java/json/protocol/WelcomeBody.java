package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;

import json.MessageHandler;

import com.google.gson.annotations.Expose;

/**
 * This is the wrapper class for the message body of the 'Welcome' protocol JSON message.
 *
 * @author Mohamad, Viktoria
 */
public class WelcomeBody implements json.protocol.ServerMessageAction<WelcomeBody> {
    @Expose
    private final Integer playerID;

    public WelcomeBody(Integer playerID) {
        this.playerID = playerID;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    @Override
    public void triggerAction(ClientModel client, ClientModelReaderThread task, WelcomeBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleWelcome(client, task, bodyObject);
    }
}
