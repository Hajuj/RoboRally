package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class UpgradeBoughtBody implements ServerMessageAction<UpgradeBoughtBody> {

    @Expose
    private int clientID;
    @Expose
    private String card;

    public UpgradeBoughtBody(int clientID, String card) {
        this.clientID = clientID;
        this.card = card;
    }

    @Override
    public void triggerAction(ClientModel client, UpgradeBoughtBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleUpgradeBought(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }

    public String getCard() {
        return card;
    }
}
