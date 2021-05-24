package game;

import game.decks.DeckSpam;
import game.decks.DeckTrojan;
import game.decks.DeckVirus;
import game.decks.DeckWorm;

import java.util.ArrayList;

public class Game {

    private DeckSpam deckSpam;
    private DeckTrojan deckTrojan;
    private DeckVirus deckVirus;
    private DeckWorm deckWorm;
    private ArrayList<ArrayList<ArrayList<Element>>> map;
    private ArrayList<Player> playerList;

    public Game(ArrayList<Player> playerList){
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
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public ArrayList<ArrayList<ArrayList<Element>>> getMap() {
        return map;
    }
}
