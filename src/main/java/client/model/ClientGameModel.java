package client.model;

import game.Element;
import game.Player;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import json.JSONMessage;
import json.protocol.SetStartingPointBody;

import java.util.ArrayList;

public class ClientGameModel {
    private static ClientGameModel instance;
    private ClientModel clientModel = ClientModel.getInstance();
    private Player player;
    private ArrayList<ArrayList<ArrayList<Element>>> map;


    //TODO: Observer hier
    private BooleanProperty canSetStartingPoint = new SimpleBooleanProperty(false);
    //Das ist so falsch oh gott
    private int x;
    private int y;
    private int actualPlayerID;
    private int actualPhase;

    //Singelton Zeug
    private ClientGameModel () {
    }

    public static ClientGameModel getInstance () {
        if (instance == null) {
            instance = new ClientGameModel();
        }
        return instance;
    }


    public void sendStartingPoint (int x, int y) {
        JSONMessage startPointMessage = new JSONMessage("SetStartingPoint", new SetStartingPointBody(x, y));
        clientModel.sendMessage(startPointMessage);
    }


    public boolean getCanSetStartingPoint () {
        return canSetStartingPoint.get();
    }

    public BooleanProperty canSetStartingPointProperty () {
        return canSetStartingPoint;
    }

    public void setCanSetStartingPoint (boolean canSetStartingPoint) {
        this.canSetStartingPoint.set(canSetStartingPoint);
    }

    public int getX () {
        return x;
    }

    public void setX (int x) {
        this.x = x;
    }

    public int getY () {
        return y;
    }

    public void setY (int y) {
        this.y = y;
    }

    public int getActualPlayerID () {
        return actualPlayerID;
    }

    public void setActualPlayerID (int actualPlayerID) {
        this.actualPlayerID = actualPlayerID;
    }

    public int getActualPhase () {
        return actualPhase;
    }

    public void setActualPhase (int actualPhase) {
        this.actualPhase = actualPhase;
    }

    public Player getPlayer () {
        return player;
    }

    public void setPlayer (Player player) {
        this.player = player;
    }

    public ArrayList<ArrayList<ArrayList<Element>>> getMap () {
        return map;
    }

    public void setMap (ArrayList<ArrayList<ArrayList<Element>>> map) {
        this.map = map;
    }
}
