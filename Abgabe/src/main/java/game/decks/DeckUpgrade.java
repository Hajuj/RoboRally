package game.decks;

import game.Card;
import game.Deck;
import game.upgradecards.AdminPrivilege;
import game.upgradecards.MemorySwap;
import game.upgradecards.RearLaser;
import game.upgradecards.SpamBlocker;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ilja
 */
public class DeckUpgrade extends Deck {
    ArrayList<Card> deckUpgrade = new ArrayList<>();

    public void initializeDeck() {
        for(int i = 0; i < 10; i++) {
            this.deckUpgrade.add(new AdminPrivilege());
            this.deckUpgrade.add(new MemorySwap());
            this.deckUpgrade.add(new RearLaser());
            this.deckUpgrade.add(new SpamBlocker());
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.deckUpgrade);
    }

    public ArrayList<Card> getDeck() {
        return this.deckUpgrade;
    }
}
