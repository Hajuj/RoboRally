package game.decks;

import game.Card;
import game.Deck;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja Knis
 */
public class DeckDiscard extends Deck {

    public ArrayList<Card> deckDiscard;

    public void initializeDeck() {
        this.deckDiscard = new ArrayList<Card>();
    }

    public void shuffleDeck() {
        Collections.shuffle(this.getDeck());
    }

    public ArrayList<Card> getDeck() {
        return deckDiscard;
    }

    //TODO implement method to draw from discard deck
}
