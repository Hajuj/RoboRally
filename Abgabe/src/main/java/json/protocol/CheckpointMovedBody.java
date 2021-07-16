package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class CheckpointMovedBody implements ServerMessageAction<CheckpointMovedBody> {
    @Expose
    private int checkpointID;
    @Expose
    private int x;
    @Expose
    private int y;


    @Override
    public void triggerAction(ClientModel client, CheckpointMovedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleCheckpointMovedBody(client, bodyObject);
    }

    public int getCheckpointID() {
        return checkpointID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public CheckpointMovedBody(int checkpointID, int x, int y) {
        this.checkpointID = checkpointID;
        this.x = x;
        this.y = y;
    }
}
