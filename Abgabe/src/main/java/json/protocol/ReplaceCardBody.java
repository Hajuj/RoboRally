package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class ReplaceCardBody implements ServerMessageAction<ReplaceCardBody> {
    @Expose
    private int register;
    @Expose
    private String newCard;
    @Expose
    private int clientID;

    public ReplaceCardBody(int register, String newCard, int clientID) {
        this.register = register;
        this.newCard = newCard;
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(ClientModel client, ReplaceCardBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleReplaceCard(client, bodyObject);
    }

    public int getRegister() {
        return register;
    }

    public String getNewCard() {
        return newCard;
    }

    public int getClientID() {
        return clientID;
    }
}
