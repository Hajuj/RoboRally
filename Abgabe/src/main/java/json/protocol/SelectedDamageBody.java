package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

import java.util.ArrayList;

/**
 * @author Mohamad
 */
public class SelectedDamageBody implements ClientMessageAction<SelectedDamageBody> {
    @Expose
    private ArrayList<String> cards;

    public SelectedDamageBody(ArrayList<String> cards) {
        this.cards = cards;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, SelectedDamageBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSelectedDamage(server, clientHandler, bodyObject);
    }

    public ArrayList<String> getCards() {
        return cards;
    }
}
