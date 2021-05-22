package json.protocol;

import com.google.gson.annotations.Expose;
import server.Server;
import json.MessageHandler;
import server.ClientHandler;

/**
 * This is the wrapper class for the message body of the 'HelloServer' protocol JSON message.
 *
 * @author Mohamad, Viktoria
 */
public class HelloServerBody implements ClientMessageAction<HelloServerBody> {
    @Expose
    private String group;
    @Expose
    private Boolean isAI;
    @Expose
    private String protocol;

    public HelloServerBody (String group, Boolean isAI, String protocol) {
        this.group = group;
        this.isAI = isAI;
        this.protocol = protocol;
    }

    @Override
    public void triggerAction (Server server, ClientHandler task, HelloServerBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleHelloServer(server, task, bodyObject);
    }

    public String getProtocol () {
        return this.protocol;
    }
}
