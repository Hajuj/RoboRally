package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;
import game.Orientation;

public class ConveyorBelt extends Element {

    @Expose
    public Orientation orientation;

    public ConveyorBelt() {
        elementName = "ConveyorBelt";
        amount = 1;
    }

    public boolean isBlocker() {
        return false;
    }

    public void setOrientation(Orientation orientation){
        this.orientation = orientation;
    }

    public Orientation getOrientation(){
        return orientation;
    }
}
