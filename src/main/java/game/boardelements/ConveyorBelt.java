package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class ConveyorBelt extends Element {

    @Expose
    private String elementName;

    public ConveyorBelt(){
        elementName = "ConveyorBelt";
    }

    public boolean isBlocker() {
        return false;
    }
}
