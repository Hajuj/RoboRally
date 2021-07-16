package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class AnimationBody implements ServerMessageAction<AnimationBody> {

    @Expose
    private String type;

    public AnimationBody(String type) {
        this.type = type;
    }


    @Override
    public void triggerAction(ClientModel client, AnimationBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleAnimation(client, bodyObject);
    }

    public String getType() {
        return type;
    }
}
