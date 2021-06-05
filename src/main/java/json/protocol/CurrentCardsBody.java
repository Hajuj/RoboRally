package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Mohamad
 */
public class CurrentCardsBody implements ServerMessageAction<CurrentCardsBody> {
    @Expose
    ArrayList<Object> activeCards = new ArrayList <Object>();

    public CurrentCardsBody(ArrayList<Object> activeCards) {
        this.activeCards = activeCards;
    }

    @Override
    public void triggerAction(ClientModel client, CurrentCardsBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleCurrentCards(client, bodyObject);
    }

    public ArrayList<Object> getActiveCards() {
        return activeCards;
    }
}
