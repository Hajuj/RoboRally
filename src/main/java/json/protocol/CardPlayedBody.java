package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author altug
 */
public class CardPlayedBody implements ServerMessageAction<CardPlayedBody> {

    @Expose
    private String card;
    @Expose
    private int clientID;

    public CardPlayedBody(int clientID ,String card) {

        this.card = card;
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(ClientModel clientModel, CardPlayedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleCardPlayed(clientModel, bodyObject);
    }

    public String getCard(){return card;}

    public int getClientID(){return clientID;}
}
