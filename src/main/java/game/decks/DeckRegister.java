package game.decks;

import game.Card;
import game.Deck;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja Knis
 */
public class DeckRegister extends Deck {

    private ArrayList<Card> deckRegister;

    public void initializeDeck() {
        this.deckRegister = new ArrayList<Card>();
        for(int i = 0; i < 5; i++) {
            deckRegister.add(i, null);
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.getDeck());
    }

    public ArrayList<Card> getDeck() {
        return deckRegister;
    }
}
