package json.protocol;

import com.google.gson.annotations.Expose;
import json.MessageHandler;
import server.ClientHandler;
import server.Server;

/**
 * @author altug
 */

public class CardPlayedBody implements ServerMessageAction<CardPlayedBody> {

    @Expose
    private String card;
    @Expose
    private int clientID;
    @Expose
    private int playerID;

    public cardPlayedBody() {

        this.card = card;
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, CardPlayedBody cardPlayedBody, MessageHandler messageHandler) {
        messageHandler.handleCardPlayed(server, clientHandler, CardPlayedBody);
    }

    public String getCard(){return card;}

    public int clientID(){return clientID;}
}