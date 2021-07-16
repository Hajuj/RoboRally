package game;

import com.google.gson.annotations.Expose;

/**
 * @author Ilja Knis
 */
public abstract class Card {

    @Expose
    public String cardName;

    /**
     * Constructor that initializes the specific
     * Card with this card name.
     */
    public Card() {
        this.cardName = "Card";
    }

    public abstract boolean isDamageCard();

    public String getCardName() {
        return this.cardName;
    }
}