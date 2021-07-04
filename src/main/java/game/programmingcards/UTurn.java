package game.programmingcards;

import game.Card;

/**
 * @author Ilja Knis
 */
public class UTurn extends Card {

    public UTurn(){
        cardName = "UTurn";
    }

    public boolean isDamageCard() {
        return false;
    }
}