package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Mohamad
 */
public class RefillShopBody implements ServerMessageAction<RefillShopBody> {

    @Expose
    private ArrayList<String> cards;

    public RefillShopBody(ArrayList<String> cards) {
        this.cards = cards;
    }

    @Override
    public void triggerAction(ClientModel client, RefillShopBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleRefillShop(client, bodyObject);
    }

    public ArrayList<String> getCards() {
        return cards;
    }
}
