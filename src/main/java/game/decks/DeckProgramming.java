package game.decks;

import game.Card;
import game.Deck;
import game.programmingcards.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja Knis
 */
public class DeckProgramming extends Deck {
    private ArrayList<Card> deckProgramming;

    public void initializeDeck() {

        for (int i = 0; i < 5; i++) {
            this.deckProgramming.add(new MoveI());
        }

        for (int i = 0; i < 3; i++) {
            this.deckProgramming.add(new MoveII());
            this.deckProgramming.add(new TurnLeft());
            this.deckProgramming.add(new TurnRight());
        }

        for (int i = 0; i < 2; i++) {
            this.deckProgramming.add(new Again());
        }

        this.deckProgramming.add(new BackUp());
        this.deckProgramming.add(new UTurn());
        this.deckProgramming.add(new MoveIII());
        this.deckProgramming.add(new PowerUp());
    }

    public void shuffleDeck() {
        Collections.shuffle(this.getDeck());
    }

    public ArrayList<Card> getDeck() {
        return this.deckProgramming;
    }
}
