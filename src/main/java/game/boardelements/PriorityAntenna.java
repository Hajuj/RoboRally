package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class PriorityAntenna extends Element {

    @Expose
    public String elementName;

    public PriorityAntenna() {
        elementName = "PriorityAntenna";
    }

    //TODO find out if antenna should function as a blocker
    public boolean isBlocker() {
        return false;
    }
}
