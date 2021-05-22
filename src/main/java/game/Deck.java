package game;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public abstract class Deck {

    private Card topCard;

    public abstract void initializeDeck();

    public abstract void shuffleDeck();

    public Card getTopCard(){
        topCard = this.getDeck().get(0);
        return topCard;
    }

    public void removeTopCard(ArrayList<Card> Deck){
        Deck.remove(0);
    }

    protected abstract ArrayList<Card> getDeck();
}
