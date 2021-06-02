package json.protocol;

/*
{
    "messageType": "StartingPointTaken",
    "messageBody": {
        "x": 4,
        "y": 2,
        "clientID": 42
    }
}

 */

import client.model.ClientModel;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

public class StartingPointTakenBody implements ServerMessageAction<StartingPointTakenBody> {

    @Expose
    private int x;
    @Expose
    private int y;
    @Expose
    private int clientID;

    public StartingPointTakenBody (int x, int y, int clientID) {
        this.x = x;
        this.y = y;
        this.clientID = clientID;
    }

    public int getX () {
        return x;
    }

    public int getY () {
        return y;
    }

    public int getClientID () {
        return clientID;
    }

    @Override
    public void triggerAction (ClientModel client, StartingPointTakenBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleStartingPointTaken(client, bodyObject);
    }
}
