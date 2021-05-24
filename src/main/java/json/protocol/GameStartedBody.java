package json.protocol;

import client.Client;
import client.ClientThread;
import com.google.gson.annotations.Expose;
import game.Element;
import json.MessageHandler;

import java.util.ArrayList;

public class GameStartedBody implements ServerMessageAction<GameStartedBody>{

    @Expose
    private ArrayList<ArrayList<ArrayList<Element>>> gameMap;

    public GameStartedBody(ArrayList<ArrayList<ArrayList<Element>>> map){
        this.gameMap = map;
    }

    @Override
    public void triggerAction(Client client, ClientThread task, GameStartedBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleGameStarted(client, task, bodyObject);
    }

    public ArrayList<ArrayList<ArrayList<Element>>> getGameMap() {
        return gameMap;
    }
}
