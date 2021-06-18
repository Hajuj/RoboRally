package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.Server;

/**
 * @author Mohamad
 */
public class RebootDirectionBody implements ClientMessageAction<RebootDirectionBody> {
    @Expose
    private String direction;

    public RebootDirectionBody(String direction) {
        this.direction = direction;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, RebootDirectionBody bodyObject, server.MessageHandler messageHandler) {
        messageHandler.handleRebootDirection(server, clientHandler, bodyObject);
    }

    public String getDirection() {
        return direction;
    }
}
