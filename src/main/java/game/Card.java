package game;

/**
 * @author Ilja Knis
 */
public abstract class Card{

    public String cardName;

    public Card() {
        cardName = "Card";
    }

    //TODO: implement input types
    public abstract void activateCard();

    public abstract boolean isDamageCard();

    public String getCardName(){
        return cardName;
    }
}