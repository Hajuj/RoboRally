package game.upgradecards;

import game.Card;

public class MemorySwap extends Card {

    public MemorySwap(){
        cardName = "MemorySwap";
    }

    public boolean isDamageCard() {
        return false;
    }

    public boolean isPermanent(){
        return false;
    }
}
