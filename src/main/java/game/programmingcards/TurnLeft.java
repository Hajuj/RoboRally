package game.programmingcards;

import game.Card;

/**
 * @author Ilja Knis
 */
public class TurnLeft extends Card {

    public TurnLeft() {
        cardName = "TurnLeft";
    }

    public boolean isDamageCard() {
        return false;
    }
}