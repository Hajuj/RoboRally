package game;

import game.boardelements.*;
import game.decks.DeckSpam;
import game.decks.DeckTrojan;
import game.decks.DeckVirus;
import game.decks.DeckWorm;
import javafx.geometry.Point2D;
import json.JSONDeserializer;
import json.JSONMessage;
import json.protocol.GameStartedBody;
import server.Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Ilja Knis
 */
public class Game {

    private DeckSpam deckSpam;
    private DeckTrojan deckTrojan;
    private DeckVirus deckVirus;
    private DeckWorm deckWorm;
    private ArrayList<ArrayList<ArrayList<Element>>> map;
    private ArrayList<Player> playerList;
    private final Server server;
    private final ArrayList<String> availableMaps = new ArrayList<>();
    private static final ArrayList<String> robotNames = new ArrayList<String>(Arrays.asList("Hulk X90", "Twonky", "Squash Bot", "Zoom Bot", "Twitch", "Spin Bot"));

    private final Map<Point2D, Antenna> antennaMap = new HashMap<>();
    private final Map<Point2D, CheckPoint> checkPointMap = new HashMap<>();
    private final Map<Point2D, ConveyorBelt> conveyorBeltMap = new HashMap<>();
    private final Map<Point2D, Empty> emptyMap = new HashMap<>();
    private final Map<Point2D, EnergySpace> energySpaceMap = new HashMap<>();
    private final Map<Point2D, Gear> gearMap = new HashMap<>();
    private final Map<Point2D, Laser> laserMap = new HashMap<>();
    private final Map<Point2D, Pit> pitMap = new HashMap<>();
    private final Map<Point2D, PushPanel> pushPanelMap = new HashMap<>();
    private final Map<Point2D, RestartPoint> restartPointMap = new HashMap<>();
    private final Map<Point2D, StartPoint> startPointMap = new HashMap<>();
    private final Map<Point2D, Wall> wallMap = new HashMap<>();

    public Game (Server server) {
        this.server = server;
        availableMaps.add("DizzyHighway");
    }

    public Game (ArrayList<Player> playerList, Server server) {
        this.server = server;

        this.deckSpam = new DeckSpam();
        this.deckSpam.initializeDeck();

        this.deckTrojan = new DeckTrojan();
        this.deckTrojan.initializeDeck();

        this.deckVirus = new DeckVirus();
        this.deckVirus.initializeDeck();

        this.deckWorm = new DeckWorm();
        this.deckWorm.initializeDeck();

        this.map = new ArrayList<>();
        this.playerList = playerList;

        this.map = new ArrayList<>();
    }

    //TODO select map
    //     if (Player player:playerList) isAI -> pickRandomMap
    //     else playerList.get(0)... pickMap

    //TODO at least 2 players ready to start game (max 6)

    //TODO game logic with startGame()
    //     map creation with elements (deserialization)
    //     phases

    public void selectMap (String mapName) throws IOException {
        //TODO maybe try block instead of throws IOException
        String fileName = "Maps/DizzyHighway.json";
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
        for(int x = 0; x < mapX; x++){
            for(int y = 0; y < mapY; y++){
                for(Element element : map.get(x).get(y)){
                    switch(element.getType()){
                        case "Antenna" -> {
                            antennaMap.put(new Point2D(x,y), new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations()));
                        }
                        case "ConveyorBelt" -> {
                            conveyorBeltMap.put(new Point2D(x,y), new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations()));
                        }
                        case "CheckPoint" -> {
                            checkPointMap.put(new Point2D(x,y), new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount()));
                        }
                        case "Empty" -> {
                            emptyMap.put(new Point2D(x,y), new Empty(element.getType(), element.getIsOnBoard()));
                        }
                        case "EnergySpace" -> {
                            energySpaceMap.put(new Point2D(x,y), new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount()));
                        }
                        case "Gear" -> {
                            gearMap.put(new Point2D(x,y), new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations()));
                        }
                        case "Laser" -> {
                            laserMap.put(new Point2D(x,y), new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount()));
                        }
                        case "Pit" -> {
                            pitMap.put(new Point2D(x,y), new Pit(element.getType(), element.getIsOnBoard()));
                        }
                        case "PushPanel" -> {
                            pushPanelMap.put(new Point2D(x,y), new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters()));
                        }
                        case "RestartPoint" -> {
                            restartPointMap.put(new Point2D(x,y), new RestartPoint(element.getType(), element.getIsOnBoard()));
                        }
                        case "StartPoint" -> {
                            startPointMap.put(new Point2D(x,y), new StartPoint(element.getType(), element.getIsOnBoard()));
                        }
                        case "Wall" -> {
                            wallMap.put(new Point2D(x,y), new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations()));
                        }
                        default -> {}
                    }
                }
            }
        }
    }

    //TODO if robot moves outside the map -> check map for RestartPoint
    //     -> spawn robot at RestartPoint

    //TODO element instanceOf Laser -> player draw Spam from DeckSpam

    //TODO calculate distance from antenna -> method

    public ArrayList<String> getAvailableMaps () {
        return availableMaps;
    }

    public static ArrayList<String> getRobotNames () {
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
}
