package json.protocol;

import com.google.gson.annotations.Expose;
import json.MessageHandler;
import server.ClientHandler;
import server.Server;

/**
 * @author Mohamad
 */
public class SendChatBody implements ClientMessageAction<SendChatBody> {
    @Expose
    private final String message;
    @Expose
    private final int to;

    public SendChatBody (String message, int to) {
        this.message = message;
        this.to = to;
    }

    @Override
    public void triggerAction (Server server, ClientHandler clientHandler, SendChatBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSendChat(server, clientHandler, bodyObject);
    }

    public String getMessage () {
        return message;
    }

    public int getTo () {
        return to;
    }
}
