package json.protocol;

import client.model.ClientModel;
import server.ClientHandler;
import server.Server;

/**
 * @author Mohamad, Viktoria
 */
public class AliveBody implements ServerMessageAction<AliveBody>, ClientMessageAction<AliveBody> {

    public AliveBody () {

    }


    @Override
    public void triggerAction (Server server, ClientHandler clientHandler, AliveBody bodyObject, server.MessageHandler messageHandler) {
        Thread sadThread = new Thread(new Runnable() {
            @Override
            public void run () {
                messageHandler.handleAlive(server, clientHandler, bodyObject);
            }
        });
        sadThread.start();
    }

    @Override
    public void triggerAction (ClientModel client, AliveBody bodyObject, client.model.MessageHandler messageHandler) {
        messageHandler.handleAlive(client, bodyObject);
    }
}
