package game;

import game.decks.DeckSpam;
import game.decks.DeckTrojan;
import game.decks.DeckVirus;
import game.decks.DeckWorm;
import server.Server;

import java.util.ArrayList;
import java.util.Arrays;

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
    private ArrayList<String> availableMaps = new ArrayList<>();
    private static final ArrayList<String> robotNames = new ArrayList<String>(Arrays.asList("Hulk X90", "Twonky", "Squash Bot", "Zoom Bot", "Twitch", "Spin Bot"));


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

    //TODO if robot moves outside the map -> check map for RestartPoint
    //     -> spawn robot at RestartPoint

    //TODO element instanceOf Laser -> player draw Spam from DeckSpam

    //TODO calculate distance from antenna -> method


    public ArrayList<String> getAvailableMaps () {
        return availableMaps;
    }

    public ArrayList<Player> getPlayerList () {
        return playerList;
    }

    public ArrayList<ArrayList<ArrayList<Element>>> getMap () {
        return map;
    }

    public static ArrayList<String> getRobotNames () {
        return robotNames;
    }
}
