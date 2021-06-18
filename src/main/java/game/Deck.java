package game;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Ilja Knis
 */
public abstract class Deck {

    private Card topCard;

    public abstract void initializeDeck();

    public abstract void shuffleDeck();

    public Card getTopCard() {
        //TODO Index 0 out of bounds for length 0 -> Ilja please help us :(
        if(this.getDeck().size() > 0) {
            topCard = this.getDeck().get(0);
            return topCard;
        }
        else{
            throw new ClassCastException("Can't draw a card as the deck is empty!");
        }
    }

    protected abstract ArrayList<Card> getDeck();

    public void removeTopCard() {
        this.getDeck().remove(0);
    }

    public void removeAllCards() {
        while (this.getDeck().size() > 0) {
            this.removeTopCard();
        }
    }
}
