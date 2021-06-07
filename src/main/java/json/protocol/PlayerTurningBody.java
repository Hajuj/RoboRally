package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class PlayerTurningBody implements ServerMessageAction<PlayerTurningBody> {

    @Expose
    private int clientID;

    @Expose
    private String rotation;

    @Override
    public void triggerAction (ClientModel client, PlayerTurningBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handlePlayerTurning(client, bodyObject);
    }
}
