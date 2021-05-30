package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

/*
{
"messageType": "PlayerStatusBody",
   "messageBody": {
      "clientID": 42,
      "ready": true
   }
}

 */
public class PlayerStatusBody implements ServerMessageAction<PlayerStatusBody> {

    @Expose
    private int clientID;
    @Expose
    private boolean ready;

    public PlayerStatusBody (int clientID, boolean ready) {
        this.clientID = clientID;
        this.ready = ready;
    }


    @Override
    public void triggerAction (ClientModel client, ClientModelReaderThread readerThread, PlayerStatusBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handlePlayerStatus(client, readerThread, bodyObject);
    }

    public int getClientID () {
        return clientID;
    }

    public boolean isReady () {
        return ready;
    }
}
