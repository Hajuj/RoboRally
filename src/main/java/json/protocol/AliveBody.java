package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import json.MessageHandler;
import server.ClientHandler;
import server.Server;

public class AliveBody implements ServerMessageAction<AliveBody>, ClientMessageAction<AliveBody> {

    public AliveBody () {

    }


    @Override
    public void triggerAction (Server server, ClientHandler clientHandler, AliveBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleAlive(server, clientHandler, bodyObject);
    }

    @Override
    public void triggerAction (ClientModel client, ClientModelReaderThread readerThread, AliveBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleAlive(client, readerThread, bodyObject);
    }
}
