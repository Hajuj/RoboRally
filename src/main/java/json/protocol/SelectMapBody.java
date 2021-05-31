package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import json.MessageHandler;

import java.util.ArrayList;

public class SelectMapBody implements ServerMessageAction<SelectMapBody> {

    ArrayList<String> availableMaps;

    public ArrayList<String> getAvailableMaps () {
        return availableMaps;
    }

    public SelectMapBody (ArrayList<String> availableMaps) {
        this.availableMaps = availableMaps;
    }

    @Override
    public void triggerAction (ClientModel client, ClientModelReaderThread readerThread, SelectMapBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSelectMap(client, readerThread, bodyObject);
    }
}
