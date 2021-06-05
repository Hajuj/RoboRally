package game;

import com.google.gson.annotations.Expose;

/**
 * @author Ilja Knis
 */
public abstract class Card{

    @Expose
    private String cardName;

    public Card() {
        cardName = "Card";
    }

    //TODO: implement input types
    public abstract void activateCard();

    public abstract boolean isDamageCard();

    public String getCardName(){
        return this.cardName;
    }
}