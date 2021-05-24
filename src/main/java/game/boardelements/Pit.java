package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

/**
 * @author Ilja Knis
 */
public class Pit extends Element {

    @Expose
    private final String type;

    public Pit(){
        this.type = "Pit";
    }

    @Override
    public String getType() {
        return type;
    }

    public boolean isBlocker() {
        return false;
    }
}
