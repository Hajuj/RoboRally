package game.programmingcards;

import game.Card;

/**
 * @author Ilja Knis
 */
public class Again extends Card {
    public Again(){
        this.cardName = "Again";
    }

    public boolean isDamageCard() {
        return false;
    }
}
