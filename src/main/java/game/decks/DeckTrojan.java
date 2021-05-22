package game.decks;

import game.Card;
import game.Deck;
import game.damagecards.Trojan;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja Knis
 */
public class DeckTrojan extends Deck {

    private ArrayList<Card> deckTrojan;

    public void initializeDeck() {
        this.deckTrojan = new ArrayList<Card>();

        for(int i = 0; i < 12; i++){
            deckTrojan.add(new Trojan());
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.getDeck());
    }

    protected ArrayList<Card> getDeck() {
        return deckTrojan;
    }
}
