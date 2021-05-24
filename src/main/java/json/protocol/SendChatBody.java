package json.protocol;

import server.Server;
import server.ClientHandler;

import json.MessageHandler;

import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class SendChatBody implements ClientMessageAction<SendChatBody> {
    @Expose
    private String message;
    @Expose
    private int to;

    public SendChatBody(String message, int to) {
        this.message = message;
        this.to = to;
    }

    @Override
    public void triggerAction(Server server, ClientHandler task, SendChatBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSendChat(server, task, bodyObject);
    }

    public String getMessage() {
        return message;
    }

    public int getTo() {
        return to;
    }
}
