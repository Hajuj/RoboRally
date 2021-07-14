package game.damagecards;

/**
 * @author Ilja Knis
 */
public class Trojan extends game.Card {

    /**
     * Instantiates a new Trojan.
     */
    public Trojan(){
        cardName = "Trojan";
    }

    public boolean isDamageCard() {
        return true;
    }
}
