package game.decks;

import game.Card;
import game.Deck;
import game.damagecards.Worm;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja Knis
 */
public class DeckWorm extends Deck {

    private ArrayList<Card> deckWorm;

    public void initializeDeck() {
        deckWorm = new ArrayList<Card>();

        for (int i = 0; i < 6; i++) {
            deckWorm.add(new Worm());
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.getDeck());
    }

    public ArrayList<Card> getDeck() {
        return deckWorm;
    }
}
