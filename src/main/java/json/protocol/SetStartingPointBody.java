package json.protocol;


import com.google.gson.annotations.Expose;
import json.MessageHandler;
import server.ClientHandler;
import server.Server;

/*
{
    "messageType": "SetStartingPoint",
    "messageBody": {
        "x": 4,
        "y": 2
    }
}

 */
public class SetStartingPointBody implements ClientMessageAction<SetStartingPointBody> {

    @Expose
    private final int x;
    @Expose
    private final int y;

    public SetStartingPointBody (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX () {
        return x;
    }

    public int getY () {
        return y;
    }

    @Override
    public void triggerAction (Server server, ClientHandler clientHandler, SetStartingPointBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSetStartingPoint(server, clientHandler, bodyObject);
    }
}
