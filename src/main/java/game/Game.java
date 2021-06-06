package game;

import game.boardelements.*;
import game.decks.DeckSpam;
import game.decks.DeckTrojan;
import game.decks.DeckVirus;
import game.decks.DeckWorm;
import javafx.geometry.Point2D;
import json.JSONDeserializer;
import json.JSONMessage;
import json.protocol.*;
import json.protocol.CurrentPlayerBody;
import server.Connection;
import server.Server;

import javafx.geometry.Point2D;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Ilja Knis
 */
public class Game {
    private static Game instance;

    private DeckSpam deckSpam;
    private DeckTrojan deckTrojan;
    private DeckVirus deckVirus;
    private DeckWorm deckWorm;
    private ArrayList<ArrayList<ArrayList<Element>>> map;
    private ArrayList<Player> playerList;
    private Server server;
    private ArrayList<String> availableMaps = new ArrayList<>();
    private static ArrayList<String> robotNames = new ArrayList<String>(Arrays.asList("Hulk X90", "Twonky", "Squash Bot", "Zoom Bot", "Twitch", "Spin Bot"));

    private Map<Point2D, Antenna> antennaMap = new HashMap<>();
    private Map<Point2D, CheckPoint> checkPointMap = new HashMap<>();
    private Map<Point2D, ConveyorBelt> conveyorBeltMap = new HashMap<>();
    private Map<Point2D, Empty> emptyMap = new HashMap<>();
    private Map<Point2D, EnergySpace> energySpaceMap = new HashMap<>();
    private Map<Point2D, Gear> gearMap = new HashMap<>();
    private Map<Point2D, Laser> laserMap = new HashMap<>();
    private Map<Point2D, Pit> pitMap = new HashMap<>();
    private Map<Point2D, PushPanel> pushPanelMap = new HashMap<>();
    private Map<Point2D, RestartPoint> restartPointMap = new HashMap<>();
    private Map<Point2D, StartPoint> startPointMap = new HashMap<>();
    private Map<Point2D, Wall> wallMap = new HashMap<>();
    private Map<Point2D, Robot> robotMap = new HashMap<>();

    private String mapName;
    private boolean gameOn;
    private int currentPlayer;
    private int activePhase;
    private int currentRound;
    private boolean activePhaseOn = false;
    private boolean timerOn = false;

