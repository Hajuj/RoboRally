package game.decks;

import game.Card;
import game.Deck;
import game.damagecards.Virus;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja Knis
 */
public class DeckVirus extends Deck {

    private ArrayList<Card> deckVirus;

    public void initializeDeck() {
        deckVirus = new ArrayList<Card>();

        for(int i = 0; i < 18; i++){
            deckVirus.add(new Virus());
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.getDeck());
    }

    @Override
    public ArrayList<Card> getDeck() {
        return deckVirus;
    }
}
