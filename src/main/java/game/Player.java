package game;

import game.decks.*;

/**
 * @author Ilja Knis
 */
public class Player implements Comparable<Player> {

    private int playerID;
    private String name;
    private Robot robot;
    private int figure;
    private boolean isReady;

    private DeckDiscard deckDiscard;
    private DeckProgramming deckProgramming;
    private DeckHand deckHand;
    private DeckRegister deckRegister;
    private DeckSpam deckSpam;
    private DeckTrojan deckTrojan;
    private DeckVirus deckVirus;
    private DeckWorm deckWorm;

    public Player (int playerID) {
        this.playerID = playerID;
        this.isReady = false;
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

    //TODO shuffle cards
    //TODO draw cards

    public void drawCardsProgramming(int amount){
        int amountLeft;

        //YourCardsBody
        if(amount <= this.deckProgramming.getDeck().size()){
            for(int i = 0; i < amount; i++){
                this.deckHand.getDeck().add(this.deckProgramming.getDeck().get(0));
                this.deckProgramming.getDeck().remove(0);
            }
        }

        //ShuffleCodingBody
        else if(amount > this.deckProgramming.getDeck().size()){
            amountLeft = amount - (this.deckProgramming.getDeck().size());
            for(int i = 0; i < this.deckProgramming.getDeck().size(); i++){
                this.deckHand.getDeck().add(this.deckProgramming.getTopCard());
                this.deckProgramming.removeTopCard();
            }
            shuffleDiscardIntoProgramming();
            for(int i = 0; i < amountLeft; i++){
                this.deckHand.getDeck().add(this.deckProgramming.getTopCard());
                this.deckProgramming.removeTopCard();
            }
        }
    }

    private void shuffleDiscardIntoProgramming() {
        this.deckProgramming.getDeck().addAll(this.deckDiscard.getDeck());
        this.deckDiscard.removeAllCards();
        this.deckProgramming.shuffleDeck();
    }

    @Override
    public int compareTo(Player o) {
        return Integer.compare(this.getPlayerID(), o.getPlayerID());
    }

    public void pickRobot (int figure, String name) {
        this.figure = figure;
        this.name = name;
    }

    public int getPlayerID () {
        return playerID;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    public DeckDiscard getDeckDiscard() {
        return deckDiscard;
    }

    public DeckHand getDeckHand() {
        return deckHand;
    }

//    public DeckProgramming getDeckProgramming() {
//        return deckProgramming;
//    }

    public DeckRegister getDeckRegister() {
        return deckRegister;
    }

    public DeckSpam getDeckSpam() {
        return deckSpam;
    }

    public DeckTrojan getDeckTrojan() {
        return deckTrojan;
    }

    public DeckVirus getDeckVirus() {
        return deckVirus;
    }

    public DeckWorm getDeckWorm() {
        return deckWorm;
    }

    public int getFigure() {
        return figure;
    }

    public void setFigure(int figure) {
        this.figure = figure;
    }

    public Robot getRobot () {
        return robot;
    }

    public void setRobot (Robot robot) {
        this.robot = robot;
    }

    public boolean isReady () {
        return isReady;
    }

    public void setReady (boolean ready) {
        isReady = ready;
    }

}
