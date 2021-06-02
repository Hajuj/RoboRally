package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

/**
 * @author Ilja Knis
 */
public class Pit extends Element {

    @Expose
    private String type;
    @Expose
    private String isOnBoard;

    public Pit(String type, String isOnBoard){
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
