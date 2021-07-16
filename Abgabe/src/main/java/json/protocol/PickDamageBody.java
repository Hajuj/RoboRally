package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class PickDamageBody implements ServerMessageAction<PickDamageBody> {
    @Expose
    private int count;

    public PickDamageBody(int count) {
        this.count = count;
    }

    @Override
    public void triggerAction(ClientModel client, PickDamageBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handlePickDamage(client, bodyObject);
    }

    public int getCount() {
        return count;
    }
}
