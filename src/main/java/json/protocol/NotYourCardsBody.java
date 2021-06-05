package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class NotYourCardsBody implements ServerMessageAction<NotYourCardsBody> {

    @Expose
    private int clientID;
    @Expose
    private int cardsInHand;

    public NotYourCardsBody (int clientID, int cardsInHand) {
        this.clientID = clientID;
        this.cardsInHand = cardsInHand;
    }

    @Override
    public void triggerAction (ClientModel client, NotYourCardsBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleNotYourCards(client, bodyObject);
    }

    public int getClientID () {
        return clientID;
    }

    public int getCardsInHand () {
        return cardsInHand;
    }
}
