package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

/**
 * @author Ilja Knis
 */
public class Wall extends Element {

    @Expose
    private final String type;

    public Wall() {
        type = "Wall";
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isBlocker() {
        return true;
    }

}
