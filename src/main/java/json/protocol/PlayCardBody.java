package json.protocol;

import com.google.gson.annotations.Expose;
import json.MessageHandler;
import server.ClientHandler;
import server.Server;

/**
 * @author altug
 */

public class PlayCardBody implements ClientMessageAction<PlayCardBody> {

    @Expose
    private String card;
    @Expose
    private int playerID;
    @Expose
    private int clientID;

    public playCardBody (int clientID, int playerID, String card) {
        this.playerID = playerID;
        this.card = card;
        this.clientID = clientID;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, PlayCardBody playCardBody, MessageHandler messageHandler) {
        messageHandler.handlePlayCard(server, clientHandler, PlayerAddedBody);

    }


    public String getCard() {
        return card;

        }

        public int getPlayerID(){
        return playerID;
        }

        public int getClientID(){
        return clientID;
        }
    }

