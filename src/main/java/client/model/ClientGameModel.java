package client.model;

import game.Element;
import game.Player;
import game.Robot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import json.JSONMessage;
import json.protocol.PlayCardBody;
import json.protocol.SelectedCardBody;
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

    private HashMap<Robot, Point2D> startingPointQueue = new HashMap<>();
    private ObservableMap<Robot, Point2D> startingPointQueueObservable = FXCollections.observableMap(startingPointQueue);


    private HashMap<Robot, Point2D> moveQueue = new HashMap<>();
    private ObservableMap<Robot, Point2D> moveQueueObservable = FXCollections.observableMap(moveQueue);

    private BooleanProperty canMove = new SimpleBooleanProperty(false);


    //TODO: Observer hier
    private BooleanProperty canSetStartingPoint = new SimpleBooleanProperty(false);
    private BooleanProperty programmingPhaseProperty = new SimpleBooleanProperty(false);
    private IntegerProperty actualPlayerTurn = new SimpleIntegerProperty(0);


    private IntegerProperty actualRegisterProperty = new SimpleIntegerProperty();

    private int actualRegister = -1;
    private volatile int actualPlayerID;
    private volatile int actualPhase;


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

    public int getActualPlayerID () {
        return actualPlayerID;
    }

    public IntegerProperty actualPlayerTurnProperty() {
        return actualPlayerTurn;
    }

    public void setActualPlayerTurn(int actualPlayerTurn) {
        this.actualPlayerTurn.set(actualPlayerTurn);
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

    public void setProgrammingPhase (boolean b) {
        /* if (this.actualPhase == 2)*/
        this.programmingPhaseProperty.set(b);

    }


    public int getActualRegisterProperty () {
        return actualRegisterProperty.get();
    }

    public IntegerProperty actualRegisterPropertyProperty () {
        return actualRegisterProperty;
    }

    public void setActualRegisterProperty (int actualRegisterProperty) {
        this.actualRegisterProperty.set(actualRegisterProperty);
    }

    public boolean isCanMove () {
        return canMove.get();
    }

    public BooleanProperty canMoveProperty () {
        return canMove;
    }

    public void setCanMove (boolean canMove) {
        this.canMove.set(canMove);
    }

    public BooleanProperty getProgrammingPhaseProperty () {
        return programmingPhaseProperty;
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

    public ObservableMap<Robot, Point2D> getStartingPointQueueObservable () {
        return startingPointQueueObservable;
    }

    public void setStartingPointQueueObservable (ObservableMap<Robot, Point2D> startingPointQueueObservable) {
        this.startingPointQueueObservable = startingPointQueueObservable;
    }

    public void sendSelectedCards (int registerNum, String cardName) {
        JSONMessage jsonMessage = new JSONMessage("SelectedCard", new SelectedCardBody(cardName, registerNum + 1));
        clientModel.sendMessage(jsonMessage);
    }

    public int getActualRegister() {
        return actualRegister;
    }

    public void setActualRegister(int actualRegister) {
        this.actualRegister = actualRegister;
        this.actualRegisterPropertyProperty().setValue(actualRegister);
    }

    public HashMap<Robot, Point2D> getStartingPointQueue () {
        return startingPointQueue;
    }

    public void setStartingPointQueue (HashMap<Robot, Point2D> startingPointQueue) {
        this.startingPointQueue = startingPointQueue;
    }

    public HashMap<Robot, Point2D> getMoveQueue () {
        return moveQueue;
    }

    public void setMoveQueue (HashMap<Robot, Point2D> moveQueue) {
        this.moveQueue = moveQueue;
    }

    public ObservableMap<Robot, Point2D> getMoveQueueObservable () {
        return moveQueueObservable;
    }

    public void setMoveQueueObservable (ObservableMap<Robot, Point2D> moveQueueObservable) {
        this.moveQueueObservable = moveQueueObservable;
    }
}
