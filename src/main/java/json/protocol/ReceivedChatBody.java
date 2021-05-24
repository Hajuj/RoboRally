package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;

import json.MessageHandler;

import com.google.gson.annotations.Expose;

/**
 * @author Mohamad
 */
public class ReceivedChatBody implements ServerMessageAction<ReceivedChatBody> {
    @Expose
    private String message;
    @Expose
    private int from;
    @Expose
    private boolean isPrivate;

    public ReceivedChatBody(String message, int from, boolean isPrivate) {
        this.message = message;
        this.from = from;
        this.isPrivate = isPrivate;
    }

    @Override
    public void triggerAction(ClientModel client, ClientModelReaderThread task, ReceivedChatBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleReceivedChat(client, task, bodyObject);
    }

    public String getMessage() {
        return message;
    }

    public int getFrom() {
        return from;
    }
}
