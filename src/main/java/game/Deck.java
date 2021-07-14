package game;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public abstract class Deck {

    private Card topCard;

    /**
     * Method to initialize the specific deck
     * with the correct amount of cards needed.
     */
    public abstract void initializeDeck();

    /**
     * Method to shuffle the deck.
     * Uses Collections sort.
     */
    public abstract void shuffleDeck();

    /**
     * Method to get the top card from the deck.
     * No checker for empty deck -> used at specific locations
     * @return  the top card from the deck
     */
    public Card getTopCard() {
        topCard = this.getDeck().get(0);
        return topCard;
    }

    protected abstract ArrayList<Card> getDeck();

    /**
     * Method to remove the top card from the deck.
     */
    public void removeTopCard() {
        this.getDeck().remove(0);
    }

    public void removeAllCards() {
        while (this.getDeck().size() > 0) {
            this.removeTopCard();
        }
    }
}
