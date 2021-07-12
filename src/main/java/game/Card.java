package game;

import com.google.gson.annotations.Expose;

/**
 * @author Ilja Knis
 */
public abstract class Card {

    @Expose
    public String cardName;

    public Card() {
        this.cardName = "Card";
    }

    public abstract boolean isDamageCard();

    public String getCardName() {
        return this.cardName;
    }
}