package game.programmingcards;

import game.Card;

/**
 * @author Ilja Knis
 */
public class PowerUp extends Card {
    public PowerUp(){
        cardName = "PowerUp";
    }

    public boolean isDamageCard() {
        return false;
    }
}