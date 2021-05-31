package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;

import json.MessageHandler;

import com.google.gson.annotations.Expose;

/**
 * This is the wrapper class for the message body of the 'HelloClient' protocol JSON message.
 *
 * @author Mohamad, Viktoria
 */
public class HelloClientBody implements ServerMessageAction<HelloClientBody> {
    @Expose
    String protocol;

    public HelloClientBody (String protocol) {
        this.protocol = protocol;
    }

    @Override
    public void triggerAction (ClientModel client, ClientModelReaderThread readerThread, HelloClientBody message, MessageHandler messageHandler) {
        messageHandler.handleHelloClient(client, readerThread, message);
    }

    public String getProtocol () {
        return protocol;
    }
}
