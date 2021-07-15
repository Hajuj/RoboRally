package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

/**
 * This is the wrapper class for the message body of the 'HelloServer' protocol JSON message.
 *
 * @author Mohamad, Viktoria
 */
public class HelloServerBody implements ClientMessageAction<HelloServerBody> {
    @Expose
    private final String group;
    @Expose
    private final boolean isAI;
    @Expose
    private final String protocol;

    public HelloServerBody(String group, Boolean isAI, String protocol) {
        this.group = group;
        this.isAI = isAI;
        this.protocol = protocol;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, HelloServerBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleHelloServer(server, clientHandler, bodyObject);
    }

    public String getProtocol() {
        return this.protocol;
    }

    public boolean isAI() {
        return isAI;
    }
}
