package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class Checkpoint extends Element {

    public Checkpoint() {
        elementName = "Checkpoint";
        amount = 1;
    }

    public boolean isBlocker() {
        return false;
    }
}
