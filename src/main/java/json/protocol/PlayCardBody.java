package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

/**
 * @author altug
 */

public class PlayCardBody implements ClientMessageAction<PlayCardBody> {

    @Expose
    private String card;


    public PlayCardBody (String card) {
        this.card = card;

    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, PlayCardBody playCardBody, MessageHandler messageHandler) {
        messageHandler.handlePlayCard(server, clientHandler, playCardBody);

    }


    public String getCard() {
        return card;

    }

}
