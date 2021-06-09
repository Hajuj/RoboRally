package client.model;

import game.Element;
import game.Game;
import game.Player;
import game.Robot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import json.JSONMessage;
import json.protocol.SelectedCardBody;
import json.protocol.SetStartingPointBody;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientGameModel {
    private static ClientGameModel instance;
    private ClientModel clientModel = ClientModel.getInstance();

    private Player player;
    private ArrayList<ArrayList<ArrayList<Element>>> map;

    private ArrayList<String> cardsInHand = new ArrayList();
    private ObservableList<String> cardsInHandObservable = FXCollections.observableList(cardsInHand);

    private Point2D oldPosition;
    private HashMap<Robot, Point2D> robotMap = new HashMap<>();
    private ObservableMap<Robot, Point2D> robotMapObservable = FXCollections.observableMap(robotMap);

    private BooleanProperty canMove = new SimpleBooleanProperty(false);


    private BooleanProperty programmingPhaseProperty = new SimpleBooleanProperty(false);

    private int actuellRegister = 0;

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

    public void setProgrammingPhase (boolean b) {
        /* if (this.actualPhase == 2)*/
        this.programmingPhaseProperty.set(b);

    }


    public Point2D getOldPosition () {
        return oldPosition;
    }

    public void saveOldPosition () {
        for (HashMap.Entry<Robot, Point2D> entry : getRobotMapObservable().entrySet()) {
            if (entry.getKey().getName().equals(Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(getActualPlayerID())))) {
                this.oldPosition = new Point2D((int) entry.getValue().getX(), (int) entry.getValue().getY());
                break;
            }
        }
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


    public ObservableMap<Robot, Point2D> getRobotMapObservable () {
        return robotMapObservable;
    }

    public void setRobotMapObservable (ObservableMap<Robot, Point2D> robotMapObservable) {
        this.robotMapObservable = robotMapObservable;
    }

    public void sendSelectedCards (int registerNum, String cardName) {
        JSONMessage jsonMessage = new JSONMessage("SelectedCard", new SelectedCardBody(cardName, registerNum + 1));
        clientModel.sendMessage(jsonMessage);
    }

    public int getActuellRegister () {
        return actuellRegister;
    }

    public void setActuellRegister (int actuellRegister) {
        this.actuellRegister = actuellRegister;
    }
}
