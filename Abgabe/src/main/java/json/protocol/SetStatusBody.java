package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

/**
 * @author Viktoria
 */
public class SetStatusBody implements ClientMessageAction<SetStatusBody> {

    @Expose
    private final boolean ready;

    public SetStatusBody(boolean ready) {
        this.ready = ready;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, SetStatusBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSetStatus(server, clientHandler, bodyObject);
    }

    public boolean isReady() {
        return ready;
    }
}
