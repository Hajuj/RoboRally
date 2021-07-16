package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

/**
 * @author Viktoria
 */
public class PlayerValuesBody implements ClientMessageAction<PlayerValuesBody> {
    @Expose
    private final String name;
    @Expose
    private final int figure;

    public String getName() {
        return name;
    }

    public int getFigure() {
        return figure;
    }

    public PlayerValuesBody(String name, int figure) {
        this.name = name;
        this.figure = figure;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, PlayerValuesBody playerValuesBody, MessageHandler messageHandler) {
        messageHandler.handlePlayerValues(server, clientHandler, playerValuesBody);

    }

}
