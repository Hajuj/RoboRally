package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;

import json.MessageHandler;

import com.google.gson.annotations.Expose;

/**
 * This is the wrapper class for the message body of the 'Error' protocol JSON message.
 *
 * @author Mohamad, Viktoria
 */
public class ErrorBody implements ServerMessageAction<ErrorBody> {
    @Expose
    private final String error;

    public ErrorBody (String error) {
        this.error = error;
    }

    public String getError () {
        return error;
    }

    @Override
    public void triggerAction (ClientModel clientmodel, ClientModelReaderThread readerThread, ErrorBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleError(clientmodel, readerThread, bodyObject);
    }
}
