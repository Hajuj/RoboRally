package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

/**
 * @author Ilja Knis
 */
public class EnergySpace extends Element {

    @Expose
    private String type;
    @Expose
    private String isOnBoard;
    @Expose
    private int count;

    public EnergySpace(String type, String isOnBoard, int count){
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

    public int getCount() {
        return count;
    }
}
