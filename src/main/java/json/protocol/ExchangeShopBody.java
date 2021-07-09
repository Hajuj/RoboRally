package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Mohamad
 */
public class ExchangeShopBody implements ServerMessageAction<ExchangeShopBody> {

    @Expose
    private ArrayList<String> cards;

    public ExchangeShopBody(ArrayList<String> cards) {
        this.cards = cards;
    }

    @Override
    public void triggerAction(ClientModel client, ExchangeShopBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleExchangeShop(client, bodyObject);
    }

    public ArrayList<String> getCards() {
        return cards;
    }
}
