package game;

import game.boardelements.*;
import game.decks.*;
import javafx.geometry.Point2D;
import json.JSONDeserializer;
import json.JSONMessage;
import json.protocol.*;
import json.protocol.CurrentPlayerBody;
import server.Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ilja Knis
 */

//TODO @FXML ATTRIBUTES GOT CHANGED TO PUBLIC TO CHECK THE EXCEPTION -> Cannot read the array length because "this.lines" is null
//     IT DIDN'T WORK LOOOOOOL
//     https://stackoverflow.com/questions/25171039/what-is-the-best-way-to-manage-multithreading-in-javafx-8
//     search in the link for -> PauseTransition

public class Game {
    private static Game instance;

    private DeckSpam deckSpam;
    private DeckTrojan deckTrojan;
    private DeckVirus deckVirus;
    private DeckWorm deckWorm;
    private DeckUpgrade deckUpgrade;
    private ArrayList<ArrayList<ArrayList<Element>>> map;
    private ArrayList<Player> playerList;
    private Server server;
    private GameTimer gameTimer;
    private ArrayList<String> upgradeCardsShop = new ArrayList<>();
    private ArrayList<String> availableMaps = new ArrayList<>();
    private ArrayList<Integer> deadRobotsIDs = new ArrayList<>();
    private static ArrayList<String> robotNames = new ArrayList<String>(Arrays.asList("Hulk X90", "Twonky", "Squash Bot", "Zoom Bot", "Twitch", "Spin Bot"));

    private Map<Point2D, Antenna> antennaMap = new HashMap<>();
    private Map<Point2D, CheckPoint> checkPointMap = new HashMap<>();
    private Map<Point2D, CheckPoint> checkPointMovedMap = new HashMap<>();
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
    private Map<Robot, Point2D> startingPointMap = new HashMap<>();
    private Map<Player, Integer> checkPointReached = new HashMap<>();
    private Map<Player, String> robotsRebootDirection = new HashMap<>();

    private Map<Integer, Player> adminPriorityMap = new HashMap<>();

    private int roundCounter = 1;
    private String mapName;
    private boolean gameOn;
    private volatile int currentPlayer;
    private int activePhase;
    private int currentRound;
    private int currentRegister = 0;
    private boolean activePhaseOn = false;
    private AtomicBoolean timerOn = new AtomicBoolean();
    private Comparator<Player> comparator = new Helper(this);
    private final boolean IS_LAZY = true;

    private HashMap<Player, ArrayList<String>> currentDamage = new HashMap<>();
    private ArrayList<Player> robotsHitByRobotLaser = new ArrayList<>();
    private ArrayList<Player> rearLasers = new ArrayList<>();

