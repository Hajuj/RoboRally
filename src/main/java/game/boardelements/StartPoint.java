package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

/**
 * @author Ilja Knis
 */
public class StartPoint extends Element {

    @Expose
    private String type;
    @Expose
    private String isOnBoard;

    public StartPoint(String type, String isOnBoard){
        this.type = type;
        this.isOnBoard = isOnBoard;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getIsOnBoard() {
        return isOnBoard;
    }

}
