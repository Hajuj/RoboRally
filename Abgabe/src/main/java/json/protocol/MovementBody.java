package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class MovementBody implements ServerMessageAction<MovementBody> {

    @Expose
    private int clientID;
    @Expose
    private int x;
    @Expose
    private int y;

    public MovementBody(int clientID, int x, int y) {
        this.clientID = clientID;
        this.x = x;
        this.y = y;
    }

    @Override
    public void triggerAction(ClientModel client, MovementBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleMovement(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
