package game.upgradecards;

import game.Card;

public class SpamBlocker extends Card {

    public SpamBlocker(){
        cardName = "SpamBlocker";
    }

    public void activateCard() {

    }

    public boolean isDamageCard() {
        return false;
    }

    public boolean isPermanent(){
        return false;
    }
}
