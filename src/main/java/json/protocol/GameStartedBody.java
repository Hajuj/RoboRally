package json.protocol;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import com.google.gson.annotations.Expose;
import game.Element;
import json.MessageHandler;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class GameStartedBody implements ServerMessageAction<GameStartedBody> {

    @Expose
    private ArrayList<ArrayList<ArrayList<Element>>> gameMap;

    public GameStartedBody (ArrayList<ArrayList<ArrayList<Element>>> map) {
        this.gameMap = map;
    }

    @Override
    public void triggerAction (ClientModel client, ClientModelReaderThread readerThread, GameStartedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleGameStarted(client, readerThread, bodyObject);
    }

    public ArrayList<ArrayList<ArrayList<Element>>> getGameMap () {
        return gameMap;
    }
}
