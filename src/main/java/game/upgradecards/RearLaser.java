package game.upgradecards;

import game.Card;

public class RearLaser extends Card {

    public RearLaser(){
        cardName = "RearLaser";
    }

    public void activateCard() {

    }

    public boolean isDamageCard() {
        return false;
    }

    public boolean isPermanent(){
        return true;
    }
}
