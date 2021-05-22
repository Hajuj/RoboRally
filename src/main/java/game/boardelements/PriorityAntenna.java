package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class PriorityAntenna extends Element {

    public PriorityAntenna() {
        elementName = "PriorityAntenna";
        amount = 1;
    }

    //TODO find out if antenna should function as a blocker
    public boolean isBlocker() {
        return false;
    }
}
