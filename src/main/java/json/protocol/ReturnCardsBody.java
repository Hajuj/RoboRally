package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

import java.util.ArrayList;

/**
 * @author Mohamad
 */
public class ReturnCardsBody implements ClientMessageAction<ReturnCardsBody> {

    @Expose
    private ArrayList<String> cards;

    public ReturnCardsBody(ArrayList<String> cards) {
        this.cards = cards;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, ReturnCardsBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleReturnCards(server, clientHandler, bodyObject);
    }

    public ArrayList<String> getCards() {
        return cards;
    }
}
