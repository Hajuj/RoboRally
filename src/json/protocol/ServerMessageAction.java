package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import json.MessageHandler;

/**
 * This interface is implemented by each class in {@link json.protocol} that represents the message body.
 * It only demands a function to be implemented that contains all the logic that should
 * happen when a message with the respective message body was received by either the client or the server.
 *
 * @author Mohamad, Viktoria
 */
public interface ServerMessageAction<T> {
    void triggerAction (ClientModel client, ClientModelReaderThread task, T bodyObject, MessageHandler messageHandler);
}