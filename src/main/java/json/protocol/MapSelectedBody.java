package json.protocol;

import client.model.ClientModel;
import com.google.gson.annotations.Expose;
import json.MessageHandler;
import server.ClientHandler;
import server.Server;

import java.io.IOException;

/**
 * @author Mohamad
 */
public class MapSelectedBody implements ServerMessageAction<MapSelectedBody>, ClientMessageAction<MapSelectedBody> {
    @Expose
    private final String map;

    public MapSelectedBody(String map) {
        this.map = map;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, MapSelectedBody bodyObject, MessageHandler messageHandler) {
        try {
            messageHandler.handleMapSelected(server, clientHandler, bodyObject);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void triggerAction(ClientModel client, MapSelectedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleMapSelected(client, bodyObject);
    }

    public String getMap() {
        return map;
    }
}
