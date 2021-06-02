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


    public PlayCardBody (String card) {
        this.card = card;

    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, PlayCardBody playCardBody, MessageHandler messageHandler) {
        messageHandler.handlePlayCard(server, playCardBody);

    }


    public String getCard() {
        return card;

        }

    }

