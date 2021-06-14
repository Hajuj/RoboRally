package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class RebootDirectionBody implements ServerMessageAction<RebootDirectionBody> {
    @Expose
    private String direction;

    public RebootDirectionBody(String direction) {
        this.direction = direction;
    }

    @Override
    public void triggerAction(ClientModel client, RebootDirectionBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleRebootDirection(client, bodyObject);
    }

    public String getDirection() {
        return direction;
    }
}
