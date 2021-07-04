package game.programmingcards;

import game.Card;

/**
 * @author Ilja Knis
 */
public class TurnRight extends Card {

    public TurnRight(){
        cardName = "TurnRight";
    }

    public boolean isDamageCard() {
        return false;
    }
}