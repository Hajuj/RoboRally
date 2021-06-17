package client.model;

import game.Element;
import game.Player;
import game.Robot;
import game.boardelements.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import json.JSONMessage;
import json.protocol.MapSelectedBody;
import json.protocol.PlayCardBody;
import json.protocol.SelectedCardBody;
import json.protocol.SetStartingPointBody;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientGameModel {

    private static ClientGameModel instance;
    private ClientModel clientModel = ClientModel.getInstance();


    protected PropertyChangeSupport propertyChangeSupport;

    private Player player;
    private ArrayList<ArrayList<ArrayList<Element>>> map;

    private ArrayList<String> cardsInHand = new ArrayList();
    private boolean handCards = false;

    private HashMap<Robot, Point2D> robotMap = new HashMap<>();

    private HashMap<Robot, Point2D> startingPointQueue = new HashMap<>();
    private boolean startingPoint = false;


    private ArrayList<String> lateCards = new ArrayList<>();
    private boolean latePlayer = false;
    private String lateCard = "";

    private HashMap<Robot, Point2D> moveQueue = new HashMap<>();
    private boolean queueMove = false;

    private HashMap<Robot, String> turningQueue = new HashMap<>();
    private boolean queueTurning = false;
    private BooleanProperty animType = new SimpleBooleanProperty(false);


    private boolean programmingPhase = false;

    private AtomicInteger actualRegister = new AtomicInteger(-1);

    private volatile int actualPlayerID;
    private volatile int actualPhase;

    private LinkedHashMap<Point2D, Antenna> antennaMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, CheckPoint> checkPointMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, ConveyorBelt> conveyorBeltMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, Empty> emptyMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, EnergySpace> energySpaceMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, Gear> gearMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, Laser> laserMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, Pit> pitMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, PushPanel> pushPanelMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, RestartPoint> restartPointMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, StartPoint> startPointMap = new LinkedHashMap<>();
    private LinkedHashMap<Point2D, Wall> wallMap = new LinkedHashMap<>();

    private SimpleBooleanProperty blueBeltAnimeProperty= new SimpleBooleanProperty(false);
    private SimpleBooleanProperty laserAnimeProperty = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty pushPanelProperty = new SimpleBooleanProperty(false);
    private boolean currentPlayer = false;
   ;


    //Singleton Zeug
    private ClientGameModel () {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public static ClientGameModel getInstance () {
        if (instance == null) {
            instance = new ClientGameModel();
        }
        return instance;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void sendStartingPoint (int x, int y) {
        JSONMessage startPointMessage = new JSONMessage("SetStartingPoint", new SetStartingPointBody(x, y));
        clientModel.sendMessage(startPointMessage);
    }
    public void setProgrammingPhase (boolean programmingPhase) {
        boolean progPhase = this.programmingPhase;
        this.programmingPhase = progPhase;
        if (this.programmingPhase) {
            propertyChangeSupport.firePropertyChange("ProgrammingPhase", progPhase, true);
        }

    }


    public void chooseMap (String mapName) {
        JSONMessage jsonMessage = new JSONMessage("MapSelected", new MapSelectedBody(mapName));
        clientModel.sendMessage(jsonMessage);
    }

    public void setanimationType (String animationType){
        switch (animationType){
            /*case "BlueConveyorBelt" ->{
                extractData(conveyorBeltMap);
                for (Map.Entry<Point2D,ConveyorBelt> entry:conveyorBeltMap.entrySet()) {
                    Point2D position =entry.getKey();
                    ConveyorBelt belt =entry.getValue();

                }
            }*/
            case "WallShooting" -> {
                animType.set(true);
            }
        }

    }
    public void extractData(LinkedHashMap elementMap){

    }
    public BooleanProperty getanimationType(){
        return animType;
    }




    public void sendPlayCard (String cardName) {
        JSONMessage playCard = new JSONMessage("PlayCard", new PlayCardBody(cardName));
        clientModel.sendMessage(playCard);
    }

    public void createMapObjects (ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) {
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                for (int i = 0; i < map.get(x).get(y).size(); i++) {
                    switch (map.get(x).get(y).get(i).getType()) {
                        case "Antenna" -> {
                            Element element = map.get(x).get(y).get(i);
                            Antenna antenna = new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            antennaMap.put(new Point2D(x, y), antenna);
                        }
                        case "ConveyorBelt" -> {
                            Element element = map.get(x).get(y).get(i);
                            ConveyorBelt conveyorBelt = new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations());
                            conveyorBeltMap.put(new Point2D(x, y), conveyorBelt);

                        }
                        case "CheckPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            CheckPoint checkPoint = new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount());
                            checkPointMap.put(new Point2D(x, y), checkPoint);
                        }
                        case "Empty" -> {
                            Element element = map.get(x).get(y).get(i);
                            Empty empty = new Empty(element.getType(), element.getIsOnBoard());
                            emptyMap.put(new Point2D(x, y), empty);
                        }
                        case "EnergySpace" -> {
                            Element element = map.get(x).get(y).get(i);
                            EnergySpace energySpace = new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount());
                            energySpaceMap.put(new Point2D(x, y), energySpace);
                        }
                        case "Gear" -> {
                            Element element = map.get(x).get(y).get(i);
                            Gear gear = new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            gearMap.put(new Point2D(x, y), gear);
                        }
                        case "Laser" -> {
                            Element element = map.get(x).get(y).get(i);
                            Laser laser = new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount());
                            laserMap.put(new Point2D(x, y), laser);
                        }
                        case "Pit" -> {
                            Element element = map.get(x).get(y).get(i);
                            Pit pit = new Pit(element.getType(), element.getIsOnBoard());
                            pitMap.put(new Point2D(x, y), pit);
                        }
                        case "PushPanel" -> {
                            Element element = map.get(x).get(y).get(i);
                            PushPanel pushPanel = new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters());
                            pushPanelMap.put(new Point2D(x, y), pushPanel);
                        }
                        case "RestartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            RestartPoint restartPoint = new RestartPoint(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            restartPointMap.put(new Point2D(x, y), restartPoint);
                        }
                        case "StartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            StartPoint startPoint = new StartPoint(element.getType(), element.getIsOnBoard());
                            startPointMap.put(new Point2D(x, y), startPoint);
                        }
                        case "Wall" -> {
                            Element element = map.get(x).get(y).get(i);
                            Wall wall = new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            wallMap.put(new Point2D(x, y), wall);
                        }
                        default -> { //place for exception handling
                        }
                    }
                }
            }
        }
    }

   /* public void setActualPlayerID (int actualPlayerID) {
        int currentPlayer = this.actualPlayerID;
        this.actualPlayerID = actualPlayerID;
        propertyChangeSupport.firePropertyChange("yourTurn", currentPlayer, actualPlayerID);

    }*/

    public void setActualPlayerID (int actualPlayerID){
        this.actualPlayerID=actualPlayerID;
    }

    public int getActualPlayerID() {
        return actualPlayerID;
    }

    public int getActualPhase () {
        return actualPhase;
    }

 /*   public void setActualPhase (int actualPhase) {
        this.actualPhase = actualPhase;

    }*/

    public boolean isProgrammingPhase() {
        return programmingPhase;
    }

    /*public void setProgrammingPhase(boolean programmingPhase) {
        this.programmingPhase = programmingPhase;
    }*/

    public LinkedHashMap<Point2D, Antenna> getAntennaMap () {
        return antennaMap;
    }

    public LinkedHashMap<Point2D, CheckPoint> getCheckPointMap () {
        return checkPointMap;
    }

    public LinkedHashMap<Point2D, ConveyorBelt> getConveyorBeltMap () {
        return conveyorBeltMap;
    }

    public LinkedHashMap<Point2D, Empty> getEmptyMap () {
        return emptyMap;
    }

    public LinkedHashMap<Point2D, EnergySpace> getEnergySpaceMap () {
        return energySpaceMap;
    }

    public LinkedHashMap<Point2D, Gear> getGearMap () {
        return gearMap;
    }

    public LinkedHashMap<Point2D, Laser> getLaserMap () {
        return laserMap;
    }

    public LinkedHashMap<Point2D, Pit> getPitMap () {
        return pitMap;
    }

    public LinkedHashMap<Point2D, PushPanel> getPushPanelMap () {
        return pushPanelMap;
    }

    public LinkedHashMap<Point2D, RestartPoint> getRestartPointMap () {
        return restartPointMap;
    }

    public LinkedHashMap<Point2D, StartPoint> getStartPointMap () {
        return startPointMap;
    }

    public LinkedHashMap<Point2D, Wall> getWallMap () {
        return wallMap;
    }

    public HashMap<Robot, String> getTurningQueue () {
        return turningQueue;
    }

    public void setQueueTurning(boolean queueTurning) {
        boolean oldQueueTurning = this.queueTurning;
        this.queueTurning = queueTurning;
        if (this.queueTurning) {
            propertyChangeSupport.firePropertyChange("queueTurning", oldQueueTurning, true);
        }
    }



    public int getValueActualRegister() {
        return actualRegister.get();
    }

    public void setActualRegister (int value) {
        while (true) {
            int existingValue = getValueActualRegister();
            if (actualRegister.compareAndSet(existingValue, value)) {
                propertyChangeSupport.firePropertyChange("currentRegister", existingValue, value);
                return;
            }
        }
    }


    public ArrayList<String> getLateCards () {
        return lateCards;
    }

    public void setLateCards (ArrayList<String> lateCards) {
        this.lateCards = lateCards;
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
  /*  public void setLateCard(String card) {
        this.lateCard = card;
    }*/
    public String getLateCard (){
        return this.lateCard;
    }

    public void setLatePlayers(boolean late){
        boolean latePlayers = this.latePlayer;
        this.latePlayer = late;
        if (this.latePlayer){
            propertyChangeSupport.firePropertyChange("Losers", latePlayers, true);
        }
    }

    public void setLateCard(String card){
        String newCard = this.lateCard;
        this.lateCard = card;
        propertyChangeSupport.firePropertyChange("blindCards", newCard,card);
    }


    public void setHandCards(boolean handCards) {
        boolean oldHandCards = this.handCards;
        this.handCards = handCards;
        if (this.handCards) {
            propertyChangeSupport.firePropertyChange("handCards", oldHandCards, true);
        }
    }

    public void setActualPhase(int phase){
        int currentPhase = this.actualPhase;
        this.actualPhase = phase;
        if (this.actualPhase == 2) {
            propertyChangeSupport.firePropertyChange("ProgrammingPhase", currentPhase, actualPhase);
        }
        if(this.actualPhase==3){
            propertyChangeSupport.firePropertyChange("ActivePhase", currentPhase, actualPhase);

        }

    }

    public HashMap<Robot, Point2D> getRobotMap () {
        return robotMap;
    }

    public void sendSelectedCards (int registerNum, String cardName) {
        JSONMessage jsonMessage = new JSONMessage("SelectedCard", new SelectedCardBody(cardName, registerNum + 1));
        clientModel.sendMessage(jsonMessage);
    }


    public HashMap<Robot, Point2D> getStartingPointQueue () {
        return startingPointQueue;
    }

    public void setStartingPoint(boolean startingPoint) {
        boolean oldStartingPoint = this.startingPoint;
        this.startingPoint = startingPoint;
        if (this.startingPoint) {
            propertyChangeSupport.firePropertyChange("startingPoint", oldStartingPoint, true);
        }
    }

    public HashMap<Robot, Point2D> getMoveQueue () {
        return moveQueue;
    }

    public void setQueueMove(boolean queueMove) {
        boolean oldQueueMove = this.queueMove;
        this.queueMove = queueMove;
        if (this.queueMove) {
            propertyChangeSupport.firePropertyChange("queueMove", oldQueueMove, true);
        }
    }

//Animation Active BooleanWerte
    public void activateBlueBeltAnime(boolean b) {
        this.blueBeltAnimeProperty.set(b);
    }

    public SimpleBooleanProperty blueBeltAnimePropertyProperty() {
        return blueBeltAnimeProperty;
    }

    public void switchPlayer(boolean currentPlayer) {
        boolean oldPlayer = this.currentPlayer;
        this.currentPlayer = currentPlayer;

        propertyChangeSupport.firePropertyChange("yourTurn", oldPlayer, true);

    }

}
