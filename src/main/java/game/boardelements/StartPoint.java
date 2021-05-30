package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

/**
 * @author Ilja Knis
 */
public class StartPoint extends Element {

    @Expose
    private final String type;

    public StartPoint(){
        type = "StartPoint";
    }

    @Override
    public String getType() {
        return type;
    }

    public boolean isBlocker() {
        return false;
    }
}
