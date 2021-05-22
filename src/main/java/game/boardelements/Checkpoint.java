package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class Checkpoint extends Element {

    @Expose
    public String elementName;

    public Checkpoint() {
        elementName = "Checkpoint";
    }

    public boolean isBlocker() {
        return false;
    }
}
