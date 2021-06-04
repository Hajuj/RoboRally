package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class RestartPoint extends Element {

    @Expose
    private final String type;
    @Expose
    private final String isOnBoard;
    @Expose
    private final ArrayList<String> orientations;

    public RestartPoint (String type, String isOnBoard, ArrayList<String> orientations) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    public String getType () {
        return type;
    }

    public String getIsOnBoard () {
        return isOnBoard;
    }

    public ArrayList<String> getOrientations () {
        return orientations;
    }

}
