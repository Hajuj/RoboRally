package json.protocol;

import com.google.gson.annotations.Expose;
import json.MessageHandler;
import server.ClientHandler;
import server.Server;

/*
{
   "messageType": "SetStatus",
   "messageBody": {
      "ready": true
   }
}

 */

public class SetStatusBody implements ClientMessageAction<SetStatusBody> {

    @Expose
    private final boolean ready;

    public SetStatusBody (boolean ready) {
        this.ready = ready;
    }

    @Override
    public void triggerAction (Server server, ClientHandler clientHandler, SetStatusBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSetStatus(server, clientHandler, bodyObject);
    }

    public boolean isReady () {
        return ready;
    }
}
