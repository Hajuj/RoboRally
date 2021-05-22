package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class PitFall extends Element {

    @Expose
    public String elementName;

    public PitFall(){
        elementName = "PitFall";
    }

    public boolean isBlocker() {
        return false;
    }
}
