package json.protocol;

import client.model.ClientModel;
import com.google.gson.annotations.Expose;
import json.MessageHandler;

/**
 * @author altug
 */

public class CardPlayedBody implements ServerMessageAction<CardPlayedBody> {

    @Expose
    private String card;
    @Expose
    private int clientID;

    public CardPlayedBody(clientID , card) {

        this.card = card;
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(ClientModel clientModel, CardPlayedBody cardPlayedBody, MessageHandler messageHandler) {
        messageHandler.handleCardPlayed(clientModel, cardPlayedBody);
    }

    public String getCard(){return card;}

    public int getClientID(){return clientID;}
}