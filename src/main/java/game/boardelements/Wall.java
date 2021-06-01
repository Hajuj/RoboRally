package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Wall extends Element {

    @Expose
    private String type;
    @Expose
    private String isOnBoard;
    @Expose
    private ArrayList<String> orientations;

    public Wall(String type, String isOnBoard, ArrayList<String> orientations) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getIsOnBoard() {
        return isOnBoard;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }
}
