package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

/*
{
"messageType": "PlayerStatus",
   "messageBody": {
      "clientID": 42,
      "ready": true
   }
}

 */
public class PlayerStatus implements ServerMessageAction<PlayerStatus> {

    @Expose
    private int clientID;
    @Expose
    private boolean ready;

    public PlayerStatus (int clientID, boolean ready) {
        this.clientID = clientID;
        this.ready = ready;
    }


    @Override
    public void triggerAction (ClientModel client, ClientModelReaderThread readerThread, PlayerStatus bodyObject, MessageHandler messageHandler) {
        messageHandler.handlePlayerStatus(client, readerThread, bodyObject);
    }

    public int getClientID () {
        return clientID;
    }

    public boolean isReady () {
        return ready;
    }
}
