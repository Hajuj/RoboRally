package game.upgradecards;

import game.Card;

public class AdminPrivilege extends Card {

    public AdminPrivilege() {
        cardName = "AdminPrivilege";
    }

    public boolean isDamageCard() {
        return false;
    }

    public boolean isPermanent() {
        return true;
    }
}
