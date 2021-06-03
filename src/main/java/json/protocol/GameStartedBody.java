package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class GameStartedBody implements ServerMessageAction<GameStartedBody> {

    @Expose
    private final ArrayList<ArrayList<ArrayList<Element>>> gameMap;

    public GameStartedBody (ArrayList<ArrayList<ArrayList<Element>>> map) {
        this.gameMap = map;
    }

    @Override
    public void triggerAction (ClientModel client, GameStartedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleGameStarted(client, bodyObject);
    }

    public ArrayList<ArrayList<ArrayList<Element>>> getGameMap () {
        return gameMap;
    }
}
