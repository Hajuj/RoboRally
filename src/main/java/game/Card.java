package game;

import com.google.gson.annotations.Expose;

/**
 * @author Ilja Knis
 */
public abstract class Card{

    @Expose
    public String cardName;

    public Card() {
        this.cardName = "Card";
    }

    //TODO: implement input types
    public abstract void activateCard();

    public abstract boolean isDamageCard();

    public String getCardName(){
        return this.cardName;
    }
}