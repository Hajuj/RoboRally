package game.programmingcards;

import game.Card;

/**
 * @author Ilja Knis
 */
public class PowerUp extends Card {

    public PowerUp(){
        cardName = "PowerUp";
    }

    public void activateCard() {
        //TODO implement card effect
    }

    public boolean isDamageCard() {
        return false;
    }
}