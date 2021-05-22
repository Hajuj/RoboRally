package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class EmptySpace extends Element {

    public EmptySpace() {
        elementName = "EmptySpace";
        amount = 1;
    }

    public boolean isBlocker() {
        return false;
    }
}
