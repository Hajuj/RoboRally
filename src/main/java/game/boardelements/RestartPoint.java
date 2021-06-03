package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

/**
 * @author Ilja Knis
 */
public class RestartPoint extends Element {

    @Expose
    private final String type;
    @Expose
    private final String isOnBoard;

    public RestartPoint(String type, String isOnBoard){
        this.type = type;
        this.isOnBoard = isOnBoard;
    }

    public String getType() {
        return type;
    }

    public String getIsOnBoard() {
        return isOnBoard;
    }

}
