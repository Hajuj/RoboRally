package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class Empty extends Element {

    @Expose
    private final String type;

    public Empty() {
        this.type = "Empty";
    }

    public String getType() {
        return type;
    }

    public boolean isBlocker() {
        return false;
    }
}
