package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Laser extends Element {

    @Expose
    private String type;
    @Expose
    private String isOnBoard;
    @Expose
    private ArrayList<String> orientations;
    @Expose
    private int count;

    public Laser(String type, String isOnBoard, ArrayList<String> orientations, int count){
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
        this.count = count;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }

    public int getCount() {
        return count;
    }

    public String getType() {
        return type;
    }

    public String getIsOnBoard() {
        return isOnBoard;
    }

}
