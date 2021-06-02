package json.protocol;

/*
{
"messageType": "ConnectionUpdate",
"messageBody": {
    "playerID": 9001,
    "isConnected": false,
    "action": "AIControl"
    }
}

 */

import client.model.ClientModel;
import json.MessageHandler;

public class ConnectionUpdateBody implements ServerMessageAction<ConnectionUpdateBody> {

    private int playerID;
    private boolean isConnected;
    private String action;

    public ConnectionUpdateBody (int playerID, boolean isConnected, String action) {
        this.playerID = playerID;
        this.isConnected = isConnected;
        this.action = action;
    }

    @Override
    public void triggerAction (ClientModel client, ConnectionUpdateBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleConnectionUpdate(client, bodyObject);
    }

    public int getPlayerID () {
        return playerID;
    }

    public boolean isConnected () {
        return isConnected;
    }

    public String getAction () {
        return action;
    }
}
