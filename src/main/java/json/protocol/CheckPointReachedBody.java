package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

public class CheckPointReachedBody implements ServerMessageAction<CheckPointReachedBody> {

    @Expose
    private int clientID;

    @Expose
    private int number;


    @Override
    public void triggerAction (ClientModel client, CheckPointReachedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleCheckPointReachedBody(client, bodyObject);
    }


    public CheckPointReachedBody (int clientID, int number) {
        this.clientID = clientID;
        this.number = number;
    }

    public int getClientID () {
        return clientID;
    }

    public void setClientID (int clientID) {
        this.clientID = clientID;
    }

    public int getNumber () {
        return number;
    }

    public void setNumber (int number) {
        this.number = number;
    }
}
