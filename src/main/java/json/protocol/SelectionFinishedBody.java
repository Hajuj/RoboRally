package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/*
{
"messageType": "SelectionFinishedBody",
"messageBody": {
"clientID": 42
}
 */
public class SelectionFinishedBody implements ServerMessageAction<SelectionFinishedBody> {

    @Expose
    private int clientID;

    public SelectionFinishedBody (int clientID) {
        this.clientID = clientID;
    }

    @Override
    public void triggerAction (ClientModel client, SelectionFinishedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSelectionFinished(client, bodyObject);
    }

    public int getClientID () {
        return clientID;
    }
}