    private Game() {

    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public Game (Server server) {
        this.server = server;
        availableMaps.add("DizzyHighway");
        availableMaps.add("One more map");
    }


    public void start (ArrayList<Player> players) throws IOException {

        this.deckSpam = new DeckSpam();
        this.deckSpam.initializeDeck();

        this.deckTrojan = new DeckTrojan();
        this.deckTrojan.initializeDeck();

        this.deckVirus = new DeckVirus();
        this.deckVirus.initializeDeck();

        this.deckWorm = new DeckWorm();
        this.deckWorm.initializeDeck();

        this.playerList = players;

        this.currentRound = 0;
        this.setActivePhase(0);
        this.setCurrentPlayer(playerList.get(0).getPlayerID());

        Collections.sort(playerList);

        //send an alle GameStartedMessage
        mapName = mapName.replaceAll("\\s+", "");
        String fileName = "Maps/" + mapName + ".json";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        String content = new String(Files.readAllBytes(file.toPath()));
        JSONMessage jsonMessage = JSONDeserializer.deserializeJSON(content);
        sendToAllPlayers(jsonMessage);

        informAboutActivePhase();
        informAboutCurrentPlayer();
    }

    public ArrayList<Integer> tooLateClients() {
        ArrayList<Integer> tooLateClients = new ArrayList<>();
        for (Player player : this.playerList) {
            if (!player.isRegisterFull()) {
                tooLateClients.add(player.getPlayerID());
            }
        }
        return tooLateClients;
    }

    public int nextPlayerID () {
        int currentIndex = playerList.indexOf(server.getPlayerWithID(currentPlayer));
        if (playerList.size() - 1 == currentIndex) {
            return -1;
        }
        return playerList.get(currentIndex + 1).getPlayerID();
    }

    public void informAboutActivePhase () {
        JSONMessage currentPhase = new JSONMessage("ActivePhase", new ActivePhaseBody(getActivePhase()));
        sendToAllPlayers(currentPhase);
    }

    public void informAboutCurrentPlayer () {
        JSONMessage currentPlayer = new JSONMessage("CurrentPlayer", new CurrentPlayerBody(getCurrentPlayer()));
        sendToAllPlayers(currentPlayer);
    }

    //TODO select map
    //     if (Player player:playerList) isAI -> pickRandomMap
    //     else playerList.get(0)... pickMap

    //TODO at least 2 players ready to start game (max 6)

    //TODO game logic with startGame()
    //     map creation with elements (deserialization)
    //     phases


    public void sendToAllPlayers (JSONMessage jsonMessage) {
        for (Player player : playerList) {
            server.sendMessage(jsonMessage, server.getConnectionWithID(player.getPlayerID()).getWriter());
        }
    }

    public void selectMap (String mapName) throws IOException {
        //TODO maybe try block instead of throws IOException
        this.mapName = mapName;
        mapName = mapName.replaceAll("\\s+", "");
        String fileName = "Maps/" + mapName + ".json";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        String content = new String(Files.readAllBytes(file.toPath()));
        JSONMessage jsonMessage = JSONDeserializer.deserializeJSON(content);
        GameStartedBody gameStartedBody = (GameStartedBody) jsonMessage.getMessageBody();
        this.map = gameStartedBody.getGameMap();
        int mapX = map.size();
        int mapY = map.get(0).size();
        createMapObjects(map, mapX, mapY);
    }

    private void createMapObjects(ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) {
        for (int y = 0; y < mapX; y++) {
            for (int x = 0; x < mapY; x++) {
                for (int i = 0; i < map.get(y).get(x).size(); i++) {
                    switch (map.get(y).get(x).get(i).getType()) {
                        case "Antenna" -> {
                            Element element = map.get(y).get(x).get(i);
                            Antenna antenna = new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, antenna);
                            antennaMap.put(new Point2D(x, y), antenna);
                        }
                        case "ConveyorBelt" -> {
                            Element element = map.get(y).get(x).get(i);
                            ConveyorBelt conveyorBelt = new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, conveyorBelt);
                            conveyorBeltMap.put(new Point2D(x, y), conveyorBelt);
                        }
                        case "CheckPoint" -> {
                            Element element = map.get(y).get(x).get(i);
                            CheckPoint checkPoint = new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount());
                            replaceElementInMap(map, x, y, element, checkPoint);
                            checkPointMap.put(new Point2D(x, y), checkPoint);
                        }
                        case "Empty" -> {
                            Element element = map.get(y).get(x).get(i);
                            Empty empty = new Empty(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, empty);
                            emptyMap.put(new Point2D(x, y), empty);
                        }
                        case "EnergySpace" -> {
                            Element element = map.get(y).get(x).get(i);
                            EnergySpace energySpace = new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount());
                            replaceElementInMap(map, x, y, element, energySpace);
                            energySpaceMap.put(new Point2D(x, y), energySpace);
                        }
                        case "Gear" -> {
                            Element element = map.get(y).get(x).get(i);
                            Gear gear = new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, gear);
                            gearMap.put(new Point2D(x, y), gear);
                        }
                        case "Laser" -> {
                            Element element = map.get(y).get(x).get(i);
                            Laser laser = new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount());
                            replaceElementInMap(map, x, y, element, laser);
                            laserMap.put(new Point2D(x, y), laser);
                        }
                        case "Pit" -> {
                            Element element = map.get(y).get(x).get(i);
                            Pit pit = new Pit(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, pit);
                            pitMap.put(new Point2D(x, y), pit);
                        }
                        case "PushPanel" -> {
                            Element element = map.get(y).get(x).get(i);
                            PushPanel pushPanel = new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters());
                            replaceElementInMap(map, x, y, element, pushPanel);
                            pushPanelMap.put(new Point2D(x, y), pushPanel);
                        }
                        case "RestartPoint" -> {
                            Element element = map.get(y).get(x).get(i);
                            RestartPoint restartPoint = new RestartPoint(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, restartPoint);
                            restartPointMap.put(new Point2D(x, y), restartPoint);
                        }
                        case "StartPoint" -> {
                            Element element = map.get(y).get(x).get(i);
                            StartPoint startPoint = new StartPoint(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, startPoint);
                            startPointMap.put(new Point2D(x, y), startPoint);
                        }
                        case "Wall" -> {
                            Element element = map.get(y).get(x).get(i);
                            Wall wall = new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, wall);
                            wallMap.put(new Point2D(x, y), wall);
                        }
                        default -> { //place for exception handling
                        }
                    }
                }
            }
        }
    }

    /*
    Antenna priority;
    Register 1:
    List<Player> turnOrder;
    for(int i = 0; i < playersList.size; i++){
        turnOrder[i].getPlayer.activateCardEffect();
    }
    triggerBoardElements();
        -> for(Element element : map.get(y).get(x))
               (ConveyorBelt, Laser, EnergySpace, CheckPoint).trigger();


     */


    //TODO messageBodies verwenden
    public void activateCardEffect(Card card){
        int indexCurrentPlayer = playerList.indexOf(server.getPlayerWithID(currentPlayer));
        String robotOrientation = playerList.get(indexCurrentPlayer).getRobot().getOrientation();

        switch(card.getCardName()){
            case "Again" -> {
                //aktuelles Register -> if 0 -> error
                //                      else player.getDeckRegister
            }
            case "BackUp" -> {
                switch(robotOrientation){
                    case "top" -> {
                        moveRobot(playerList.get(indexCurrentPlayer).getRobot(), "bottom", 1);
                    }
                    case "bottom" -> {
                        moveRobot(playerList.get(indexCurrentPlayer).getRobot(), "top", 1);
                    }
                    case "left" -> {
                        moveRobot(playerList.get(indexCurrentPlayer).getRobot(), "right", 1);
                    }
                    case "right" -> {
                        moveRobot(playerList.get(indexCurrentPlayer).getRobot(), "left", 1);
                    }
                }
            }
            case "MoveI" -> {
                moveRobot(playerList.get(indexCurrentPlayer).getRobot(), robotOrientation, 1);
            }
            case "MoveII" -> {
                moveRobot(playerList.get(indexCurrentPlayer).getRobot(), robotOrientation, 2);
            }
            case "MoveIII" -> {
                moveRobot(playerList.get(indexCurrentPlayer).getRobot(), robotOrientation, 3);
            }
            case "PowerUp" -> {
                playerList.get(indexCurrentPlayer).increaseEnergy(1);
            }
            case "TurnLeft" -> {
                changeOrientation(playerList.get(indexCurrentPlayer).getRobot(), "left");
            }
            case "TurnRight" -> {
                changeOrientation(playerList.get(indexCurrentPlayer).getRobot(), "right");
            }
            case "UTurn" -> {
                changeOrientation(playerList.get(indexCurrentPlayer).getRobot(), "uturn");
            }
            case "Spam" -> {}
            case "Trojan" -> {
                for(int i = 0; i < 2; i++) {
                    playerList.get(indexCurrentPlayer).getDeckDiscard().getDeck().add(deckSpam.getTopCard());
                    deckSpam.removeTopCard();
                }
                //TODO access current register and play top card from deckProgramming
            }
            case "Virus" -> {
                ArrayList<Player> playersWithinRadius = getPlayersInRadius(playerList.get(indexCurrentPlayer), 6);
                for(Player player : playersWithinRadius){
                    player.getDeckDiscard().getDeck().add(deckSpam.getTopCard());
                    deckSpam.removeTopCard();
                }
            }
            case "Worm" -> {
                for(int i = 0; i < 2; i++) {
                    playerList.get(indexCurrentPlayer).getDeckDiscard().getDeck().add(deckSpam.getTopCard());
                    deckSpam.removeTopCard();
                }

                //TODO: set Robot to RestartPoint
                //      cancel remaining registers
                //      discard remaining registers
            }
        }
    }

    public ArrayList<Player> getPlayersInRadius(Player currentPlayer, int radius){
        ArrayList<Player> playersInRadius = new ArrayList<>();
        int robotXPosition = currentPlayer.getRobot().getxPosition();
        int robotYPosition = currentPlayer.getRobot().getyPosition();
        int lowerXCap, upperXCap, lowerYCap, upperYCap;

        //Calculate boundaries within radius span
        if(robotXPosition - radius < 0){
            lowerXCap = 0;
        }
        else {
            lowerXCap = robotXPosition - radius;
        }
        if(robotYPosition - radius > 0){
            lowerYCap = 0;
        }
        else {
            lowerYCap = robotYPosition - radius;
        }
        if(robotXPosition + radius > map.get(0).size()){
            upperXCap = map.get(0).size();
        }
        else {
            upperXCap = robotXPosition + radius;
        }
        if(robotYPosition + radius > map.size()){
            upperYCap = map.size();
        }
        else {
            upperYCap = robotYPosition + radius;
        }

        //TODO what about the player that uses the virus card?
        for(Player player : playerList){
            int robotX = player.getRobot().getxPosition();
            int robotY = player.getRobot().getyPosition();
            if(robotX >= lowerXCap && robotX <= upperXCap && robotY >= lowerYCap && robotY <= upperYCap){
                playersInRadius.add(player);
            }
        }
        playersInRadius.remove(currentPlayer);

        return playersInRadius;
    }

    public void moveRobot(Robot robot, String orientation, int movement){
        int robotXPosition = robot.getxPosition();
        int robotYPosition = robot.getyPosition();
        switch (orientation){
            case "top" -> {
                robot.setyPosition(robotYPosition+movement);
            }
            case "bottom" -> {
                robot.setyPosition(robotYPosition-movement);
            }
            case "left" -> {
                robot.setxPosition(robotXPosition-movement);
            }
            case "right" -> {
                robot.setxPosition(robotXPosition+movement);
            }
        }
    }

    public void changeOrientation(Robot robot, String direction){
        switch (robot.getOrientation()){
            case "top" -> {
                switch (direction){
                    case "left" -> {
                        robot.setOrientation("left");
                    }
                    case "right" -> {
                        robot.setOrientation("right");
                    }
                    case "uturn" -> {
                        robot.setOrientation("bottom");
                    }
                }
            }
            case "bottom" -> {
                switch (direction){
                    case "left" -> {
                        robot.setOrientation("right");
                    }
                    case "right" -> {
                        robot.setOrientation("left");
                    }
                    case "uturn" -> {
                        robot.setOrientation("top");
                    }
                }
            }
            case "left" -> {
                switch (direction){
                    case "left" -> {
                        robot.setOrientation("bottom");
                    }
                    case "right" -> {
                        robot.setOrientation("top");
                    }
                    case "uturn" -> {
                        robot.setOrientation("right");
                    }
                }
            }
            case "right" -> {
                switch (direction){
                    case "left" -> {
                        robot.setOrientation("top");
                    }
                    case "right" -> {
                        robot.setOrientation("bottom");
                    }
                    case "uturn" -> {
                        robot.setOrientation("left");
                    }
                }
            }
        }
    }

    //TODO: expand method for laser hits robot (robotMap)
    //      check for coordination consistency
    //      check for possible exception handling
    public ArrayList<Point2D> getLaserPath(Laser laser, Point2D laserPosition){
        ArrayList<Point2D> laserPath = new ArrayList<>();
        laserPath.add(laserPosition);
        boolean foundBlocker = false;
        double tempPosition;
        switch(laser.getOrientations().get(0)){
            case "top" -> {
                tempPosition = laserPosition.getY();
                while(!foundBlocker){
                    tempPosition--;
                    laserPath.add(new Point2D(tempPosition, laserPosition.getY()));
                    for(int i = 0; i < map.get((int) tempPosition).get((int) laserPosition.getX()).size(); i++) {
                        if (map.get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                    }
                }
            }
            case "bottom" -> {
                tempPosition = laserPosition.getY();
                while(!foundBlocker){
                    tempPosition++;
                    laserPath.add(new Point2D(tempPosition, laserPosition.getY()));
                    for(int i = 0; i < map.get((int) tempPosition).get((int) laserPosition.getX()).size(); i++) {
                        if (map.get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                    }
                }
            }
            case "left" -> {
                tempPosition = laserPosition.getX();
                while(!foundBlocker){
                    tempPosition--;
                    laserPath.add(new Point2D(tempPosition, laserPosition.getY()));
                    for(int i = 0; i < map.get((int) laserPosition.getY()).get((int) tempPosition).size(); i++) {
                        if (map.get((int) laserPosition.getY()).get((int) tempPosition).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                    }
                }
            }
            case "right" -> {
                tempPosition = laserPosition.getX();
                while(!foundBlocker){
                    tempPosition++;
                    laserPath.add(new Point2D(tempPosition, laserPosition.getY()));
                    for(int i = 0; i < map.get((int) laserPosition.getY()).get((int) tempPosition).size(); i++) {
                        if (map.get((int) laserPosition.getY()).get((int) tempPosition).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                    }
                }
            }
            default -> {
                //Place for exception handling
            }
        }

        return laserPath;
    }


    //TODO if robot moves outside the map -> check map for RestartPoint
    //     -> spawn robot at RestartPoint

    //TODO element instanceOf Laser -> player draw Spam from DeckSpam

    //TODO calculate distance from antenna -> method


    //TODO find next wall with laser

    public void startProgrammingPhase () {
        for (Player player : playerList) {
            player.drawCardsProgramming(9 - player.getRobot().getSchadenPunkte());
            System.out.println("player " + player.getName() + "  has " + player.getDeckHand().getDeck().size());
            JSONMessage yourCardsMessage = new JSONMessage("YourCards", new YourCardsBody(player.getDeckHand().toArrayList()));
            server.sendMessage(yourCardsMessage, server.getConnectionWithID(player.getPlayerID()).getWriter());

            for (Player otherPlayer : playerList) {
                if (otherPlayer.getPlayerID() != player.getPlayerID()) {
                    JSONMessage notYourCardsMessage = new JSONMessage("NotYourCards", new NotYourCardsBody(player.getPlayerID(), player.getDeckHand().getDeck().size()));
                    server.sendMessage(notYourCardsMessage, server.getConnectionWithID(otherPlayer.getPlayerID()).getWriter());
                }
            }
        }
    }

    //TODO change get(0)
    public void startActivationPhase() {
        ArrayList<Object> currentCards = new ArrayList<>();
        for (Player player : playerList) {
            ArrayList<Object> array1 = new ArrayList<>();
            array1.add(player.getPlayerID());
            array1.add(player.getDeckRegister().getDeck().get(0).getCardName());
            currentCards.add(array1);
        }
        JSONMessage jsonMessage = new JSONMessage("CurrentCards", new CurrentCardsBody(currentCards));
        sendToAllPlayers(jsonMessage);
    }


    public boolean valideStartingPoint (int x, int y) {
        Point2D positionID = new Point2D(x, y);
        if (startPointMap.containsKey(positionID)) {
            if (!robotMap.containsKey(positionID)) {
                System.out.println("hier ist einen startpoint " + positionID);
                //TODO: testen!!
                robotMap.put(positionID, server.getPlayerWithID(currentPlayer).getRobot());
                return true;
            } else {
                System.out.println("Das ist kein leeres Starting point");
                return false;
            }
        } else {
            //TODO: dem Spieler das auch sagen
            System.out.println("hier NOT startpoint ");
            return false;
        }
    }

    public void replaceElementInMap(ArrayList<ArrayList<ArrayList<Element>>> map, int x, int y, Element element, Object object) {
        if (object instanceof Element) {
            int indexelement = map.get(y).get(x).indexOf(element);
            map.get(y).get(x).remove(element);
            map.get(y).get(x).add(indexelement, (Element) object);
        } else {
            throw new ClassCastException(object + " is not an Element!" +
                    "Can't cast this method on Objects other than Elements!");

        }
    }

    //TODO activateCard() method with switch
    //     check discard consistency


    public int getActivePhase () {
        return activePhase;
    }

    public void setActivePhase (int activePhase) {
        this.activePhase = activePhase;
        informAboutActivePhase();
        if (activePhase == 2 && !activePhaseOn) {
            startProgrammingPhase();
            activePhaseOn = true;
        } else if (activePhase == 3 && !activePhaseOn) {
            startActivationPhase();
            activePhaseOn = true;
        }
    }



    public boolean isTimerOn () {
        return timerOn;
    }

    public void setTimerOn (boolean timerOn) {
        this.timerOn = timerOn;
    }

    public int getCurrentRound () {
        return currentRound;
    }

    public void setCurrentRound (int currentRound) {
        this.currentRound = currentRound;
    }

    public boolean isActivePhaseOn () {
        return activePhaseOn;
    }

    public void setActivePhaseOn (boolean activePhaseOn) {
        this.activePhaseOn = activePhaseOn;
    }

    public int getCurrentPlayer () {
        return currentPlayer;
    }

    public void setCurrentPlayer (int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getMapName () {
        return mapName;
    }

    public void setMapName (String mapName) {
        this.mapName = mapName;
    }

    public boolean isGameOn () {
        return gameOn;
    }

    public void setGameOn (boolean gameOn) {
        this.gameOn = gameOn;
    }

    public ArrayList<String> getAvailableMaps () {
        return availableMaps;
    }

    public static ArrayList<String> getRobotNames () {
        return robotNames;
    }

    public ArrayList<Player> getPlayerList () {
        return playerList;
    }

    public ArrayList<ArrayList<ArrayList<Element>>> getMap() {
        return map;
    }

    public Map<Point2D, Laser> getLaserMap() {
        return laserMap;
    }

    public Map<Point2D, Antenna> getAntennaMap() {
        return antennaMap;
    }

    public DeckWorm getDeckWorm() {
        return deckWorm;
    }

    public DeckVirus getDeckVirus() {
        return deckVirus;
    }

    public DeckTrojan getDeckTrojan() {
        return deckTrojan;
    }

    public DeckSpam getDeckSpam() {
        return deckSpam;
    }

    public Map<Point2D, CheckPoint> getCheckPointMap() {
        return checkPointMap;
    }

    public Map<Point2D, ConveyorBelt> getConveyorBeltMap() {
        return conveyorBeltMap;
    }

    public Map<Point2D, Empty> getEmptyMap() {
        return emptyMap;
    }

    public Map<Point2D, EnergySpace> getEnergySpaceMap() {
        return energySpaceMap;
    }

    public Map<Point2D, Gear> getGearMap() {
        return gearMap;
    }

    public Map<Point2D, Pit> getPitMap() {
        return pitMap;
    }

    public Map<Point2D, PushPanel> getPushPanelMap() {
        return pushPanelMap;
    }

    public Map<Point2D, RestartPoint> getRestartPointMap() {
        return restartPointMap;
    }

    public Map<Point2D, StartPoint> getStartPointMap() {
        return startPointMap;
    }

    public Map<Point2D, Wall> getWallMap() {
        return wallMap;
    }

    public Server getServer() {
        return server;
    }

}
