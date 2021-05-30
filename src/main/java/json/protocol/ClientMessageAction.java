package json.protocol;

import server.Server;
import server.ClientHandler;

import json.MessageHandler;

/**
 * This interface is implemented by each class in {@link json.protocol} that represents the message body.
 * It only demands a function to be implemented that contains all the logic that should
 * happen when a message with the respective message body was received by either the client or the server.
 *
 * @author Mohamad, Viktoria
 */
public interface ClientMessageAction<T> {
    void triggerAction(Server server, ClientHandler clientHandler, T bodyObject, MessageHandler messageHandler);
}
