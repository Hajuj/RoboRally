package game.damagecards;

/**
 * @author Ilja Knis
 */
public class Virus extends game.Card {

    /**
     * Instantiates a new Virus.
     */
    public Virus() {
        cardName = "Virus";
    }

    public boolean isDamageCard() {
        return true;
    }
}
