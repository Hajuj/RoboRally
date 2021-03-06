package client.model;

import game.Element;
import game.Player;
import game.Robot;
import game.boardelements.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import json.JSONMessage;
import json.protocol.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Viktoria, Mohamad
 */
public class ClientGameModel {

    private static ClientGameModel instance;
    private ClientModel clientModel = ClientModel.getInstance();


    protected PropertyChangeSupport propertyChangeSupport;

    private Player player;
    private ArrayList<ArrayList<ArrayList<Element>>> map;

    private ArrayList<String> refillShopCards = new ArrayList<>();
    private ArrayList<String> exchangeShopCards = new ArrayList<>();
    private ArrayList<String> boughtCards = new ArrayList<>();

    private ArrayList<String> cardsInHand = new ArrayList<>();
    private ArrayList<String> upgradeCards = new ArrayList<>();
    private boolean handCards = false;

    private int energy = 5;

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
    private boolean animateBelts = false;
    private boolean animateGears = false;
    private boolean animateSpaces = false;

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
    private boolean gameFinished = false;
    private boolean rebooting = false;
    private boolean refillShop = false;
    private boolean isBuying = false;
    private boolean backShooting = false;
    private String boughtCard;
    private int choosenRegister;
    private ArrayList<String> returnedCards;
    private boolean isReturning= false;
    private boolean MemorySwapOnPlay= false;
    private boolean timer= false;


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
        rebootSetting ( true );
        clientModel.sendMessage(rebootDirection);
    }


    public void activateAdminPrivilege (int register) {
        JSONMessage adminMessage = new JSONMessage("ChooseRegister", new ChooseRegisterBody(register));
        clientModel.sendMessage(adminMessage);
    }


    public boolean getRebootSetting (){
        return rebooting;
    }
    public void setRebootingSetting(boolean b){
        this.rebooting = b;
    }


    public boolean isCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Methode to send the played card in a PlayCard- Message to the clientModel
     * which will send the message to the server to be handled
     *
     * @param cardName string vlaue of the played-card name
     * **/
    public void sendPlayCard (String cardName) {
        JSONMessage playCard = new JSONMessage("PlayCard", new PlayCardBody(cardName));
        clientModel.sendMessage(playCard);
    }

    /**
     * Methode that sends choosen cards by the player after placing them in registers
     * in a  SelectedCard- Message to the clientModel
     * which will send the message to the server to be handled
     * @param cardName string vlaue of the played-card name
     * @param registerNum is an int value of the current register
     * **/
    public void sendSelectedCards (int registerNum, String cardName) {
        JSONMessage jsonMessage = new JSONMessage("SelectedCard", new SelectedCardBody(cardName, registerNum + 1));
        clientModel.sendMessage(jsonMessage);
    }
    /**
     * Methode that sends the choosen return-cards by the player after playing
     * the upgrade Card MemorySwap. Then send it in ReturnCards Message
     * @param allReturnedCardsL an ArrayList of strings of all 3 returned choosen-cards
     **/
    public void sendRetrunCards(ArrayList<String> allReturnedCardsL) {
        this.returnedCards = allReturnedCardsL;
        JSONMessage returnCardsMessage = new JSONMessage("ReturnCards", new ReturnCardsBody (allReturnedCardsL));
        clientModel.sendMessage(returnCardsMessage);
    }


    /**
     * Methode to set Map Objects with their Type, isOnBoard and Orientation and put it
     * in a Hashmap for each Element in order to save their Location and ElementTyp in a Hashmap
     * this will serve the purpose of easy accessing to every map-element and its properties
     * @param map is a 3D ArrayList of the jsonMap
     * @param mapX is the x Demension of the Map
     * @param mapY is the y Demension of the Map
     * **/
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

    public void removeElementFromMap(Element element, int x, int y) {
        for(int i = 0; i < map.get(x).get(y).size(); i++) {
            if(element.getType().equals(map.get(x).get(y).get(i).getType())) {
                map.get(x).get(y).remove(i);
                break;
            }
        }
    }

    public void placeElementOnMap (Element element, int x, int y) {
        map.get(x).get(y).add(element);
    }
    /**
     * Methode that check if its allowed to buy an UpgradeCard using the MEthode checkAllowtoBuy
     * and then send cardName to BuyUpgradeBody with a boolean value isBuying
     * whether the player is buying or not and the cardName of the upgradeCard
     * @param cardName is a String value of the Upgrade Card name
     * **/
    public void buyUpgradeCard (String cardName) {
        boolean isBuying = true;
        if (cardName.equals ( "Null" )){
            isBuying = false;
        }
        if (checkAllowToBuy(cardName)) {
            boughtCards.add(cardName);
            this.boughtCard= cardName;
        }
        JSONMessage buyMessage = new JSONMessage("BuyUpgrade", new BuyUpgradeBody(isBuying, cardName));
        clientModel.sendMessage(buyMessage);
    }


    /**
     * Methode to check if the Player is allowed to buy an Upgrade Card in case of having enough Energy
     * and not more than 3 of each permanent and Temporary Upgrade Cards
     * Depending on all mensioned condtions, the methode returns a boolean value if the player can
     * or cant buy the choosen Upgrad card
     * @param cardName a String Value of the choosen card name
     * @return boolean value gives the permission to buy the card or not
     * **/
    public boolean checkAllowToBuy (String cardName) {
        boolean allowToBuy = true;
        int numOfPermanent = Collections.frequency(boughtCards, "AdminPrivilege") + Collections.frequency(boughtCards, "RearLaser");
        int numOfTemporary = Collections.frequency(boughtCards, "MemorySwap") + Collections.frequency(boughtCards, "SpamBlocker");
        // ob er noch nicht 3 und 3 karten hat
        if (cardName.equals("Null")) {
            allowToBuy = false;
        } else {
            if (isPermanent(cardName) && numOfPermanent == 3) {
                allowToBuy = false;
            } else if ((!isPermanent(cardName)) && numOfTemporary == 3) {
                allowToBuy = false;

            }
        }
        int energyCost = getUpgradeCost(cardName);
        if (this.energy < energyCost) {
            allowToBuy = false;
        }
        if (allowToBuy) {
            this.energy = this.energy -energyCost;
        }
        return allowToBuy;
    }
    /**
     * Methode to lookup the price of player-choice of an Upgrade Card
     * @param cardName String value of the choosen card
     * @return int value of the cost of the choosen upgrade Card
     * **/
    public int getUpgradeCost (String cardName) {
        return switch (cardName) {
            case "AdminPrivilege", "SpamBlocker" -> 3;

            case "RearLaser" -> 2;
            case "MemorySwap" -> 1;
            default -> 0;

        };
    }
    /**
     *Methode to check if an upgrade Card is permanent
     * @param cardName string vlaue for the upgrade-card name
     * @return boolean value whether the given card permanent or not
     * **/
    public boolean isPermanent (String cardName) {
        return (cardName.equals("AdminPrivilege") || cardName.equals("RearLaser"));
    }


    /**
     * Methode to activate the upgrade-card SpamBlocker using sendPlayCard methode
     * **/
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


    public boolean isProgrammingPhase() {
        return programmingPhase;
    }

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
    public void setUpgradeCards (ArrayList<String> upgradeCards){
        this.upgradeCards = upgradeCards;
    }
    public ArrayList <String> getUpgradeCards(){return upgradeCards; }



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

    public void playMemorySwap(boolean b) {
        this.MemorySwapOnPlay = b;
        JSONMessage memorySwapMessage = new JSONMessage("PlayCard", new PlayCardBody ("MemorySwap"));
        clientModel.sendMessage(memorySwapMessage);
    }


    public void refillShop(boolean refill) {
        boolean oldShop = this.refillShop;
        this.refillShop = refill;
        if (this.refillShop) {
            propertyChangeSupport.firePropertyChange ( "refillShop", oldShop, true );
        }
    }

    public boolean isBuying() {
        return this.isBuying;
    }

    public void notBuying(boolean b ) {
        this.isBuying = b;
    }


    public void setActualPhase(int phase){
        int currentPhase = this.actualPhase;
        this.actualPhase = phase;
        propertyChangeSupport.firePropertyChange("ActualPhase", currentPhase, phase);

    }

    public HashMap<Robot, Point2D> getRobotMap () {
        return robotMap;
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

    /**
     * Method to get every Robot on a specific position.
     * Takes a position.
     *@param position  is the position that is checked
     *@return          every Robot on the position
     * **/
    public ArrayList<Robot> getRobotsOnFields (Point2D position) {
        ArrayList<Robot> robotsOnFields = new ArrayList<>();
        for (Map.Entry<Robot, Point2D> entry : robotMap.entrySet()) {
            if (entry.getValue().equals(position)) {
                robotsOnFields.add(entry.getKey());
            }
        }
        return robotsOnFields;
    }

    /**
     * Methode that retruns the angle depending on the orientation of the Antenna
     * @return  int value of the angle
     * **/
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

    /**
     * Methode that checks if a Robot is on a specific position
     * @param position a Point2D value the helds an x and y position as a tuple (x,y)
     * @return boolean value whether a robot is on the given position
     * **/
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

    public void setAnimateBelts (boolean belts) {
        boolean oldValue = this.animateBelts;
        this.animateBelts = belts;
        if (this.animateBelts) {
            propertyChangeSupport.firePropertyChange("BlueConveyorBelt", oldValue, true);
        }
    }

    public void setAnimateEnergySpaces (boolean spaces) {
        boolean oldValue = this.animateSpaces;
        this.animateSpaces = spaces;
        if (this.animateSpaces) {
            propertyChangeSupport.firePropertyChange("EnergySpaces", oldValue, true);
        }
    }


    public void rebootSetting (boolean b) {
        boolean oldValue = this.rebooting ;
        this.rebooting = b;
        if (this.rebooting) {
            propertyChangeSupport.firePropertyChange ( "rebootFinished", oldValue, true );
        }
    }


    public ArrayList<String> getBoughtCards () {
        return this.boughtCards;
    }

    public void finishBuyCard(boolean b) {
        boolean oldValue = this.isBuying ;
        this.isBuying = b;
        if (this.isBuying) {
            propertyChangeSupport.firePropertyChange ( "buyingCardFinished", oldValue, true );
        }
    }
    public void setTimer(boolean b) {
        boolean oldValue = this.timer ;
        this.timer = b;
        if (this.timer) {
            propertyChangeSupport.firePropertyChange ( "TimerStarted", oldValue, true );
        }
    }

    public void canBackShooting(boolean b) {
        this.backShooting= b;
    }



    public void finishRetunrCard(boolean b) {
        boolean oldValue = this.isReturning ;
        this.isReturning = b;
        if (this.isReturning) {
            propertyChangeSupport.firePropertyChange ( "returningFinished", oldValue, true );
        }
    }

    public ArrayList<String> getReturnedCards() {
        return this.returnedCards;
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

    public void setQueueCPMove(boolean queueCPMove) {
        boolean oldQueueCPMove = this.queueCPMove;
        this.queueCPMove = queueCPMove;
        if(this.queueCPMove) {
            propertyChangeSupport.firePropertyChange("oldQueueCPMove", oldQueueCPMove, true);
        }
    }


    public void setGameFinished(boolean gameFinished) {
        boolean oldGameFinished = this.gameFinished;
        this.gameFinished = gameFinished;
        if(this.gameFinished) {
            propertyChangeSupport.firePropertyChange("gameFinished", oldGameFinished, true);
        }
    }

}
