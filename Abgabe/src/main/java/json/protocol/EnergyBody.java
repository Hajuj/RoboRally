package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class EnergyBody implements ServerMessageAction<EnergyBody> {

    @Expose
    private int clientID;

    @Expose
    private int count;

    @Expose
    private String source;

    public EnergyBody(int clientID, int count, String source) {
        this.clientID = clientID;
        this.count = count;
        this.source = source;
    }

    @Override
    public void triggerAction(ClientModel client, EnergyBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleEnergy(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
