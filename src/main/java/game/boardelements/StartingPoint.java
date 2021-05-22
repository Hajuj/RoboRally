package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class StartingPoint extends Element {

    public StartingPoint(){
        elementName = "StartingPoint";
        amount = 1;
    }

    public boolean isBlocker() {
        return false;
    }
}
