package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class CardSelectedBody implements ServerMessageAction<CardSelectedBody> {
    @Expose
    private int clientID;
    @Expose
    private int register;
    @Expose
    private boolean filled;

    public CardSelectedBody(int clientID, int register, boolean filled) {
        this.clientID = clientID;
        this.register = register;
        this.filled = filled;
    }

    @Override
    public void triggerAction(ClientModel client, CardSelectedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleCardSelected(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }

    public int getRegister() {
        return register;
    }

    public boolean isFilled() {
        return filled;
    }
}
