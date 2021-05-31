package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import com.google.gson.annotations.Expose;
import json.MessageHandler;
import server.ClientHandler;
import server.Server;

/**
 * @author Mohamad
 */
public class MapSelectedBody implements ServerMessageAction<MapSelectedBody>, ClientMessageAction<MapSelectedBody> {
    @Expose
    private String map;

    public MapSelectedBody(String map) {
        this.map = map;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, MapSelectedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleMapSelected(server, clientHandler, bodyObject);
    }

    @Override
    public void triggerAction(ClientModel client, MapSelectedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleMapSelected(client, bodyObject);
    }

    public String getMap() {
        return map;
    }
}
