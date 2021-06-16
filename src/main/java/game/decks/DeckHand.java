package game.decks;

import game.Card;
import game.Deck;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja Knis
 */
public class DeckHand extends Deck {

    private ArrayList<Card> deckHand;

    public void initializeDeck() {
        this.deckHand = new ArrayList<Card>();
    }

    public void shuffleDeck () {
        Collections.shuffle(this.getDeck());
    }

    public ArrayList<Card> getDeck () {
        return deckHand;
    }

    public ArrayList<String> toArrayList () {
        ArrayList<String> nameList = new ArrayList<>();
        for (int i = 0; i < deckHand.size(); i++) {
            nameList.add(deckHand.get(i).getCardName());
        }
        return nameList;
    }
}
