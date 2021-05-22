package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class StartingPoint extends Element {

    @Expose
    public String elementName;

    public StartingPoint(){
        elementName = "StartingPoint";
    }

    public boolean isBlocker() {
        return false;
    }
}
