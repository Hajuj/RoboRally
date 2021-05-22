package json.protocol;

import client.Client;
import client.ClientThread;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

/**
 * This is the wrapper class for the message body of the 'Welcome' protocol JSON message.
 *
 * @author Mohamad, Viktoria
 */
public class WelcomeBody implements json.protocol.ServerMessageAction<WelcomeBody> {
    @Expose
    private Integer playerID;

    public WelcomeBody(Integer playerID) {
        this.playerID = playerID;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    @Override
    public void triggerAction(Client client, ClientThread task, WelcomeBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleWelcome(client, task, bodyObject);
    }
}
