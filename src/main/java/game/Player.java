package game;

import game.decks.*;
import json.JSONMessage;
import json.protocol.*;
import server.Server;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Player {
    /**
     * The Server.
     */
    Server server = Server.getInstance();

    private int playerID;
    private String name;
    private Robot robot;
    private int figure = -1;
    private boolean isReady;
    private int energy = 5;
    private boolean isAI = false;

    private DeckDiscard deckDiscard;
    private DeckProgramming deckProgramming;
    private DeckHand deckHand;
    private DeckRegister deckRegister;
    private ArrayList<Card> installedPermanentUpgrades;
    private ArrayList<Card> temporaryUpgrades;

    private int numberOfAdminPrivilege = 0;
    private int activeAdminPrivilege = 0;

    /**
     * Constructor for a new player.
     * Creates a new player with corresponding decks.
     *
     * @param playerID is the player ID the player has at the server
     */
    public Player(int playerID) {
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

        this.installedPermanentUpgrades = new ArrayList<>();
        this.temporaryUpgrades = new ArrayList<>();
    }

    /**
     * Method to refresh all decks of a player.
     */
    public void refreshPlayer() {
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

        this.installedPermanentUpgrades = new ArrayList<>();
        this.temporaryUpgrades = new ArrayList<>();

        this.activeAdminPrivilege = 0;
        this.numberOfAdminPrivilege = 0;
        this.energy = 5;
    }

    /**
     * Method to check if all registers are full.
     *
     * @return true if all registers are full
     */
    public boolean isRegisterFull() {
        int count = 0;
        for(Card card : this.getDeckRegister().getDeck()) {
            if(card != null) {
                count++;
            }
        }
        return (count == 5);
    }

    /**
     * Method to draw cards from the deck into empty registers.
     * Discards current hand. Then draws new cards into
     * empty registers. Doesn't draw "Again" into first register.
     *
     * @return an ArrayList of drawn cards
     */
    public ArrayList<String> drawBlind() {
        discardHandCards();
        ArrayList<String> newCard = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            if(this.getDeckRegister().getDeck().get(i) == null) {
                Card card = drawRegisterCards();
                if(card.cardName.equals("Again") && i == 0) {
                    this.getDeckDiscard().getDeck().add(card);
                    i--;
                } else {
                    newCard.add(card.getCardName());
                    this.getDeckRegister().getDeck().set(i, card);
                }
            }
        }
        return newCard;
    }

    /**
     * Method to draw a new card for registers.
     * Shuffles deckDiscard into deckProgramming, if deckProgramming
     * is empty.
     *
     * @return a new drawn card
     */
    public Card drawRegisterCards() {
        if(this.deckProgramming.getDeck().size() > 0) {
            Card card = this.deckProgramming.getTopCard();
            this.deckProgramming.removeTopCard();
            return card;
        } else {
            shuffleDiscardIntoProgramming();
            Card card = this.deckProgramming.getTopCard();
            this.deckProgramming.removeTopCard();

            JSONMessage shuffleMessage = new JSONMessage("ShuffleCoding", new ShuffleCodingBody(playerID));
            server.getCurrentGame().sendToAllPlayers(shuffleMessage);
            return card;
        }
    }

    /**
     * Method to discard all cards from deckHand into deckDiscard.
     */
    public void discardHandCards() {
        for(int i = 0; i < this.deckHand.getDeck().size(); i++) {
            this.deckDiscard.getDeck().add(this.deckHand.getDeck().get(i));
        }
        this.deckHand.getDeck().clear();
    }

    /**
     * Method to discard all cards from deckRegister into deckDiscard.
     */
    public void discardRegisterCards() {
        for(int i = 0; i < this.deckRegister.getDeck().size(); i++) {
            this.deckDiscard.getDeck().add(this.deckRegister.getDeck().get(i));
            this.deckRegister.getDeck().set(i, null);
        }
    }

    /**
     * Method to draw new cards from the deck into the hand.
     * Checks if there are enough cards in the programming deck to draw.
     * If there is not enough cards left -> draws the rest of the deck,
     * shuffles deckDiscard into deckProgramming and draws the rest
     * of the needed cards.
     *
     * @param amount is the amount of cards you need to draw
     */
    public void drawCardsProgramming(int amount) {
        int amountLeft;
        if(amount <= this.deckProgramming.getDeck().size()) {
            for(int i = 0; i < amount; i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getDeck().get(0));
                this.deckProgramming.getDeck().remove(0);
            }
        } else if(amount > this.deckProgramming.getDeck().size()) {
            amountLeft = amount - (this.deckProgramming.getDeck().size());

            for(int i = 0; i < this.deckProgramming.getDeck().size(); i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getDeck().get(i));
            }
            this.deckProgramming.getDeck().clear();
            shuffleDiscardIntoProgramming();
            for(int i = 0; i < amountLeft; i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getTopCard());
                this.deckProgramming.removeTopCard();
            }
            JSONMessage shuffleMessage = new JSONMessage("ShuffleCoding", new ShuffleCodingBody(playerID));
            server.getCurrentGame().sendToAllPlayers(shuffleMessage);
        }
    }

    /**
     * Method to shuffle deckDiscard into deckProgramming.
     */
    public void shuffleDiscardIntoProgramming() {
        this.deckProgramming.getDeck().addAll(this.deckDiscard.getDeck());
        this.deckDiscard.getDeck().clear();
        this.deckProgramming.shuffleDeck();
    }

    /**
     * Method to select and remove a card from hand.
     *
     * @param card is the card one selects
     * @return the removed card
     */
    public Card removeSelectedCard(String card) {
        for(Card card1 : this.deckHand.getDeck()) {
            if(card.equals(card1.getCardName())) {
                deckHand.getDeck().remove(card1);
                return card1;
            }
        }
        return null;
    }

    /**
     * Method to assign a robot to a player
     *
     * @param figure is the robot
     * @param name   is the username
     */
    public void pickRobot(int figure, String name) {
        this.figure = figure;
        this.name = name;
    }

    /**
     * Check admin boolean.
     *
     * @return the boolean
     */
    public boolean checkAdmin() {
        if(numberOfAdminPrivilege >= activeAdminPrivilege + 1) {
            activeAdminPrivilege++;
            return true;
        } else {
            return false;
        }
    }


    /**
     * Gets player id.
     *
     * @return the player id
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets deck discard.
     *
     * @return the deck discard
     */
    public DeckDiscard getDeckDiscard() {
        return deckDiscard;
    }

    /**
     * Gets deck hand.
     *
     * @return the deck hand
     */
    public DeckHand getDeckHand() {
        return deckHand;
    }

    /**
     * Gets deck programming.
     *
     * @return the deck programming
     */
    public DeckProgramming getDeckProgramming() {
        return deckProgramming;
    }

    /**
     * Gets deck register.
     *
     * @return the deck register
     */
    public DeckRegister getDeckRegister() {
        return deckRegister;
    }

    /**
     * Gets figure.
     *
     * @return the figure
     */
    public int getFigure() {
        return figure;
    }

    /**
     * Sets figure.
     *
     * @param figure the figure
     */
    public void setFigure(int figure) {
        this.figure = figure;
    }

    /**
     * Gets robot.
     *
     * @return the robot
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * Sets robot.
     *
     * @param robot the robot
     */
    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    /**
     * Is ready boolean.
     *
     * @return the boolean
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Sets ready.
     *
     * @param ready the ready
     */
    public void setReady(boolean ready) {
        isReady = ready;
    }

    /**
     * Gets energy.
     *
     * @return the energy
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * Increase energy.
     *
     * @param amount the amount
     */
    public void increaseEnergy(int amount) {
        this.energy += amount;
    }

    /**
     * Is ai boolean.
     *
     * @return the boolean
     */
    public boolean isAI() {
        return isAI;
    }

    /**
     * Sets ai.
     *
     * @param AI the ai
     */
    public void setAI(boolean AI) {
        isAI = AI;
    }


    /**
     * Gets installed permanent upgrades.
     *
     * @return the installed permanent upgrades
     */
    public ArrayList<Card> getInstalledPermanentUpgrades() {
        return installedPermanentUpgrades;
    }

    /**
     * Sets installed permanent upgrades.
     *
     * @param installedPermanentUpgrades the installed permanent upgrades
     */
    public void setInstalledPermanentUpgrades(ArrayList<Card> installedPermanentUpgrades) {
        this.installedPermanentUpgrades = installedPermanentUpgrades;
    }

    /**
     * Gets temporary upgrades.
     *
     * @return the temporary upgrades
     */
    public ArrayList<Card> getTemporaryUpgrades() {
        return temporaryUpgrades;
    }

    /**
     * Sets temporary upgrades.
     *
     * @param temporaryUpgrades the temporary upgrades
     */
    public void setTemporaryUpgrades(ArrayList<Card> temporaryUpgrades) {
        this.temporaryUpgrades = temporaryUpgrades;
    }

    /**
     * Gets number of admin privilege.
     *
     * @return the number of admin privilege
     */
    public int getNumberOfAdminPrivilege() {
        return numberOfAdminPrivilege;
    }

    /**
     * Sets number of admin privilege.
     *
     * @param numberOfAdminPrivilege the number of admin privilege
     */
    public void setNumberOfAdminPrivilege(int numberOfAdminPrivilege) {
        this.numberOfAdminPrivilege = numberOfAdminPrivilege;
    }

    /**
     * Gets active admin privilege.
     *
     * @return the active admin privilege
     */
    public int getActiveAdminPrivilege() {
        return activeAdminPrivilege;
    }

    /**
     * Sets active admin privilege.
     *
     * @param activeAdminPrivilege the active admin privilege
     */
    public void setActiveAdminPrivilege(int activeAdminPrivilege) {
        this.activeAdminPrivilege = activeAdminPrivilege;
    }
}
