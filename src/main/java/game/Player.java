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
    Server server = Server.getInstance();

    private int playerID;
    private String name;
    private Robot robot;
    private int figure;
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
    }

    //TODO shuffle cards
    //TODO draw cards
    public boolean isRegisterFull() {
        int count = 0;
        for (Card card : this.getDeckRegister().getDeck()) {
            if (card != null) {
                count++;
            }
        }
        return (count == 5);
    }

    public ArrayList<String> drawBlind() {
        //TODO check when cards are discarded
        discardHandCards();
        ArrayList<String> newCard = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (this.getDeckRegister().getDeck().get(i) == null) {
                Card card = drawRegisterCards();
                if (card.cardName.equals("Again") && i == 0) {
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

    //TODO fix method -> not working correctly?
    public Card drawRegisterCards() {
        //YourCardsBody
        if (this.deckProgramming.getDeck().size() > 0) {
            Card card = this.deckProgramming.getTopCard();
            this.deckProgramming.removeTopCard();
            return card;
        }

        //When there is no enough cards
        else {
            shuffleDiscardIntoProgramming();
            Card card = this.deckProgramming.getTopCard();
            this.deckProgramming.removeTopCard();

            JSONMessage shuffleMessage = new JSONMessage("ShuffleCoding", new ShuffleCodingBody(playerID));
            server.getCurrentGame().sendToAllPlayers(shuffleMessage);
            return card;
        }
    }

    public void discardHandCards() {
        for (int i = 0; i < this.deckHand.getDeck().size(); i++) {
            this.deckDiscard.getDeck().add(this.deckHand.getDeck().get(i));
        }
        this.deckHand.getDeck().clear();
    }

    public void discardRegisterCards() {
        for (int i = 0; i < this.deckRegister.getDeck().size(); i++) {
            this.deckDiscard.getDeck().add(this.deckRegister.getDeck().get(i));
            this.deckRegister.getDeck().set(i, null);
        }
    }

    public void drawCardsProgramming(int amount) {
        int amountLeft;
        //YourCardsBody
        //If there is enough cards in deckProgramming
        if (amount <= this.deckProgramming.getDeck().size()) {
            for (int i = 0; i < amount; i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getDeck().get(0));
                this.deckProgramming.getDeck().remove(0);
            }
        }

        //ShuffleCodingBody
        //If there is not enough cards in deckProgramming
        else if (amount > this.deckProgramming.getDeck().size()) {
            amountLeft = amount - (this.deckProgramming.getDeck().size());

            for (int i = 0; i < this.deckProgramming.getDeck().size(); i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getDeck().get(i));
            }
            this.deckProgramming.getDeck().clear();
            shuffleDiscardIntoProgramming();
            for (int i = 0; i < amountLeft; i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getTopCard());
                this.deckProgramming.removeTopCard();
            }
            JSONMessage shuffleMessage = new JSONMessage("ShuffleCoding", new ShuffleCodingBody(playerID));
            server.getCurrentGame().sendToAllPlayers(shuffleMessage);
        }
    }

    public void shuffleDiscardIntoProgramming() {
        this.deckProgramming.getDeck().addAll(this.deckDiscard.getDeck());
        this.deckDiscard.getDeck().clear();
        this.deckProgramming.shuffleDeck();
    }

    public Card removeSelectedCard(String card) {
        for (Card card1 : this.deckHand.getDeck()) {
            if (card.equals(card1.getCardName())) {
                deckHand.getDeck().remove(card1);
                return card1;
            }
        }
        return null;
    }

    public void pickRobot(int figure, String name) {
        this.figure = figure;
        this.name = name;
    }

    public boolean checkAdmin() {
        if (numberOfAdminPrivilege >= activeAdminPrivilege + 1) {
            activeAdminPrivilege++;
            return true;
        } else {
            return false;
        }
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

    public DeckHand getDeckHand() {
        return deckHand;
    }

    public DeckProgramming getDeckProgramming() {
        return deckProgramming;
    }

    public DeckRegister getDeckRegister() {
        return deckRegister;
    }

    public int getFigure() {
        return figure;
    }

    public void setFigure(int figure) {
        this.figure = figure;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public int getEnergy() {
        return energy;
    }

    public void increaseEnergy(int amount) {
        this.energy += amount;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean AI) {
        isAI = AI;
    }


    public ArrayList<Card> getInstalledPermanentUpgrades() {
        return installedPermanentUpgrades;
    }

    public void setInstalledPermanentUpgrades(ArrayList<Card> installedPermanentUpgrades) {
        this.installedPermanentUpgrades = installedPermanentUpgrades;
    }

    public ArrayList<Card> getTemporaryUpgrades() {
        return temporaryUpgrades;
    }

    public void setTemporaryUpgrades(ArrayList<Card> temporaryUpgrades) {
        this.temporaryUpgrades = temporaryUpgrades;
    }

    public int getNumberOfAdminPrivilege() {
        return numberOfAdminPrivilege;
    }

    public void setNumberOfAdminPrivilege(int numberOfAdminPrivilege) {
        this.numberOfAdminPrivilege = numberOfAdminPrivilege;
    }

    public int getActiveAdminPrivilege() {
        return activeAdminPrivilege;
    }

    public void setActiveAdminPrivilege(int activeAdminPrivilege) {
        this.activeAdminPrivilege = activeAdminPrivilege;
    }
}
