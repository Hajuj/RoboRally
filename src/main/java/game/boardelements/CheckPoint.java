package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;


/**
 * @author Ilja Knis
 */
public class CheckPoint extends Element {

    @Expose
    private int count;

    public CheckPoint(String type, String isOnBoard, int count) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public String getIsOnBoard() {
        return isOnBoard;
    }

}
