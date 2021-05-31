package game;

import game.decks.DeckSpam;
import game.decks.DeckTrojan;
import game.decks.DeckVirus;
import game.decks.DeckWorm;
import json.JSONDeserializer;
import json.JSONMessage;
import json.protocol.GameStartedBody;
import server.Server;
import game.boardelements.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Server server;

    private Map<String, Antenna> antennaMap = new HashMap<>();
    private Map<String, CheckPoint> checkPointMap = new HashMap<>();
    private Map<String, ConveyorBelt> conveyorBeltMap = new HashMap<>();
    private Map<String, Empty> emptyMap = new HashMap<>();
    private Map<String, EnergySpace> energySpaceMap = new HashMap<>();
    private Map<String, Gear> gearMap = new HashMap<>();
    private Map<String, Laser> laserMap = new HashMap<>();
    private Map<String, Pit> pitMap = new HashMap<>();
    private Map<String, PushPanel> pushPanelMap = new HashMap<>();
    private Map<String, RestartPoint> restartPointMap = new HashMap<>();
    private Map<String, StartPoint> startPointMap = new HashMap<>();
    private Map<String, Wall> wallMap = new HashMap<>();

    public Game(ArrayList<Player> playerList, Server server){
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

    public void selectMap() throws IOException {
        //TODO maybe try block instead of throws IOException
        Path pathToMap = Paths.get("blinde-bonbons/src/resources/Maps/DizzyHighway.json");
        String jsonMap = Files.readString(pathToMap, StandardCharsets.UTF_8);
        JSONMessage jsonMessage = JSONDeserializer.deserializeJSON(jsonMap);
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
                            String position = x + "/" + y;
                            antennaMap.put(position, new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations()));
                        }
                        case "ConveyorBelt" -> {
                            String position = x + "/" + y;
                            conveyorBeltMap.put(position, new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations()));
                        }
                        case "CheckPoint" -> {
                            String position = x + "/" + y;
                            checkPointMap.put(position, new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount()));
                        }
                        case "Empty" -> {
                            String position = x + "/" + y;
                            emptyMap.put(position, new Empty(element.getType(), element.getIsOnBoard()));
                        }
                        case "EnergySpace" -> {
                            String position = x + "/" + y;
                            energySpaceMap.put(position, new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount()));
                        }
                        case "Gear" -> {
                            String position = x + "/" + y;
                            gearMap.put(position, new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations()));
                        }
                        case "Laser" -> {
                            String position = x + "/" + y;
                            laserMap.put(position, new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount()));
                        }
                        case "Pit" -> {
                            String position = x + "/" + y;
                            pitMap.put(position, new Pit(element.getType(), element.getIsOnBoard()));
                        }
                        case "PushPanel" -> {
                            String position = x + "/" + y;
                            pushPanelMap.put(position, new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters()));
                        }
                        case "RestartPoint" -> {
                            String position = x + "/" + y;
                            restartPointMap.put(position, new RestartPoint(element.getType(), element.getIsOnBoard()));
                        }
                        case "StartPoint" -> {
                            String position = x + "/" + y;
                            startPointMap.put(position, new StartPoint(element.getType(), element.getIsOnBoard()));
                        }
                        case "Wall" -> {
                            String position = x + "/" + y;
                            wallMap.put(position, new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations()));
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

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public ArrayList<ArrayList<ArrayList<Element>>> getMap() {
        return map;
    }
}
