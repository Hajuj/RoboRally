package game.damagecards;

/**
 * @author Ilja Knis
 */
public class Worm extends game.Card {

    /**
     * Instantiates a new Worm.
     */
    public Worm() {
        cardName = "Worm";
    }

    public boolean isDamageCard() {
        return true;
    }
}
