package game;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public abstract class Deck {

    private Card topCard;

    public abstract void initializeDeck ();

    public abstract void shuffleDeck ();

    public Card getTopCard () {
        topCard = this.getDeck().get(0);
        return topCard;
    }

    public void removeTopCard(){
        this.getDeck().remove(0);
    }

    public void removeAllCards(){
        while(this.getDeck().size() > 0){
            this.removeTopCard();
        }
    public void removeTopCard () {
        this.getDeck().remove(0);
    }

    public void removeAllCards () {
        while (this.getDeck().size() > 0) {
            this.removeTopCard();
        }
    }

    public abstract ArrayList<Card> getDeck();
}
