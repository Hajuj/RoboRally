package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Viktoria
 */
public class CardsYouGotNowBody implements ServerMessageAction<CardsYouGotNowBody> {
    @Expose
    private ArrayList<String> cards;

    @Override
    public void triggerAction (ClientModel client, CardsYouGotNowBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleCardsYouGotNowBody(client, bodyObject);
    }

    public CardsYouGotNowBody (ArrayList<String> cards) {
        this.cards = cards;
    }

    public ArrayList<String> getCards () {
        return cards;
    }
}
