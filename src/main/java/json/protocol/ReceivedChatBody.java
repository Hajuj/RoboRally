package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class ReceivedChatBody implements ServerMessageAction<ReceivedChatBody> {
    @Expose
    private final String message;
    @Expose
    private final int from;
    @Expose
    private final boolean isPrivate;

    public ReceivedChatBody(String message, int from, boolean isPrivate) {
        this.message = message;
        this.from = from;
        this.isPrivate = isPrivate;
    }

    @Override
    public void triggerAction(ClientModel client, ReceivedChatBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleReceivedChat(client, bodyObject);
    }

    public String getMessage() {
        return message;
    }

    public int getFrom() {
        return from;
    }
}
