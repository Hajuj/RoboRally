package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * This is the wrapper class for the message body of the 'Welcome' protocol JSON message.
 *
 * @author Mohamad, Viktoria
 */
public class WelcomeBody implements ServerMessageAction<WelcomeBody> {
    @Expose
    private final Integer clientID;

    public WelcomeBody (Integer clientID) {
        this.clientID = clientID;
    }

    public Integer getClientID () {
        return clientID;
    }

    @Override
    public void triggerAction (ClientModel client, WelcomeBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleWelcome(client, bodyObject);
    }
}
