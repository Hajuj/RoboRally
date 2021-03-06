package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Viktoria
 */
public class DrawDamageBody implements ServerMessageAction<DrawDamageBody> {

    @Expose
    private int clientID;

    @Expose
    private ArrayList<String> cards;

    public DrawDamageBody(int clientID, ArrayList<String> cards) {
        this.clientID = clientID;
        this.cards = cards;
    }

    @Override
    public void triggerAction(ClientModel client, DrawDamageBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleDrawDamage(client, bodyObject);
    }

    public int getClientID() {
        return clientID;
    }

    public ArrayList<String> getCards() {
        return cards;
    }
}
