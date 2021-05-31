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
        createMapObjects(mapX, mapY);
    }

    private void createMapObjects(int mapX, int mapY) {
        for(int x = 0; x < mapX; x++){
            for(int y = 0; y < mapY; y++){
                for(Element element : map.get(x).get(y)){
                    if(element instanceof Antenna){
                        Antenna antenna = (Antenna) element;
                        System.out.println("The antenna is at ("+x+"|"+y+")");
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
