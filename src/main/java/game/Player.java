package game;

import game.decks.*;

/**
 * @author Ilja Knis
 */
public class Player {

    private int playerID;
    private String name;
    private Robot robot;
    private int figure;

    private DeckDiscard deckDiscard;
    private DeckProgramming deckProgramming;
    private DeckHand deckHand;
    private DeckRegister deckRegister;
    private DeckSpam deckSpam;
    private DeckTrojan deckTrojan;
    private DeckVirus deckVirus;
    private DeckWorm deckWorm;


    public Player(int playerID) {
        this.playerID = playerID;

        this.deckDiscard = new DeckDiscard();
        deckDiscard.initializeDeck();
        deckDiscard.shuffleDeck();

        this.deckProgramming = new DeckProgramming();
        deckProgramming.initializeDeck();
        deckProgramming.shuffleDeck();

        this.deckHand = new DeckHand();
        deckHand.initializeDeck();
        deckHand.shuffleDeck();

        this.deckRegister = new DeckRegister();
        deckRegister.initializeDeck();
        deckRegister.shuffleDeck();

        this.deckSpam = new DeckSpam();
        deckSpam.initializeDeck();
        deckSpam.shuffleDeck();

        this.deckTrojan = new DeckTrojan();
        deckTrojan.initializeDeck();
        deckTrojan.shuffleDeck();

        this.deckVirus = new DeckVirus();
        deckVirus.initializeDeck();
        deckVirus.shuffleDeck();

        this.deckWorm = new DeckWorm();
        deckWorm.initializeDeck();
        deckWorm.shuffleDeck();
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public DeckDiscard getDeckDiscard() {
        return deckDiscard;
    }

    public int getFigure() {
        return figure;
    }

    public Robot getRobot() {
        return robot;
    }

    //TODO pick robot + player
    public void foo(int figure, String name) {
        this.figure = figure;
        this.name = name;
    }
}
