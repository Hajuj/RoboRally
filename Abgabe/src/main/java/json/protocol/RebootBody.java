package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class RebootBody implements ServerMessageAction<RebootBody> {
    @Expose
    private int clientID;

    public RebootBody(int clientID) {
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(ClientModel client, RebootBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleReboot(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }
}
