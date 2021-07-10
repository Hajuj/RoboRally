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
import json.protocol.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientGameModel {

    private static ClientGameModel instance;
    private ClientModel clientModel = ClientModel.getInstance();


    protected PropertyChangeSupport propertyChangeSupport;

    private Player player;
    private ArrayList<ArrayList<ArrayList<Element>>> map;

    private ArrayList<String> refillShopCards = new ArrayList<>();
    private ArrayList<String> exchangeShopCards = new ArrayList<>();

    private ArrayList<String> cardsInHand = new ArrayList<>();
    private boolean handCards = false;

    private int energy = 0;

    private HashMap<Robot, Point2D> robotMap = new HashMap<>();

    private HashMap<Robot, Point2D> startingPointQueue = new HashMap<>();
    private boolean startingPoint = false;


    private ArrayList<String> lateCards = new ArrayList<>();
    private boolean latePlayer = false;
    private String lateCard = "";

    private ArrayList<MoveTask> moveQueue = new ArrayList<MoveTask>();
    private boolean queueMove = false;


    private ArrayList<MoveCPTask> moveCPQueue = new ArrayList<MoveCPTask>();
    private boolean queueCPMove = false;

    private ArrayList<TurnTask> turningQueue = new ArrayList<TurnTask>();
    private boolean queueTurning = false;
    private BooleanProperty animType = new SimpleBooleanProperty(false);

    private boolean animateGears = false;

    private boolean moveCheckpoints = false;

    private boolean programmingPhase = false;

    private boolean chooseRebootDirection = false;
    private AtomicInteger actualRegister = new AtomicInteger(-1);

    private volatile int actualPlayerID;
    private volatile int actualPhase;

    private int damageCount = 0;

    private Map<Point2D, CheckPoint> checkPointMovedMap = new HashMap<>();
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
    private boolean currentPlayer;



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

    public void refreshModel() {
        queueMove = false;
        handCards = false;
        latePlayer = false;
        animateGears = false;
        queueTurning = false;
        startingPoint = false;
        programmingPhase = false;
        chooseRebootDirection = false;
        energy = 0;
        damageCount = 0;
        lateCard = "";

        robotMap = new HashMap<>();
        moveQueue = new ArrayList<MoveTask>();
        lateCards = new ArrayList<>();
        turningQueue = new ArrayList<TurnTask>();
        cardsInHand = new ArrayList<>();
        startingPointQueue = new HashMap<>();

        pitMap = new LinkedHashMap<>();
        gearMap = new LinkedHashMap<>();
        wallMap = new LinkedHashMap<>();
        laserMap = new LinkedHashMap<>();
        emptyMap = new LinkedHashMap<>();
        antennaMap = new LinkedHashMap<>();
        pushPanelMap = new LinkedHashMap<>();
        checkPointMap = new LinkedHashMap<>();
        startPointMap = new LinkedHashMap<>();
        energySpaceMap = new LinkedHashMap<>();
        conveyorBeltMap = new LinkedHashMap<>();
        restartPointMap = new LinkedHashMap<>();

        animType = new SimpleBooleanProperty(false);
        pushPanelProperty = new SimpleBooleanProperty(false);
        laserAnimeProperty = new SimpleBooleanProperty(false);
        blueBeltAnimeProperty= new SimpleBooleanProperty(false);
        actualRegister = new AtomicInteger(-1);

        clientModel.setAvailableMaps(new ArrayList<String>());
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

    public void sendSelectedDamage (ArrayList<String> damageList) {
        JSONMessage jsonMessage = new JSONMessage("SelectedDamage", new SelectedDamageBody(damageList));
        clientModel.sendMessage(jsonMessage);
    }

    public void chooseMap (String mapName) {
        JSONMessage jsonMessage = new JSONMessage("MapSelected", new MapSelectedBody(mapName));
        clientModel.sendMessage(jsonMessage);
    }

    public void sendRebootDirection (String direction) {
        JSONMessage rebootDirection = new JSONMessage("RebootDirection", new RebootDirectionBody(direction));
        clientModel.sendMessage(rebootDirection);
    }


    public void activateAdminPrivilege (int register) {
        JSONMessage adminMessage = new JSONMessage("ChooseRegister", new ChooseRegisterBody(register));
        clientModel.sendMessage(adminMessage);
    }

    public void setanimationType (String animationType) {
        switch (animationType) {
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

    public boolean isCurrentPlayer() {
        return currentPlayer;
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

    public void removeElementFromMap (Element element, int x, int y) {
        for (int i = 0; i < map.get(x).get(y).size(); i++) {
            if (element.getType().equals(map.get(x).get(y).get(i).getType())) {
                map.get(x).get(y).remove(i);
                break;
            }
        }
    }

    public void placeElementOnMap (Element element, int x, int y) {
        map.get(x).get(y).add(element);
    }

   /* public void setActualPlayerID (int actualPlayerID) {
        int currentPlayer = this.actualPlayerID;
        this.actualPlayerID = actualPlayerID;
        propertyChangeSupport.firePropertyChange("yourTurn", currentPlayer, actualPlayerID);

    }*/


    public void activateSpamBlocker () {
        sendPlayCard("SpamBlocker");
    }

    public void setActualPlayerID (int actualPlayerID) {
        this.actualPlayerID = actualPlayerID;
    }

    public int getActualPlayerID () {
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

    public ArrayList<TurnTask> getTurningQueue () {
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
        propertyChangeSupport.firePropertyChange("ActualPhase", currentPhase, phase);

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

    public ArrayList<MoveTask> getMoveQueue () {
        return moveQueue;
    }

    public synchronized void setQueueMove (boolean queueMove) {
        boolean oldQueueMove = this.queueMove;
        this.queueMove = queueMove;
        if (this.queueMove) {
            propertyChangeSupport.firePropertyChange("queueMove", oldQueueMove, true);
        }
    }


    public void setCheckpointPositionByID (int checkpointID, Point2D newPosition) {
        Point2D oldPosition = getCheckpointPositionByID(checkpointID);
        CheckPoint checkPoint = checkPointMap.get(oldPosition);
        checkPointMap.remove(oldPosition);
        checkPointMap.put(newPosition, checkPoint);
    }


    public Point2D getCheckpointPositionByID (int checkpointID) {
        for (Map.Entry<Point2D, CheckPoint> entry : checkPointMap.entrySet()) {
            if (entry.getValue().getCount() == checkpointID) {
                return entry.getKey();
            }
        }
        return null;
    }


    public ArrayList<Robot> getRobotsOnFields (Point2D position) {
        ArrayList<Robot> robotsOnFields = new ArrayList<>();
        for (Map.Entry<Robot, Point2D> entry : robotMap.entrySet()) {
            if (entry.getValue().equals(position)) {
                robotsOnFields.add(entry.getKey());
            }
        }
        return robotsOnFields;
    }


    public int getAntennaOrientation () {
        for (Map.Entry<Point2D, Antenna> entry : antennaMap.entrySet()) {
            if (entry.getValue().getOrientations().contains("left")) {
                return 90;
            } else if (entry.getValue().getOrientations().contains("right")) {
                return -90;
            }
        }
        return 0;
    }


    public boolean isRobotOnField (Point2D position) {
        for (Map.Entry<Robot, Point2D> entry : robotMap.entrySet()) {
            if (position.getX() == entry.getValue().getX() && position.getY() == entry.getValue().getY()) {
                return true;
            }
        }
        return false;
    }

    //Animation Active BooleanWerte
    public void activateBlueBeltAnime (boolean b) {
        this.blueBeltAnimeProperty.set(b);
    }

    public SimpleBooleanProperty blueBeltAnimePropertyProperty () {
        return blueBeltAnimeProperty;
    }

    public void switchPlayer(boolean currentPlayer) {
        boolean oldPlayer = this.currentPlayer;
        this.currentPlayer = currentPlayer;

        propertyChangeSupport.firePropertyChange("yourTurn", oldPlayer, currentPlayer);
    }
    public int getEnergy () {
        return energy;
    }

    public void setEnergy (int energy) {
        this.energy = energy;
    }

    public void setAnimateGears (boolean animateGears) {
        boolean oldValue = this.animateGears;
        this.animateGears = animateGears;
        if (this.animateGears) {
            propertyChangeSupport.firePropertyChange("Gears", oldValue, true);
        }
    }

    public int getDamageCount () {
        return damageCount;
    }

    public void setDamageCount (int damageCount) {
        int oldValue = this.damageCount;
        this.damageCount = damageCount;
        if (!clientModel.isAI() && damageCount != 0) {
            propertyChangeSupport.firePropertyChange("PickDamage", oldValue, damageCount);
        }
    }

    public void setChooseRebootDirection (boolean chooseRebootDirection) {
        boolean oldValue = this.chooseRebootDirection;
        this.chooseRebootDirection = chooseRebootDirection;
        if (this.chooseRebootDirection) {
            propertyChangeSupport.firePropertyChange("RebootDirection", oldValue, true);
        }
    }

    public ArrayList<String> getRefillShopCards() {
        return refillShopCards;
    }

    public ArrayList<String> getExchangeShopCards() {
        return exchangeShopCards;
    }


    public static class TurnTask {
        private int playerID;
        private String rotation;

        public TurnTask (int playerID, String rotation) {
            this.playerID = playerID;
            this.rotation = rotation;
        }

        public int getplayerID () {
            return playerID;
        }

        public String getRotation () {
            return rotation;
        }
    }

    public static class MoveTask {
        private int playerID;
        private Point2D newPosition;

        public MoveTask (int playerID, Point2D newPosition) {
            this.playerID = playerID;
            this.newPosition = newPosition;
        }

        public int getPlayerID () {
            return playerID;
        }

        public Point2D getNewPosition () {
            return newPosition;
        }
    }


    public static class MoveCPTask {
        private int numCP;
        private Point2D newPosition;

        public MoveCPTask (int numCP, Point2D newPosition) {
            this.numCP = numCP;
            this.newPosition = newPosition;
        }

        public int getnumCP () {
            return numCP;
        }

        public Point2D getNewPosition () {
            return newPosition;
        }
    }


    public ArrayList<MoveCPTask> getMoveCPQueue () {
        return moveCPQueue;
    }

    public boolean isQueueCPMove () {
        return queueCPMove;
    }

    public void setQueueCPMove (boolean queueCPMove) {
        boolean oldQueueCPMove = this.queueCPMove;
        this.queueCPMove = queueCPMove;
        if (this.queueCPMove) {
            propertyChangeSupport.firePropertyChange("oldQueueCPMove", oldQueueCPMove, true);
        }
    }
}
