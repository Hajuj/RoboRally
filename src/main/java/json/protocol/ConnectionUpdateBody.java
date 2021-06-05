package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;

/**
 * @author Viktoria
 */
public class ConnectionUpdateBody implements ServerMessageAction<ConnectionUpdateBody> {

    private final int playerID;
    private final boolean isConnected;
    private final String action;

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
