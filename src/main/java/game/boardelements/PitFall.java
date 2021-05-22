package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class PitFall extends Element {

    public PitFall(){
        elementName = "PitFall";
        amount = 1;
    }

    public boolean isBlocker() {
        return false;
    }
}
