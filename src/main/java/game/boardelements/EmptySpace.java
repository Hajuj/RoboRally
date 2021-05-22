package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class EmptySpace extends Element {

    @Expose
    public String elementName;

    public EmptySpace() {
        elementName = "EmptySpace";
    }

    public boolean isBlocker() {
        return false;
    }
}
