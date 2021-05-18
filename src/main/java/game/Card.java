package game;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public abstract class Card{

    public String cardName;

    public Card() {
        cardName = "Card";
    }

    public String getCardName(){
        return cardName;
    }
}