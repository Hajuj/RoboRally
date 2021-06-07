package client.model;

import game.Element;
import game.Player;
import game.Robot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import json.JSONMessage;
import json.protocol.SetStartingPointBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientGameModel {
    private static ClientGameModel instance;
    private ClientModel clientModel = ClientModel.getInstance();
    private Player player;
    private ArrayList<ArrayList<ArrayList<Element>>> map;
    private ArrayList<String> cardsInHand = new ArrayList();
    private ObservableList<String> cardsInHandObservable = FXCollections.observableList(cardsInHand);
    private HashMap<Robot, Point2D> robotMap = new HashMap<>();
    private ObservableMap<Robot, Point2D> robotMapObservable = FXCollections.observableMap(robotMap);


    //TODO: Observer hier
    private BooleanProperty canSetStartingPoint = new SimpleBooleanProperty(false);

    //Das ist so falsch oh gott
    private int x;
    private int y;
    private int actualPlayerID;
    private int actualPhase;


    //Singleton Zeug
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

    public ArrayList<String> getCardsInHand () {
        return cardsInHand;
    }

    public void setCardsInHand (ArrayList<String> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    public ObservableList getCardsInHandObservable () {
        return cardsInHandObservable;
    }

    public void setCardsInHandObservable (ObservableList cardsInHandObservable) {
        this.cardsInHandObservable = cardsInHandObservable;
    }

    public HashMap<Robot, Point2D> getRobotMap () {
        return robotMap;
    }


    public ObservableMap<Robot, Point2D> getRobotMapObservable () {
        return robotMapObservable;
    }

    public void setRobotMapObservable (ObservableMap<Robot, Point2D> robotMapObservable) {
        this.robotMapObservable = robotMapObservable;
    }
}
