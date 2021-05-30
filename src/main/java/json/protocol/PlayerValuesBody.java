package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;

import server.ClientHandler;
import server.Server;

import json.MessageHandler;

import com.google.gson.annotations.Expose;

/*
{
   "messageType": "PlayerValues",
   "messageBody": {
       "name": "Nr. 5",
       "figure": 5
   }
}

 */

/**
 * @author Viktoria
 */
public class PlayerValuesBody implements ClientMessageAction<PlayerValuesBody> {
    @Expose
    private final String name;
    @Expose
    private final int figure;

    public String getName() {
        return name;
    }

    public int getFigure() {
        return figure;
    }

    public PlayerValuesBody(String name, int figure) {
        this.name = name;
        this.figure = figure;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, PlayerValuesBody playerValuesBody, MessageHandler messageHandler) {
        messageHandler.handlePlayerValues(server, clientHandler, playerValuesBody);

    }

}
