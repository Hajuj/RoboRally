package game;

import game.boardelements.Antenna;
import game.decks.*;
import javafx.geometry.Point2D;
import json.JSONMessage;
import json.protocol.*;

//import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ilja Knis
 */
public class Player {
    Game game = Game.getInstance();

    private int playerID;
    private String name;
    private Robot robot;
    private int figure;
    private boolean isReady;
    private int energy;

    private DeckDiscard deckDiscard;
    private DeckProgramming deckProgramming;
    private DeckHand deckHand;
    private DeckRegister deckRegister;

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
        discardCards();
        ArrayList<String> newCard = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (this.getDeckRegister().getDeck().get(i) == null) {
                Card card = drawRegisterCards();
                newCard.add(card.getCardName());
                this.getDeckRegister().getDeck().set(i, card);
            }
        }
        return newCard;
    }

    //TODO fix method -> not working correctly?
    public Card drawRegisterCards() {
        //YourCardsBody
        if (this.deckProgramming.getDeck().size() > 0) {
            Card card = this.deckProgramming.getDeck().get(0);
            this.deckProgramming.getDeck().remove(0);
            return card;
        }

        //When there is no enough cards
        else {
            shuffleDiscardIntoProgramming();
            Card card = this.deckProgramming.getDeck().get(0);
            this.deckProgramming.getDeck().remove(0);

            JSONMessage shuffleMessage = new JSONMessage("ShuffleCoding", new ShuffleCodingBody(playerID));
            game.sendToAllPlayers(shuffleMessage);

            return card;
        }
    }

    public void discardCards() {
        for (Card card : this.deckHand.getDeck()) {
            this.deckDiscard.getDeck().add(card);
            this.deckHand.getDeck().remove(card);
        }
    }

    public void drawCardsProgramming(int amount) {
        int amountLeft;

        //YourCardsBody
        if (amount <= this.deckProgramming.getDeck().size()) {
            for (int i = 0; i < amount; i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getDeck().get(0));
                this.deckProgramming.getDeck().remove(0);
            }
        }

        //ShuffleCodingBody
        else if (amount > this.deckProgramming.getDeck().size()) {
            amountLeft = amount - (this.deckProgramming.getDeck().size());
            for (int i = 0; i < this.deckProgramming.getDeck().size(); i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getTopCard());
                this.deckProgramming.removeTopCard();
            }
            shuffleDiscardIntoProgramming();
            for (int i = 0; i < amountLeft; i++) {
                this.deckHand.getDeck().add(this.deckProgramming.getTopCard());
                this.deckProgramming.removeTopCard();
            }
            JSONMessage shuffleMessage = new JSONMessage("ShuffleCoding", new ShuffleCodingBody(playerID));
            game.sendToAllPlayers(shuffleMessage);
        }
    }

    private void shuffleDiscardIntoProgramming() {
        this.deckProgramming.getDeck().addAll(this.deckDiscard.getDeck());
        this.deckDiscard.removeAllCards();
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

    public void increaseEnergy(int amount){
        this.energy += amount;
    }

    public void decreaseEnergy(int amount){
        if(this.energy < amount){
            //TODO can't use if not enough energy
            this.energy = 0;
        }
        else {
            this.energy -= amount;
        }
    }
}
