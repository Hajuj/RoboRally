package game.upgradecards;

import game.Card;

public class MemorySwap extends Card {

    public MemorySwap(){
        cardName = "MemorySwap";
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
