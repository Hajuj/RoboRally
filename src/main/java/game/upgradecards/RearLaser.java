package game.upgradecards;

import game.Card;

public class RearLaser extends Card {

    public RearLaser() {
        cardName = "RearLaser";
    }

    public boolean isDamageCard() {
        return false;
    }

    public boolean isPermanent() {
        return true;
    }
}
