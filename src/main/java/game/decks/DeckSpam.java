package game.decks;

import game.Card;
import game.Deck;
import game.damagecards.Spam;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja Knis
 */
public class DeckSpam extends Deck {

    private ArrayList<Card> deckSpam;

    public void initializeDeck() {
        this.deckSpam = new ArrayList<Card>();

        for (int i = 0; i < 36; i++){
            deckSpam.add(new Spam());
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.getDeck());
    }

    public ArrayList<Card> getDeck() {
        return deckSpam;
    }

    //TODO method draw card and remove from top of the deck
}
