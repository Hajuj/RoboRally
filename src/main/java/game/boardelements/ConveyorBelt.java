package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class ConveyorBelt extends Element {

    public ConveyorBelt() {
        elementName = "ConveyorBelt";
        amount = 1;
    }

    public boolean isBlocker() {
        return false;
    }
}
