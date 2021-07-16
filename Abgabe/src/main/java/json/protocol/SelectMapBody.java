package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;

import java.util.ArrayList;

/**
 * @author Viktoria
 */
public class SelectMapBody implements ServerMessageAction<SelectMapBody> {

    ArrayList<String> availableMaps;

    public ArrayList<String> getAvailableMaps() {
        return availableMaps;
    }

    public SelectMapBody(ArrayList<String> availableMaps) {
        this.availableMaps = availableMaps;
    }

    @Override
    public void triggerAction(ClientModel client, SelectMapBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSelectMap(client, bodyObject);
    }
}