    private Game() {

    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /**
     * Constructor for the game
     * adds available Maps to availableMaps
     * creates a game timer
     *
     * @param server is the server where the game is started
     */
    public Game(Server server) {
        this.server = server;
        availableMaps.add("DizzyHighway");
        availableMaps.add("DeathTrap");
        availableMaps.add("ExtraCrispy");
        availableMaps.add("LostBearings");
        availableMaps.add("AlmostTwister");
        gameTimer = new GameTimer(server);
    }

    /**
     * Method to start the game
     * initializes all global game decks (Damage cards and Upgrade cards)
     * initializes global variables to initial value
     * sends GameStartedMessage to all participating players
     *
     * @param players are the players who joined the game
     * @throws IOException handles IO exceptions
     */
    public void startGame(ArrayList<Player> players) throws IOException {
        this.deckSpam = new DeckSpam();
        this.deckSpam.initializeDeck();

        this.deckTrojan = new DeckTrojan();
        this.deckTrojan.initializeDeck();

        this.deckVirus = new DeckVirus();
        this.deckVirus.initializeDeck();

        this.deckWorm = new DeckWorm();
        this.deckWorm.initializeDeck();

        this.deckUpgrade = new DeckUpgrade();
        this.deckUpgrade.initializeDeck();
        this.deckUpgrade.shuffleDeck();

        this.playerList = players;

        clearDamage();

        for (Player player : players) {
            checkPointReached.put(player, 0);
        }

        this.currentRound = 0;
        this.setActivePhase(0);

        //Sort list by IDs
        playerList.sort(comparator);
        this.setCurrentPlayer(playerList.get(0).getPlayerID());

        //send an alle GameStartedMessage
        mapName = mapName.replaceAll("\\s+", "");
        String fileName = "Maps/" + mapName + ".json";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        String content = new String(Files.readAllBytes(file.toPath()));
        JSONMessage jsonMessage = JSONDeserializer.deserializeJSON(content);
        sendToAllPlayers(jsonMessage);

        refillUpgradeShop();
        informAboutActivePhase();
        informAboutCurrentPlayer();
    }

    /**
     * Method to refresh the game to an initial state
     * necessary to start a new game from scratch
     */
    public void refreshGame() {
        antennaMap = new HashMap<>();
        checkPointMap = new HashMap<>();
        conveyorBeltMap = new HashMap<>();
        emptyMap = new HashMap<>();
        energySpaceMap = new HashMap<>();
        gearMap = new HashMap<>();
        laserMap = new HashMap<>();
        pitMap = new HashMap<>();
        pushPanelMap = new HashMap<>();
        restartPointMap = new HashMap<>();
        startPointMap = new HashMap<>();
        wallMap = new HashMap<>();
        robotMap = new HashMap<>();
        startingPointMap = new HashMap<>();
        checkPointReached = new HashMap<>();
        robotsRebootDirection = new HashMap<>();
        currentDamage = new HashMap<>();
        robotsHitByRobotLaser = new ArrayList<>();
        deadRobotsIDs = new ArrayList<>();

        roundCounter = 1;
        currentRegister = 0;
        activePhaseOn = false;

        server.setReadyPlayer(new ArrayList<>());

        for (Player player : playerList) {
            player.setReady(false);
            player.refreshPlayer();
            for (Player player1 : server.getWaitingPlayer()) {
                JSONMessage jsonMessage = new JSONMessage("PlayerStatus", new PlayerStatusBody(player.getPlayerID(), false));
                server.sendMessage(jsonMessage, server.getConnectionWithID(player1.getPlayerID()).getWriter());
            }
        }
    }

    /**
     * Method to add clients who are too late for the game
     * into an ArrayList
     * @return  an ArrayList of PlayerIDs
     */
    public ArrayList<Integer> tooLateClients() {
        ArrayList<Integer> tooLateClients = new ArrayList<>();
        for (Player player : this.playerList) {
            if (!player.isRegisterFull()) {
                tooLateClients.add(player.getPlayerID());
            }
        }
        return tooLateClients;
    }

    /**
     * Method to check if special reboot rules are needed
     * for the current game
     * depends on the chosen map
     * @return  true if special reboot rules are needed
     */
    public boolean specialRebootRules() {
        if (mapName.equals("ExtraCrispy") || mapName.equals("DeathTrap")) {
            return true;
        } else return false;
    }

    /**
     * Method to iterate over the active players
     * @return  the next active player ID
     */
    public int nextPlayerID() {
        int currentIndex = playerList.indexOf(server.getPlayerWithID(currentPlayer));
        //Get the next alive player
        for (int i = currentIndex + 1; i < playerList.size(); i++) {
            if (!server.getCurrentGame().getDeadRobotsIDs().contains(playerList.get(i).getPlayerID())) {
                return playerList.get(i).getPlayerID();
            }
            System.out.println("Player " + playerList.get(i).getPlayerID() + " ist tot");
        }
        //No more players in the list / no more alive players in the list
        return -1;
    }

    /**
     * Method to communicate the currently
     * active phase of the game
     */
    public void informAboutActivePhase() {
        JSONMessage currentPhase = new JSONMessage("ActivePhase", new ActivePhaseBody(getActivePhase()));
        sendToAllPlayers(currentPhase);
    }

    /**
     * Method to communicate the
     * currently active player of the game
     */
    public void informAboutCurrentPlayer() {
        JSONMessage currentPlayer = new JSONMessage("CurrentPlayer", new CurrentPlayerBody(getCurrentPlayer()));
        sendToAllPlayers(currentPlayer);
    }

    /**
     * Method to send a JSONMessage to all players
     * @param jsonMessage   is the message that is sent to all players
     */
    public void sendToAllPlayers(JSONMessage jsonMessage) {
        for (int i = 0; i < playerList.size(); i++) {
            server.sendMessage(jsonMessage, server.getConnectionWithID(playerList.get(i).getPlayerID()).getWriter());
        }
    }


    public void refreshAdminPrivilege () {
        adminPriorityMap.clear();
        for (Player player : playerList) {
            player.setActiveAdminPrivilege(0);
        }
    }

    /**
     * Method to select a map for the game
     * loads the dimensions of the map
     * uses a deserializer to load the map from a JSON
     * calls method createMapObjects() to build map
     * @param mapName   is the chosen map
     * @throws IOException  handles IO exceptions
     */
    public void selectMap(String mapName) throws IOException {
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

    /**
     * Method to build the map
     * takes a three dimensional ArrayList map
     * first dimension: x; second dimension: y; third dimension: Element.class
     * iterates over x, then y coordinates of the map
     * checks the type of every element on every square of the map
     * creates elements of the corresponding type and adds them to hashmaps
     * initially elements are creates as superclass Element.class
     * calls method replaceElementInMap() in order to replace elements of
     * superclass Element.class with corresponding subclass
     * @param map   is the deserialized 3D ArrayList from the JSON message
     * @param mapX  is the x dimension of the map
     * @param mapY  is the y dimension of the map
     */
    private void createMapObjects(ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) {
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                for (int i = 0; i < map.get(x).get(y).size(); i++) {
                    switch (map.get(x).get(y).get(i).getType()) {
                        case "Antenna" -> {
                            Element element = map.get(x).get(y).get(i);
                            Antenna antenna = new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, antenna);
                            antennaMap.put(new Point2D(x, y), antenna);
                        }
                        case "ConveyorBelt" -> {
                            Element element = map.get(x).get(y).get(i);
                            ConveyorBelt conveyorBelt = new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, conveyorBelt);
                            conveyorBeltMap.put(new Point2D(x, y), conveyorBelt);
                        }
                        case "CheckPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            CheckPoint checkPoint = new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount());
                            replaceElementInMap(map, x, y, element, checkPoint);
                            checkPointMap.put(new Point2D(x, y), checkPoint);
                        }
                        case "Empty" -> {
                            Element element = map.get(x).get(y).get(i);
                            Empty empty = new Empty(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, empty);
                            emptyMap.put(new Point2D(x, y), empty);
                        }
                        case "EnergySpace" -> {
                            Element element = map.get(x).get(y).get(i);
                            EnergySpace energySpace = new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount());
                            replaceElementInMap(map, x, y, element, energySpace);
                            energySpaceMap.put(new Point2D(x, y), energySpace);
                        }
                        case "Gear" -> {
                            Element element = map.get(x).get(y).get(i);
                            Gear gear = new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, gear);
                            gearMap.put(new Point2D(x, y), gear);
                        }
                        case "Laser" -> {
                            Element element = map.get(x).get(y).get(i);
                            Laser laser = new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount());
                            replaceElementInMap(map, x, y, element, laser);
                            laserMap.put(new Point2D(x, y), laser);
                        }
                        case "Pit" -> {
                            Element element = map.get(x).get(y).get(i);
                            Pit pit = new Pit(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, pit);
                            pitMap.put(new Point2D(x, y), pit);
                        }
                        case "PushPanel" -> {
                            Element element = map.get(x).get(y).get(i);
                            PushPanel pushPanel = new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters());
                            replaceElementInMap(map, x, y, element, pushPanel);
                            pushPanelMap.put(new Point2D(x, y), pushPanel);
                        }
                        case "RestartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            RestartPoint restartPoint = new RestartPoint(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, restartPoint);
                            restartPointMap.put(new Point2D(x, y), restartPoint);
                        }
                        case "StartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            StartPoint startPoint = new StartPoint(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, startPoint);
                            startPointMap.put(new Point2D(x, y), startPoint);
                        }
                        case "Wall" -> {
                            Element element = map.get(x).get(y).get(i);
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

    private void moveCheckPoints() {
        if (getMapName().equals("AlmostTwister")) {
            for (Point2D positionCheckPoint : checkPointMap.keySet()) {
                for (Point2D position : conveyorBeltMap.keySet()) {
                    if (positionCheckPoint.getX() == position.getX() && positionCheckPoint.getY() == position.getY()) {
                        //calculate new checkPoint position
                        //first movement:
                        int numCP = checkPointMap.get(position).getCount();
                        Point2D newPosition = getMoveInDirection(positionCheckPoint, conveyorBeltMap.get(position).getOrientations().get(0));
                        System.out.println(conveyorBeltMap.get(position).getOrientations());
                        //second movement:
                        newPosition = getMoveInDirection(newPosition, conveyorBeltMap.get(newPosition).getOrientations().get(0));
                        System.out.println(conveyorBeltMap.get(newPosition).getOrientations());
                        //save new positions
                        checkPointMovedMap.put(newPosition, checkPointMap.get(positionCheckPoint));
                        //adjust map
                        removeElementFromMap(checkPointMap.get(positionCheckPoint), (int) positionCheckPoint.getX(), (int) positionCheckPoint.getY());
                        placeElementOnMap(checkPointMap.get(positionCheckPoint), (int) newPosition.getX(), (int) newPosition.getY());

                        JSONMessage jsonMessage = new JSONMessage("CheckpointMoved", new CheckpointMovedBody(numCP, (int) newPosition.getX(), (int) newPosition.getY()));
                        sendToAllPlayers(jsonMessage);
                    }
                }
            }
        }
        //change old CheckPoint positions in HashMap to new positions
        checkPointMap.clear();
        checkPointMap.putAll(checkPointMovedMap);
        checkPointMovedMap.clear();
    }

    private void removeElementFromMap(Element element, int x, int y) {
        for (int i = 0; i < map.get(x).get(y).size(); i++) {
            if (element.getType().equals(map.get(x).get(y).get(i).getType())) {
                map.get(x).get(y).remove(i);
                break;
            }
        }
    }

    private void placeElementOnMap(Element element, int x, int y){
        map.get(x).get(y).add(element);
    }

    private Point2D getMoveInDirection(Point2D position, String orientation){
        double x = position.getX();
        double y = position.getY();
        switch (orientation){
            case "left" -> x -= 1;
            case "right" -> x += 1;
            case "top" -> y -= 1;
            case "bottom" -> y += 1;
        }

        return new Point2D(x, y);
    }

    public void activateBlueBelts() {
        for (Player player : playerList) {
            for (Point2D position : conveyorBeltMap.keySet()) {
                if (conveyorBeltMap.get(position).getColour().equals("blue")) {
                    if (player.getRobot().getxPosition() == (int) position.getX() && player.getRobot().getyPosition() == (int) position.getY()) {
                        boolean movedOnBelt = false;
                        //first move on the belt
                        moveRobot(player.getRobot(), conveyorBeltMap.get(position).getOrientations().get(0), 1);
                        if (IS_LAZY) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (!deadRobotsIDs.contains(player.getPlayerID())) {
                            //second move: need to find new position and new orientation first
                            double xRobotPos = player.getRobot().getxPosition();
                            double yRobotPos = player.getRobot().getyPosition();
                            for (int i = 0; i < map.get((int) xRobotPos).get((int) yRobotPos).size(); i++) {
                                if (map.get((int) xRobotPos).get((int) yRobotPos).get(i).getType().equals("ConveyorBelt")) {
                                    movedOnBelt = true;
                                    Point2D newPos = new Point2D(xRobotPos, yRobotPos);
                                    String newOrientation = conveyorBeltMap.get(newPos).getOrientations().get(0);
                                    moveRobot(player.getRobot(), newOrientation, 1);
                                }
                            }
                            if (!movedOnBelt) {
                                moveRobot(player.getRobot(), conveyorBeltMap.get(position).getOrientations().get(0), 1);
                            }
                            if (IS_LAZY) {
                                try {
                                    Thread.sleep(80);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        moveCheckPoints();
    }


    public void sendNewPosition(Player player) {
        int clientID = player.getPlayerID();
        int newX = player.getRobot().getxPosition();
        int newY = player.getRobot().getyPosition();
        JSONMessage jsonMessage = new JSONMessage("Movement", new MovementBody(clientID, newX, newY));
        sendToAllPlayers(jsonMessage);
    }

    public void activateBoardElements() {
        activateBlueBelts();
        activateGreenBelts();
        activatePushPanels();
        activateGears();
        activateWallLasers();
        activateRobotLasers();
        activateDamage();
        activateEnergySpaces();
        activateCheckpoints();
    }

    public void activateGreenBelts() {
        for (Player player : playerList) {
            for (Point2D position : conveyorBeltMap.keySet()) {
                if (conveyorBeltMap.get(position).getColour().equals("green")) {
                    if (player.getRobot().getxPosition() == (int) position.getX() &&
                            player.getRobot().getyPosition() == (int) position.getY()) {
                        moveRobot(player.getRobot(), conveyorBeltMap.get(position).getOrientations().get(0), 1);
                        try {
                            if (IS_LAZY) {
                                Thread.sleep(80);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    public void activateEnergySpaces() {
        for (Point2D position : energySpaceMap.keySet()) {
            for (Player player : getRobotsOnFieldsOwner(position)) {
                if (currentRegister != 4) {
                    int energyCount = energySpaceMap.get(position).getCount();
                    if (energyCount > 0) {
                        energySpaceMap.get(position).setCount(energyCount - 1);
                        player.increaseEnergy(1);
                        sendToAllPlayers(new JSONMessage("Energy", new EnergyBody(player.getPlayerID(), player.getEnergy(), "EnergySpace")));
                    }
                } else {
                    // 5 register
                    player.increaseEnergy(1);
                    sendToAllPlayers(new JSONMessage("Energy", new EnergyBody(player.getPlayerID(), player.getEnergy(), "EnergySpace")));
                }
            }
        }
    }

    public void activatePushPanels() {
        if (currentRegister == 1 || currentRegister == 3 || currentRegister == 5) {
            for (Point2D position : pushPanelMap.keySet()) {
                if (pushPanelMap.get(position).getRegisters().contains(1) ||
                        pushPanelMap.get(position).getRegisters().contains(3) ||
                        pushPanelMap.get(position).getRegisters().contains(5)) {
                    for (Player player : getRobotsOnFieldsOwner(position)) {
                        moveRobot(player.getRobot(), pushPanelMap.get(position).getOrientations().get(0), 1);
                    }
                }
            }
        } else if (currentRegister == 2 || currentRegister == 4) {
            for (Point2D position : pushPanelMap.keySet()) {
                if (pushPanelMap.get(position).getRegisters().contains(2) ||
                        pushPanelMap.get(position).getRegisters().contains(4)) {
                    for (Player player : getRobotsOnFieldsOwner(position)) {
                        moveRobot(player.getRobot(), pushPanelMap.get(position).getOrientations().get(0), 1);
                    }
                }
            }
        }
    }

    public void activateGears() {
        for (Point2D position : gearMap.keySet()) {
            for (Player player : getRobotsOnFieldsOwner(position)) {
                if (gearMap.get(position).getOrientations().get(0).equals("counterclockwise")) {
                    changeOrientation(player.getRobot(), "left");
                    JSONMessage jsonMessage = new JSONMessage("PlayerTurning", new PlayerTurningBody(player.getPlayerID(), "counterclockwise"));
                    sendToAllPlayers(jsonMessage);
                } else if (gearMap.get(position).getOrientations().get(0).equals("clockwise")) {
                    changeOrientation(player.getRobot(), "right");
                    JSONMessage jsonMessage = new JSONMessage("PlayerTurning", new PlayerTurningBody(player.getPlayerID(), "clockwise"));
                    sendToAllPlayers(jsonMessage);
                }
            }
        }
    }

    public void activateWallLasers () {
        for (Point2D position : laserMap.keySet()) {
            for (Point2D beamPosition : getLaserPath(laserMap.get(position), position)) {
                for (Player player : getRobotsOnFieldsOwner(beamPosition)) {
                    drawSpam(player, laserMap.get(position).getCount());
                }
            }
        }
    }

    public boolean isPermanent (String cardName) {
        return (cardName.equals("AdminPrivilege") || cardName.equals("RearLaser"));
    }

    public void drawSpam (Player player, int amount) {
        //TODO: Check why draw damage more than deckSpam.size() is.
        int amountLeft;
        //If there is enough spam cards
        if (deckSpam.getDeck().size() >= amount) {
            for (int i = 0; i < amount; i++) {
                player.getDeckDiscard().getDeck().add(deckSpam.getTopCard());
                deckSpam.removeTopCard();
                currentDamage.get(player).add("Spam");
            }
        } else {
            //If there is not enough spam cards
            amountLeft = amount - deckSpam.getDeck().size();
            for (int i = 0; i < deckSpam.getDeck().size(); i++) {
                player.getDeckDiscard().getDeck().add(deckSpam.getDeck().get(i));
                currentDamage.get(player).add("Spam");
            }
            deckSpam.getDeck().clear();

            JSONMessage jsonMessage = new JSONMessage("PickDamage", new PickDamageBody(amountLeft));
            server.sendMessage(jsonMessage, server.getConnectionWithID(player.getPlayerID()).getWriter());
        }
    }

    public void sendDamage(Player player, ArrayList<String> damageCards) {
        JSONMessage damageMessage = new JSONMessage("DrawDamage", new DrawDamageBody(player.getPlayerID(), damageCards));
        sendToAllPlayers(damageMessage);
    }

    public void activateDamage() {
        for (Map.Entry<Player, ArrayList<String>> entry : currentDamage.entrySet()) {
            if (entry.getValue().size() != 0) {
                sendDamage(entry.getKey(), entry.getValue());
            }
        }
        clearDamage();
    }


    public void clearDamage() {
        currentDamage.clear();
        for (Player player : playerList) {
            currentDamage.put(player, new ArrayList<String>());
        }
    }

    public void activateRobotLasers() {
        ArrayList<Player> activePlayers = new ArrayList<>();
        for (Player player : playerList) {
            if (!deadRobotsIDs.contains(player.getPlayerID()))
                activePlayers.add(player);
        }
        for (Player player : activePlayers) {
            triggerLasersInSight(player.getRobot(), player.getRobot().getOrientation());
            //TODO further usage with rearLasers ArrayList for consistency
            if(rearLasers.contains(player)){
                triggerLasersInSight(player.getRobot(), getInverseOrientation(player.getRobot().getOrientation()));
            }
        }

        robotsHitByRobotLaser.clear();
    }

    public void triggerLasersInSight(Robot robot, String orientation) {
        boolean foundBlocker = false;
        boolean reachedEndOfMap = false;
        double tempPosition;
        switch (orientation) {
            case "top" -> {
                tempPosition = robot.getyPosition();

                if (tempPosition == 0) {
                    reachedEndOfMap = true;
                }

                while (!foundBlocker && !reachedEndOfMap) {
                    tempPosition--;
                    for (int i = 0; i < map.get(robot.getxPosition()).get((int) tempPosition).size(); i++) {
                        if (!getRobotsOnFieldsOwner(new Point2D(robot.getxPosition(), tempPosition)).isEmpty()) {
                            foundBlocker = true;
                            robotsHitByRobotLaser.add(getRobotsOnFieldsOwner(new Point2D(robot.getxPosition(), tempPosition)).get(0));
                            break;
                        }
                        if (map.get(robot.getxPosition()).get((int) tempPosition).get(i).getType().equals("Wall")) {
                            for (int j = 0; j < map.get(robot.getxPosition()).get((int) tempPosition).get(i).getOrientations().size(); j++) {
                                if (map.get(robot.getxPosition()).get((int) tempPosition).get(i).getOrientations()
                                        .get(j).equals("top") ||
                                        map.get(robot.getxPosition()).get((int) tempPosition).get(i).getOrientations()
                                                .get(j).equals("bottom")) {
                                    foundBlocker = true;
                                    break;
                                }
                            }
                        }
                        if (tempPosition == 0) {
                            reachedEndOfMap = true;
                        }
                    }
                }
            }
            case "bottom" -> {
                tempPosition = robot.getyPosition();

                if (tempPosition == (map.get(0).size() - 1)) {
                    reachedEndOfMap = true;
                }

                while (!foundBlocker && !reachedEndOfMap) {
                    tempPosition++;
                    for (int i = 0; i < map.get(robot.getxPosition()).get((int) tempPosition).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(robot.getxPosition(), tempPosition)).isEmpty()) {
                            foundBlocker = true;
                            robotsHitByRobotLaser.add(getRobotsOnFieldsOwner(new Point2D(robot.getxPosition(), tempPosition)).get(0));
                            break;
                        }
                        if (map.get(robot.getxPosition()).get((int) tempPosition).get(i).getType().equals("Wall")) {
                            for (int j = 0; j < map.get(robot.getxPosition()).get((int) tempPosition).get(i).getOrientations().size(); j++) {
                                if (map.get(robot.getxPosition()).get((int) tempPosition).get(i).getOrientations()
                                        .get(j).equals("bottom") ||
                                        map.get(robot.getxPosition()).get((int) tempPosition).get(i).getOrientations()
                                                .get(j).equals("top")) {
                                    foundBlocker = true;
                                    break;
                                }
                            }
                        }
                        if (tempPosition == (map.get(0).size() - 1)) {
                            reachedEndOfMap = true;
                        }
                    }
                }
            }
            case "right" -> {
                tempPosition = robot.getxPosition();

                if (tempPosition == (map.size() - 1)) {
                    reachedEndOfMap = true;
                }

                while (!foundBlocker && !reachedEndOfMap) {
                    tempPosition++;
                    for (int i = 0; i < map.get((int) tempPosition).get(robot.getyPosition()).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(tempPosition, robot.getyPosition())).isEmpty()) {
                            foundBlocker = true;
                            robotsHitByRobotLaser.add(getRobotsOnFieldsOwner(new Point2D(tempPosition, robot.getyPosition())).get(0));
                            break;
                        }
                        if (map.get((int) tempPosition).get(robot.getyPosition()).get(i).getType().equals("Wall")) {
                            for (int j = 0; j < map.get((int) tempPosition).get(robot.getyPosition()).get(i).getOrientations().size(); j++) {
                                if (map.get((int) tempPosition).get(robot.getyPosition()).get(i).getOrientations()
                                        .get(j).equals("right") ||
                                        map.get((int) tempPosition).get(robot.getyPosition()).get(i).getOrientations()
                                                .get(j).equals("left")) {
                                    foundBlocker = true;
                                    break;
                                }
                            }
                        }
                        if (tempPosition == (map.size() - 1)) {
                            reachedEndOfMap = true;
                        }
                    }
                }
            }
            case "left" -> {
                tempPosition = robot.getxPosition();

                if (tempPosition == 0) {
                    reachedEndOfMap = true;
                }

                while (!foundBlocker && !reachedEndOfMap) {
                    tempPosition--;
                    for (int i = 0; i < map.get((int) tempPosition).get(robot.getyPosition()).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(tempPosition, robot.getyPosition())).isEmpty()) {
                            foundBlocker = true;
                            robotsHitByRobotLaser.add(getRobotsOnFieldsOwner(new Point2D(tempPosition, robot.getyPosition())).get(0));
                            break;
                        }
                        if (map.get((int) tempPosition).get(robot.getyPosition()).get(i).getType().equals("Wall")) {
                            for (int j = 0; j < map.get((int) tempPosition).get(robot.getyPosition()).get(i).getOrientations().size(); j++) {
                                if (map.get((int) tempPosition).get(robot.getyPosition()).get(i).getOrientations()
                                        .get(j).equals("left") ||
                                        map.get((int) tempPosition).get(robot.getyPosition()).get(i).getOrientations()
                                                .get(j).equals("right")) {
                                    foundBlocker = true;
                                    break;
                                }
                            }
                        }
                        if (tempPosition == 0) {
                            reachedEndOfMap = true;
                        }
                    }
                }
            }
        }
        for(Player player : robotsHitByRobotLaser){
            drawSpam(player, 1);
        }

        robotsHitByRobotLaser.clear();
    }

    public ArrayList<Robot> getRobotsOnFieldsWithout(Point2D position, Robot withoutRobot) {
        ArrayList<Robot> robotsOnFields = new ArrayList<>();

        for (Player player : playerList) {
            if (player.getRobot().getxPosition() == (int) position.getX() &&
                    player.getRobot().getyPosition() == (int) position.getY()) {
                robotsOnFields.add(player.getRobot());
            }
        }
        robotsOnFields.remove(withoutRobot);

        return robotsOnFields;
    }

    public ArrayList<Robot> getRobotsOnFields(Point2D position) {
        ArrayList<Robot> robotsOnFields = new ArrayList<>();

        for (Player player : playerList) {
            if (player.getRobot().getxPosition() == (int) position.getX() &&
                    player.getRobot().getyPosition() == (int) position.getY()) {
                robotsOnFields.add(player.getRobot());
            }
        }

        return robotsOnFields;
    }

    public ArrayList<Player> getRobotsOnFieldsOwner(Point2D position) {
        ArrayList<Player> robotsOwner = new ArrayList<>();

        for (Player player : playerList) {
            if (player.getRobot().getxPosition() == (int) position.getX() &&
                    player.getRobot().getyPosition() == (int) position.getY()) {
                robotsOwner.add(player);
            }
        }
        return robotsOwner;
    }


    //TODO messageBodies verwenden
    public void activateCardEffect(String card) {
        int indexCurrentPlayer = playerList.indexOf(server.getPlayerWithID(currentPlayer));
        String robotOrientation = playerList.get(indexCurrentPlayer).getRobot().getOrientation();

        switch (card) {
            case "Again" -> {
                Card lastCard = playerList.get(indexCurrentPlayer).getDeckRegister().getDeck().get((currentRegister - 1));
                if (lastCard.cardName.equals("Again")) {
                    Card veryLastCard = playerList.get(indexCurrentPlayer).getDeckRegister().getDeck().get((currentRegister - 2));
                    activateCardEffect(veryLastCard.cardName);
                } else {
                    activateCardEffect(lastCard.cardName);
                }
                JSONMessage jsonMessage1 = new JSONMessage("CardPlayed", new PlayCardBody(lastCard.cardName));
                sendToAllPlayers(jsonMessage1);
            }
            case "BackUp" -> {
                switch (robotOrientation) {
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
                sendToAllPlayers(new JSONMessage("Energy", new EnergyBody(currentPlayer, server.getPlayerWithID(currentPlayer).getEnergy(), "PowerUpCard")));
            }
            case "TurnLeft" -> {
                changeOrientation(playerList.get(indexCurrentPlayer).getRobot(), "left");
                JSONMessage jsonMessage = new JSONMessage("PlayerTurning", new PlayerTurningBody(currentPlayer, "counterclockwise"));
                sendToAllPlayers(jsonMessage);
            }
            case "TurnRight" -> {
                changeOrientation(playerList.get(indexCurrentPlayer).getRobot(), "right");
                JSONMessage jsonMessage = new JSONMessage("PlayerTurning", new PlayerTurningBody(currentPlayer, "clockwise"));
                sendToAllPlayers(jsonMessage);
            }
            case "UTurn" -> {
                changeOrientation(playerList.get(indexCurrentPlayer).getRobot(), "uturn");
                JSONMessage jsonMessage = new JSONMessage("PlayerTurning", new PlayerTurningBody(currentPlayer, "clockwise"));
                sendToAllPlayers(jsonMessage);
                //TODO: Repair Move and Turning Queues.
                if (IS_LAZY) {
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                JSONMessage jsonMessage2 = new JSONMessage("PlayerTurning", new PlayerTurningBody(currentPlayer, "clockwise"));
                sendToAllPlayers(jsonMessage2);
            }
            case "Spam" -> {
                Card spam = playerList.get(indexCurrentPlayer).getDeckRegister().getDeck().get(currentRegister);
                deckSpam.getDeck().add(spam);
                //TODO implement a method and call by the other cards (similar to drawBlind in Player -> return Card instead of ArrayList)

                //TODO test if-statement for consistency when deck is empty
                if (!(playerList.get(indexCurrentPlayer).getDeckProgramming().getDeck().size() > 0)) {
                    playerList.get(indexCurrentPlayer).shuffleDiscardIntoProgramming();
                }
                Card top = playerList.get(indexCurrentPlayer).getDeckProgramming().getTopCard();
                if (currentRegister == 0 && top.cardName.equals("Again")) {
                    playerList.get(indexCurrentPlayer).getDeckDiscard().getDeck().add(top);
                    playerList.get(indexCurrentPlayer).getDeckProgramming().getDeck().remove(top);
                    activateCardEffect("Spam");
                    break;
                }
                playerList.get(indexCurrentPlayer).getDeckRegister().getDeck().set(currentRegister, top);
                playerList.get(indexCurrentPlayer).getDeckProgramming().getDeck().remove(top);

                JSONMessage jsonMessage = new JSONMessage("ReplaceCard", new ReplaceCardBody(currentRegister, top.cardName, currentPlayer));
                sendToAllPlayers(jsonMessage);
                activateCardEffect(top.cardName);
                JSONMessage jsonMessage1 = new JSONMessage("CardPlayed", new PlayCardBody(top.cardName));
                sendToAllPlayers(jsonMessage1);
            }
            case "Trojan" -> {
                drawSpam(server.getPlayerWithID(currentPlayer), 2);
                //TODO access current register and play top card from deckProgramming
                //     JSON Messages senden

            }
            case "Virus" -> {
                ArrayList<Player> playersWithinRadius = getPlayersInRadius(playerList.get(indexCurrentPlayer), 6);
                for (Player player : playersWithinRadius) {
                    drawSpam(player, 1);
                }
                //TODO access current register and play top card from deckProgramming
                //     JSON Messages senden
            }
            case "Worm" -> rebootRobot(server.getPlayerWithID(getCurrentPlayer()));

            case "SpamBlocker" -> replaceSpamCardsHand(server.getPlayerWithID(getCurrentPlayer()));

        }
    }

    public int getUpgradeCost (String cardName) {
        return switch (cardName) {
            case "AdminPrivilege", "SpamBlocker" -> 3;
            case "RearLaser" -> 2;
            case "MemorySwap" -> 1;
            default -> 0;
        };
    }

    public void replaceSpamCardsHand (Player player) {
        int counter = 0;
        for (Card card : player.getDeckHand().getDeck()) {
            //throw all Spam cards from hand to DeckSpam
            if (card.getCardName().equals("Spam")) {
                deckSpam.getDeck().add(card);
                player.getDeckHand().getDeck().remove(card);
                counter++;
            }
        }
        //draw a new card from deck for each discarded Spam card
        player.drawCardsProgramming(counter);

    }


    public void rebootRobot(Player player) {
        deadRobotsIDs.add(player.getPlayerID());

        int robotPlacementX = player.getRobot().getxPosition();
        int robotPlacementY = player.getRobot().getyPosition();
        String boardName = map.get(robotPlacementX).get(robotPlacementY).get(0).getIsOnBoard();
        boolean rebooted = false;
        boolean fieldIsTaken = false;
        if (!specialRebootRules()) {
            //If a robot dies on the starting map
            if (boardName.equals("Start A") || boardName.equals("Start B")) {
                System.out.println(1);
                int startingPointX = (int) startingPointMap.get(player.getRobot()).getX();
                int startingPointY = (int) startingPointMap.get(player.getRobot()).getY();
                for (Player player1 : playerList) {
                    if (player1.getPlayerID() != player.getPlayerID()) {
                        int robotHereX = player1.getRobot().getxPosition();
                        int robotHereY = player1.getRobot().getyPosition();
                        //If another robot is already on the start point
                        if (robotHereX == startingPointX && robotHereY == startingPointY) {
                            fieldIsTaken = true;
                            //If the robot can move up
                            if (canRobotMove(robotHereX, robotHereY, "top")) {
                                player1.getRobot().setyPosition(robotHereY - 1);
                                sendNewPosition(player1);

                                player.getRobot().setxPosition(startingPointX);
                                player.getRobot().setyPosition(startingPointY);
                            } else {
                                //Robot can not move up because of wall, look for another free starting point
                                int newStartingPointX = (int) firstFreeStartingPoint().getX();
                                int newStartingPointY = (int) firstFreeStartingPoint().getY();
                                player.getRobot().setxPosition(newStartingPointX);
                                player.getRobot().setyPosition(newStartingPointY);
                            }
                        }
                    }
                }
                //No another robot on the starting point
                if (!fieldIsTaken) {
                    player.getRobot().setxPosition(startingPointX);
                    player.getRobot().setyPosition(startingPointY);
                }
                rebooted = true;
            }
        }
        boolean isFree = true;
        if (specialRebootRules() || !rebooted) {
            //If robot dies on the game map
            for (HashMap.Entry<Point2D, RestartPoint> entry : restartPointMap.entrySet()) {
                if (entry.getValue() != null) {
                    int restartPointX = (int) entry.getKey().getX();
                    int restartPointY = (int) entry.getKey().getY();
                    for (Player player1 : playerList) {
                        if (player1.getPlayerID() != player.getPlayerID()) {
                            int robotX = player1.getRobot().getxPosition();
                            int robotY = player1.getRobot().getyPosition();
                            //If another robot already on the restart point
                            if (restartPointX == robotX && restartPointY == robotY) {
                                isFree = false;
                                //If the robot can move up
                                String rebootPointOrientation = entry.getValue().getOrientations().get(0);

                                System.out.println("OMG I CRY ALL THE TIME");
                                moveRobot(player1.getRobot(), rebootPointOrientation, 1);

                                player.getRobot().setxPosition(restartPointX);
                                player.getRobot().setyPosition(restartPointY);
                            }
                        }
                    }
                    if (isFree) {
                        System.out.println("IM LOST WTF");
                        //If the restart point is free
                        player.getRobot().setxPosition(restartPointX);
                        player.getRobot().setyPosition(restartPointY);
                    }
                }
                break;
            }
        }
        drawSpam(player, 2);

        JSONMessage jsonMessage = new JSONMessage("Reboot", new RebootBody(player.getPlayerID()));
        sendToAllPlayers(jsonMessage);

        sendNewPosition(player);
    }

    public Point2D firstFreeStartingPoint() {
        for (Map.Entry<Point2D, StartPoint> entry : startPointMap.entrySet()) {
            if (getRobotsOnFields(entry.getKey()).size() == 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void setRebootDirection() {
        //Set players who chose a direction, players who didn't set on top by default
        for (Integer deadRobotsID : deadRobotsIDs) {
            setRebootOrientation(server.getPlayerWithID(deadRobotsID), robotsRebootDirection.getOrDefault(server.getPlayerWithID(deadRobotsID), "top"));
        }
        robotsRebootDirection.clear();
        deadRobotsIDs.clear();
    }

    public void setRebootOrientation(Player player, String orientation) {
        switch (player.getRobot().getOrientation()) {
            case "top" -> {
                switch (orientation) {
                    case "left" -> {
                        player.getRobot().setOrientation("left");
                        sendRotation(player.getPlayerID(), "counterclockwise");
                    }
                    case "right" -> {
                        player.getRobot().setOrientation("right");
                        sendRotation(player.getPlayerID(), "clockwise");
                    }
                    case "top" -> {
                        player.getRobot().setOrientation("top");
                    }
                    case "bottom" -> {
                        player.getRobot().setOrientation("bottom");
                        sendRotation(player.getPlayerID(), "clockwise");
                        if (IS_LAZY) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        sendRotation(player.getPlayerID(), "clockwise");
                    }
                }
            }
            case "bottom" -> {
                switch (orientation) {
                    case "left" -> {
                        player.getRobot().setOrientation("left");
                        sendRotation(player.getPlayerID(), "clockwise");
                    }
                    case "right" -> {
                        player.getRobot().setOrientation("right");
                        sendRotation(player.getPlayerID(), "counterclockwise");
                    }
                    case "top" -> {
                        player.getRobot().setOrientation("top");
                        sendRotation(player.getPlayerID(), "clockwise");
                        if (IS_LAZY) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        sendRotation(player.getPlayerID(), "clockwise");
                    }
                    case "bottom" -> {
                        player.getRobot().setOrientation("bottom");
                    }
                }
            }
            case "left" -> {
                switch (orientation) {
                    case "left" -> {
                        player.getRobot().setOrientation("left");
                    }
                    case "right" -> {
                        player.getRobot().setOrientation("right");
                        sendRotation(player.getPlayerID(), "counterclockwise");
                        if (IS_LAZY) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        sendRotation(player.getPlayerID(), "counterclockwise");
                    }
                    case "top" -> {
                        player.getRobot().setOrientation("top");
                        sendRotation(player.getPlayerID(), "clockwise");
                    }
                    case "bottom" -> {
                        player.getRobot().setOrientation("bottom");
                        sendRotation(player.getPlayerID(), "counterclockwise");
                    }
                }
            }
            case "right" -> {
                switch (orientation) {
                    case "left" -> {
                        player.getRobot().setOrientation("left");
                        sendRotation(player.getPlayerID(), "clockwise");
                        if (IS_LAZY) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        sendRotation(player.getPlayerID(), "clockwise");
                    }
                    case "right" -> {
                        player.getRobot().setOrientation("right");
                    }
                    case "top" -> {
                        player.getRobot().setOrientation("top");
                        sendRotation(player.getPlayerID(), "counterclockwise");
                    }
                    case "bottom" -> {
                        player.getRobot().setOrientation("bottom");
                        sendRotation(player.getPlayerID(), "clockwise");
                    }
                }
            }
        }
    }

    public void sendRotation(int playerID, String rotation) {
        JSONMessage jsonMessage = new JSONMessage("PlayerTurning", new PlayerTurningBody(playerID, rotation));
        sendToAllPlayers(jsonMessage);
    }

    public ArrayList<Player> getPlayersInRadius(Player currentPlayer, int radius) {
        ArrayList<Player> playersInRadius = new ArrayList<>();
        int robotXPosition = currentPlayer.getRobot().getxPosition();
        int robotYPosition = currentPlayer.getRobot().getyPosition();
        int lowerXCap, upperXCap, lowerYCap, upperYCap;

        //Calculate boundaries within radius span
        if (robotXPosition - radius < 0) {
            lowerXCap = 0;
        } else {
            lowerXCap = robotXPosition - radius;
        }
        if (robotYPosition - radius < 0) {
            lowerYCap = 0;
        } else {
            lowerYCap = robotYPosition - radius;
        }
        if (robotXPosition + radius >= map.size()) {
            upperXCap = map.size();
        } else {
            upperXCap = robotXPosition + radius;
        }
        if (robotYPosition + radius >= map.get(0).size()) {
            upperYCap = map.get(0).size();
        } else {
            upperYCap = robotYPosition + radius;
        }

        for (Player player : playerList) {
            int robotX = player.getRobot().getxPosition();
            int robotY = player.getRobot().getyPosition();
            if (robotX >= lowerXCap && robotX <= upperXCap && robotY >= lowerYCap && robotY <= upperYCap) {
                playersInRadius.add(player);
            }
        }
        playersInRadius.remove(currentPlayer);

        return playersInRadius;
    }

    public boolean isFieldNotBlocked(Robot robot, int x, int y, String blockOrientation) {
        boolean foundBlocker = false;

        for (Element element : map.get(x).get(y)) {
            if (element.getType().equals("Pit")) {
                foundBlocker = true;
                rebootRobot(getRobotOwner(robot));
                break;
            }
            if (element.getType().equals("Wall")) {
                for (String orientation : element.getOrientations()) {
                    if (orientation.equals(blockOrientation)) {
                        foundBlocker = true;
                        break;
                    }
                }
            }
            if (element.getType().equals("Antenna")) {
                foundBlocker = true;
            }
        }

        return !foundBlocker;
    }


    //TODO: ich kann auch den anderen spieler auf ein checkpoint schieben
    public void activateCheckpoints() {
        for (Player player : playerList) {
            for (Point2D position : checkPointMap.keySet()) {
                if (player.getRobot().getxPosition() == position.getX() && player.getRobot().getyPosition() == position.getY()) {
                    int lastCheckpoint = checkPointReached.get(player);
                    if (checkPointMap.get(position).getCount() == lastCheckpoint + 1) {
                        checkPointReached.replace(player, lastCheckpoint + 1);
                        if (lastCheckpoint + 1 == checkPointMap.size()) {
                            JSONMessage checkpointReached = new JSONMessage("CheckPointReached", new CheckPointReachedBody(player.getPlayerID(), lastCheckpoint + 1));
                            sendToAllPlayers(checkpointReached);
                            JSONMessage finishedGame = new JSONMessage("GameFinished", new GameFinishedBody(player.getPlayerID()));
                            sendToAllPlayers(finishedGame);
                            refreshGame();
                        } else {
                            JSONMessage checkpointReached = new JSONMessage("CheckPointReached", new CheckPointReachedBody(player.getPlayerID(), lastCheckpoint + 1));
                            sendToAllPlayers(checkpointReached);
                        }
                    }
                }
            }
        }
    }

    public void moveRobot(Robot robot, String orientation, int movement) {
        int robotXPosition = robot.getxPosition();
        int robotYPosition = robot.getyPosition();
        boolean canMove;
        switch (orientation) {
            case "top" -> {
                for (int i = 0; i < movement; i++) {
                    if (robotYPosition - 1 < 0) {
                        //TODO: Test the rebootRobot method
                        rebootRobot(server.getPlayerWithID(currentPlayer));
                        break;
                    } else {
                        canMove = isFieldNotBlocked(robot, robotXPosition, (robotYPosition - 1),
                                getInverseOrientation("top"));
                        if (canMove && canRobotMove(robotXPosition, robotYPosition, orientation)) {
                            robot.setyPosition(robotYPosition - 1);
                            robotYPosition--;
                            sendNewPosition(getRobotOwner(robot));
                            if (!getRobotsOnFieldsWithout(new Point2D(robotXPosition, robotYPosition), robot).isEmpty()) {
                                pushRobot(robot,
                                        getRobotsOnFieldsWithout(new Point2D(robotXPosition, robotYPosition), robot).get(0), 1);
                            }
                        }
                    }
                    if (IS_LAZY) {
                        if (movement > 1) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            case "bottom" -> {
                for (int i = 0; i < movement; i++) {
                    if (robotYPosition + 1 >= map.get(0).size()) {
                        //TODO: Test the rebootRobot method
                        rebootRobot(server.getPlayerWithID(currentPlayer));
                        break;
                    } else {
                        canMove = isFieldNotBlocked(robot, robotXPosition, (robotYPosition + 1),
                                getInverseOrientation("bottom"));
                        if (canMove && canRobotMove(robotXPosition, robotYPosition, orientation)) {
                            robot.setyPosition(robotYPosition + 1);
                            robotYPosition++;
                            sendNewPosition(getRobotOwner(robot));
                            if (!getRobotsOnFieldsWithout(new Point2D(robotXPosition, robotYPosition), robot).isEmpty()) {
                                pushRobot(robot,
                                        getRobotsOnFieldsWithout(new Point2D(robotXPosition, robotYPosition), robot).get(0), 1);
                            }
                        }
                    }
                    if (IS_LAZY) {
                        if (movement > 1) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            case "left" -> {
                for (int i = 0; i < movement; i++) {
                    if (robotXPosition - 1 < 0) {
                        //TODO: Test the rebootRobot method
                        rebootRobot(server.getPlayerWithID(currentPlayer));
                        break;
                    } else {
                        canMove = isFieldNotBlocked(robot, (robotXPosition - 1), robotYPosition,
                                getInverseOrientation("left"));
                        if (canMove && canRobotMove(robotXPosition, robotYPosition, orientation)) {
                            robot.setxPosition(robotXPosition - 1);
                            robotXPosition--;
                            sendNewPosition(getRobotOwner(robot));
                            if (!getRobotsOnFieldsWithout(new Point2D(robotXPosition, robotYPosition), robot).isEmpty()) {
                                pushRobot(robot,
                                        getRobotsOnFieldsWithout(new Point2D(robotXPosition, robotYPosition), robot).get(0), 1);
                            }
                        }
                    }
                    if (IS_LAZY) {
                        if (movement > 1) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            case "right" -> {
                for (int i = 0; i < movement; i++) {
                    if (robotXPosition + 1 >= map.size()) {
                        //TODO: Test the rebootRobot method
                        rebootRobot(server.getPlayerWithID(currentPlayer));
                        break;
                    } else {
                        canMove = isFieldNotBlocked(robot, (robotXPosition + 1), robotYPosition,
                                getInverseOrientation("right"));
                        if (canMove && canRobotMove(robotXPosition, robotYPosition, orientation)) {
                            robot.setxPosition(robotXPosition + 1);
                            robotXPosition++;
                            sendNewPosition(getRobotOwner(robot));
                            if (!getRobotsOnFieldsWithout(new Point2D(robotXPosition, robotYPosition), robot).isEmpty()) {
                                pushRobot(robot,
                                        getRobotsOnFieldsWithout(new Point2D(robotXPosition, robotYPosition), robot).get(0), 1);
                            }
                        }
                    }
                    if (IS_LAZY) {
                        if (movement > 1) {
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public Player getRobotOwner(Robot robot) {
        for (Player player : playerList) {
            if (player.getRobot().equals(robot)) {
                return player;
            }
        }
        return null;
    }

    //TODO: add to moveRobot and find robots on new field -> use pushRobot()
    public void pushRobot(Robot pusher, Robot robotGettingPushed, int iteration) {
        int initX = robotGettingPushed.getxPosition();
        int initY = robotGettingPushed.getyPosition();

        if (iteration == 1) {
            moveRobot(robotGettingPushed, pusher.getOrientation(), 1);
        }
        if (iteration == 2) {
            moveRobot(robotGettingPushed, rotateClockwise(pusher.getOrientation()), 1);
        }
        if (iteration == 3) {
            moveRobot(robotGettingPushed, rotateClockwise(rotateClockwise(pusher.getOrientation())), 1);
        }
        if (iteration == 4) {
            moveRobot(robotGettingPushed, rotateClockwise(rotateClockwise(rotateClockwise(pusher.getOrientation()))), 1);
        }

        if (initX == robotGettingPushed.getxPosition() && initY == robotGettingPushed.getyPosition()) {
            switch (iteration) {
                case 1 -> pushRobot(pusher, robotGettingPushed, 2);
                case 2 -> pushRobot(pusher, robotGettingPushed, 3);
                case 3 -> pushRobot(pusher, robotGettingPushed, 4);
            }
        }
    }

    public String rotateClockwise(String orientation) {
        String rotatedOrientation;

        switch (orientation) {
            case "top" -> rotatedOrientation = "right";
            case "right" -> rotatedOrientation = "bottom";
            case "bottom" -> rotatedOrientation = "left";
            case "left" -> rotatedOrientation = "top";
            default -> rotatedOrientation = orientation;
        }

        return rotatedOrientation;
    }

    private boolean canRobotMove(int robotXPosition, int robotYPosition, String orientation) {
        boolean canPass = true;
        for (Element element : map.get(robotXPosition).get(robotYPosition)) {
            if ("Wall".equals(element.getType())) {
                for (String orient : element.getOrientations()) {
                    if (orientation.equals(orient)) {
                        canPass = false;
                        break;
                    }
                }
            }
        }
        return canPass;
    }

    public void changeOrientation(Robot robot, String direction) {
        switch (robot.getOrientation()) {
            case "top" -> {
                switch (direction) {
                    case "left" -> robot.setOrientation("left");

                    case "right" -> robot.setOrientation("right");

                    case "uturn" -> robot.setOrientation("bottom");

                }
            }
            case "bottom" -> {
                switch (direction) {
                    case "left" -> robot.setOrientation("right");

                    case "right" -> robot.setOrientation("left");

                    case "uturn" -> robot.setOrientation("top");

                }
            }
            case "left" -> {
                switch (direction) {
                    case "left" -> robot.setOrientation("bottom");

                    case "right" -> robot.setOrientation("top");

                    case "uturn" -> robot.setOrientation("right");

                }
            }
            case "right" -> {
                switch (direction) {
                    case "left" -> robot.setOrientation("top");

                    case "right" -> robot.setOrientation("bottom");

                    case "uturn" -> robot.setOrientation("left");

                }
            }
        }
    }

    //TODO: expand method for laser hits robot (robotMap)
    //      check for coordination consistency
    //      check for possible exception handling
    public ArrayList<Point2D> getLaserPath(Laser laser, Point2D laserPosition) {
        ArrayList<Point2D> laserPath = new ArrayList<>();
        laserPath.add(laserPosition);
        boolean foundBlocker = false;
        double tempPosition;
        switch (laser.getOrientations().get(0)) {
            case "top" -> {
                tempPosition = laserPosition.getY();
                while (!foundBlocker) {
                    tempPosition--;
                    laserPath.add(new Point2D(laserPosition.getX(), tempPosition));
                    for (int i = 0; i < map.get((int) laserPosition.getX()).get((int) tempPosition).size(); i++) {
                        //Is a robot in the line of the laser?
                        if (!getRobotsOnFields(new Point2D(laserPosition.getX(), tempPosition)).isEmpty()) {
                            foundBlocker = true;
                            //Robot robotShot = getRobotsOnFields(new Point2D(laserPosition.getX(), tempPosition)).get(0);
                            break;
                        }
                        if (map.get((int) laserPosition.getX()).get((int) tempPosition).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                        if (map.get((int) laserPosition.getX()).get((int) tempPosition).get(i).getType().equals("CheckPoint")) {
                            foundBlocker = true;
                        }
                    }
                }
            }
            case "bottom" -> {
                tempPosition = laserPosition.getY();
                while (!foundBlocker) {
                    tempPosition++;
                    laserPath.add(new Point2D(laserPosition.getX(), tempPosition));
                    for (int i = 0; i < map.get((int) laserPosition.getX()).get((int) tempPosition).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(laserPosition.getX(), tempPosition)).isEmpty()) {
                            foundBlocker = true;
                            break;
                        }
                        if (map.get((int) laserPosition.getX()).get((int) tempPosition).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                        if (map.get((int) laserPosition.getX()).get((int) tempPosition).get(i).getType().equals("CheckPoint")) {
                            foundBlocker = true;
                        }
                    }
                }
            }
            case "left" -> {
                tempPosition = laserPosition.getX();
                while (!foundBlocker) {
                    tempPosition--;
                    laserPath.add(new Point2D(tempPosition, laserPosition.getY()));
                    for (int i = 0; i < map.get((int) tempPosition).get((int) laserPosition.getY()).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(tempPosition, laserPosition.getY())).isEmpty()) {
                            foundBlocker = true;
                            break;
                        }
                        if (map.get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                        if (map.get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("CheckPoint")) {
                            foundBlocker = true;
                        }
                    }
                }
            }
            case "right" -> {
                tempPosition = laserPosition.getX();
                while (!foundBlocker) {
                    tempPosition++;
                    laserPath.add(new Point2D(tempPosition, laserPosition.getY()));
                    for (int i = 0; i < map.get((int) tempPosition).get((int) laserPosition.getY()).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(tempPosition, laserPosition.getY())).isEmpty()) {
                            foundBlocker = true;
                            break;
                        }
                        if (map.get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                        if (map.get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("CheckPoint")) {
                            foundBlocker = true;
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

    public void startProgrammingPhase() {
        //TODO check .NullPointerException: Cannot invoke "game.Robot.getSchadenPunkte()" because the return value of "game.Player.getRobot()" is null
        for (Player player : playerList) {
            player.drawCardsProgramming(9);
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

    public boolean valideStartingPoint(int x, int y) {
        Point2D positionID = new Point2D(x, y);
        if (startPointMap.containsKey(positionID)) {
            if (!robotMap.containsKey(positionID)) {
                robotMap.put(positionID, server.getPlayerWithID(currentPlayer).getRobot());
                return true;
            } else {
                JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("Another robot is on this tile!"));
                server.sendMessage(errorNotYourTurn, server.getConnectionWithID(currentPlayer).getWriter());
                return false;
            }
        } else {
            JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("It's not a starting point"));
            server.sendMessage(errorNotYourTurn, server.getConnectionWithID(currentPlayer).getWriter());
            return false;
        }
    }

    public void replaceElementInMap(ArrayList<ArrayList<ArrayList<Element>>> map, int x, int y, Element element, Object object) {
        if (object instanceof Element) {
            int indexelement = map.get(x).get(y).indexOf(element);
            map.get(x).get(y).remove(element);
            map.get(x).get(y).add(indexelement, (Element) object);
        } else {
            throw new ClassCastException(object + " is not an Element!" +
                    "Can't cast this method on Objects other than Elements!");
        }
    }

    public void setActivePhase(int activePhase) {
        this.activePhase = activePhase;
        if (activePhase == 1 && !activePhaseOn) {
            informAboutActivePhase();
            startUpgradePhase();
            activePhaseOn = true;
        } else if (activePhase == 2 && !activePhaseOn) {
            informAboutActivePhase();
            startProgrammingPhase();
            activePhaseOn = true;
        } else if (activePhase == 3 && !activePhaseOn) {
            informAboutActivePhase();
            startActivationPhase();
            activePhaseOn = true;
        }
    }

    //TODO change get(0)
    public void startActivationPhase () {
        playerList.sort(comparator);        //Sort list by distance to the Antenna
        currentRegister = 0;
        sendCurrentCards(currentRegister);
        currentPlayer = playerList.get(0).getPlayerID();
        informAboutCurrentPlayer();
    }


    public void refillUpgradeShop () {
        //how much cards reinzutun
        int amount = playerList.size() - upgradeCardsShop.size();
        ArrayList<String> newCards = new ArrayList<>();
        //deckUpgrade.shuffleDeck ();
        for (int i = 0; i < amount; i++) {
            upgradeCardsShop.add(deckUpgrade.getTopCard().getCardName());
            newCards.add(deckUpgrade.getTopCard().getCardName());
            deckUpgrade.removeTopCard();
        }
        JSONMessage jsonMessage = new JSONMessage("RefillShop", new RefillShopBody(newCards));
        sendToAllPlayers(jsonMessage);
    }


    public void startUpgradePhase () {
        //After the first round and If no one bought an Upgrade Card
        if (upgradeCardsShop.size() == playerList.size() && roundCounter != 1) {
            upgradeCardsShop.clear();
            for (int i = 0; i < playerList.size(); i++) {
                upgradeCardsShop.add(deckUpgrade.getTopCard().getCardName());
                deckUpgrade.removeTopCard();
            }
            JSONMessage jsonMessage = new JSONMessage("ExchangeShop", new ExchangeShopBody(upgradeCardsShop));
            sendToAllPlayers(jsonMessage);
        } else if (roundCounter != 1) {
            //If the Upgrade Shop not full
            refillUpgradeShop();
        }
        playerList.sort(comparator);        //Sort list by distance to the Antenna
        currentPlayer = playerList.get(0).getPlayerID();
        informAboutCurrentPlayer();
    }

    public void canStartTheGame () {
        if (server.areAllPlayersReady()) {
            try {
                if (server.onlyAI() && server.getCurrentGame().getMapName() == null) {
                    ArrayList<String> maps = server.getCurrentGame().getAvailableMaps();
                    int random = (int) (Math.random() * maps.size());
                    String mapName = maps.get(random);
                    server.getCurrentGame().selectMap(mapName);
                }
                if (server.getCurrentGame().getMapName() != null) {
                    server.getCurrentGame().setGameOn(true);
                    server.getCurrentGame().startGame(server.getReadyPlayer());
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public String getInverseOrientation(String orientation) {
        String inverseOrientation;
        switch (orientation) {
            case "top" -> inverseOrientation = "bottom";
            case "bottom" -> inverseOrientation = "top";
            case "left" -> inverseOrientation = "right";
            case "right" -> inverseOrientation = "left";
            default -> throw new IllegalStateException("Unexpected value: " + orientation);
        }
        return inverseOrientation;
    }

    public void sendCurrentCards(int register) {
        ArrayList<Object> currentCards = new ArrayList<>();
        for (Player player : playerList) {
            if (!deadRobotsIDs.contains(player.getPlayerID())) {
                ArrayList<Object> array1 = new ArrayList<>();
                array1.add("clientID=" + player.getPlayerID() + ".0");
                array1.add("card=" + player.getDeckRegister().getDeck().get(register).getCardName());
                currentCards.add(array1);
            }
        }
        JSONMessage jsonMessage = new JSONMessage("CurrentCards", new CurrentCardsBody(currentCards));
        sendToAllPlayers(jsonMessage);
        //currentPlayer = playerList.get(0).getPlayerID();
        //informAboutCurrentPlayer();
    }

    public int getActivePhase() {
        return activePhase;
    }

    public static class Helper implements java.util.Comparator<Player> {
        private Game game;

        private Helper(Game game) {
            this.game = game;
        }

        @Override
        public int compare(Player o1, Player o2) {
            // Compare Player IDs
            if (game.getActivePhase() == 0) {
                return Integer.compare(o1.getPlayerID(), o2.getPlayerID());
            } else if (game.getActivePhase() == 1) {
                // Compare distance to Antenna
                for (HashMap.Entry<Point2D, Antenna> entry : game.getAntennaMap().entrySet()) {
                    if (entry.getValue() != null) {
                        int antennaX = (int) entry.getKey().getX();
                        int antennaY = (int) entry.getKey().getY();

                        int robotX = o1.getRobot().getxPosition();
                        int robotY = o1.getRobot().getyPosition();

                        int oRobotX = o2.getRobot().getxPosition();
                        int oRobotY = o2.getRobot().getyPosition();

                        int distance = Math.abs(robotX - antennaX) + Math.abs(robotY - antennaY);
                        int oDistance = Math.abs(oRobotX - antennaX) + Math.abs(oRobotY - antennaY);
                        return Integer.compare(distance, oDistance);
                    }
                }
                return Integer.compare(o1.getPlayerID(), o2.getPlayerID());
            } else {
                Player admin = null;
                if (game.getAdminPriorityMap().containsKey(game.getCurrentRegister())) {
                    admin = game.getAdminPriorityMap().get(game.getCurrentRegister());
                }
                // Compare distance to Antenna
                for (HashMap.Entry<Point2D, Antenna> entry : game.getAntennaMap().entrySet()) {
                    if (entry.getValue() != null) {
                        int antennaX = (int) entry.getKey().getX();
                        int antennaY = (int) entry.getKey().getY();

                        int robotX = o1.getRobot().getxPosition();
                        int robotY = o1.getRobot().getyPosition();

                        int oRobotX = o2.getRobot().getxPosition();
                        int oRobotY = o2.getRobot().getyPosition();

                        int distance = Math.abs(robotX - antennaX) + Math.abs(robotY - antennaY);
                        int oDistance = Math.abs(oRobotX - antennaX) + Math.abs(oRobotY - antennaY);
                        if (admin != null) {
                            if (o1.equals(admin)) {
                                return 1;
                            } else if (o2.equals((admin))) {
                                return -1;
                            }
                        }
                        return Integer.compare(distance, oDistance);
                    }
                }
                return Integer.compare(o1.getPlayerID(), o2.getPlayerID());
            }
        }
    }

    public Integer getCurrentRegister() {
        return currentRegister;
    }

    public void setCurrentRegister(int currentRegister) {
        this.currentRegister = currentRegister;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public boolean getTimerOn() {
        return timerOn.get();
    }

    public synchronized void setTimerOn(AtomicBoolean timerOn) {
        this.timerOn = timerOn;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public boolean isActivePhaseOn() {
        return activePhaseOn;
    }

    public void setNewRoundCounter() {
        this.roundCounter++;
        System.out.println("************************************************************************");
        System.out.println("******************************   ROUND " + roundCounter + "   ******************************");
        System.out.println("************************************************************************");

    }

    public void setActivePhaseOn(boolean activePhaseOn) {
        this.activePhaseOn = activePhaseOn;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }


    public Map<Player, Integer> getCheckPointReached() {
        return checkPointReached;
    }

    public void setCheckPointReached(Map<Player, Integer> checkPointReached) {
        this.checkPointReached = checkPointReached;
    }

    public boolean isGameOn() {
        return gameOn;
    }

    public void setGameOn(boolean gameOn) {
        this.gameOn = gameOn;
    }

    public ArrayList<String> getAvailableMaps() {
        return availableMaps;
    }

    public static ArrayList<String> getRobotNames() {
        return robotNames;
    }

    public ArrayList<Player> getPlayerList() {
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

    public ArrayList<Integer> getDeadRobotsIDs() {
        return deadRobotsIDs;
    }

    public Map<Robot, Point2D> getStartingPointMap() {
        return startingPointMap;
    }

    public Comparator<Player> getComparator() {
        return comparator;
    }

    public Map<Player, String> getRobotsRebootDirection() {
        return robotsRebootDirection;
    }

    public DeckUpgrade getDeckUpgrade() {
        return deckUpgrade;
    }

    public Map<Integer, Player> getAdminPriorityMap () {
        return adminPriorityMap;
    }

    public void setAdminPriorityMap (Map<Integer, Player> adminPriorityMap) {
        this.adminPriorityMap = adminPriorityMap;
    }

    public ArrayList<Player> getRearLasers () {
        return rearLasers;
    }

    public void setRearLasers (ArrayList<Player> rearLasers) {
        this.rearLasers = rearLasers;
    }
}
