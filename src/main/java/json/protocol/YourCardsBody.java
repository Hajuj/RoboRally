package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Viktoria
 */
public class YourCardsBody implements ServerMessageAction<YourCardsBody> {

    @Expose
    private ArrayList<String> cardsInHand;

    public YourCardsBody(ArrayList<String> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    @Override
    public void triggerAction(ClientModel client, YourCardsBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleYourCards(client, bodyObject);
    }

    public ArrayList<String> getCardsInHand() {
        return cardsInHand;
    }
}
